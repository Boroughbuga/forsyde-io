package forsyde.io.java.adapters.amalthea;

import forsyde.io.java.adapters.EquivalenceModel2ModelMixin;
import forsyde.io.java.core.EdgeTrait;
import forsyde.io.java.core.ForSyDeSystemGraph;
import forsyde.io.java.core.Vertex;
import forsyde.io.java.core.VertexTrait;
import forsyde.io.java.typed.viewers.execution.*;
import forsyde.io.java.typed.viewers.execution.Channel;
import forsyde.io.java.typed.viewers.execution.Task;
import forsyde.io.java.typed.viewers.impl.InstrumentedExecutable;
import forsyde.io.java.typed.viewers.impl.InstrumentedExecutableViewer;
import forsyde.io.java.typed.viewers.visualization.GreyBox;
import org.eclipse.app4mc.amalthea.model.*;
import org.eclipse.app4mc.amalthea.model.PeriodicStimulus;

import java.lang.System;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface AmaltheaSW2ForSyDeMixin extends EquivalenceModel2ModelMixin<INamed, Vertex> {

    default void fromSWToForSyDe(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        if (amalthea.getSwModel() != null) {
            convertActivations(amalthea, forSyDeSystemGraph);
            fromLabelToVertex(amalthea, forSyDeSystemGraph);
            fromRunnableToVertex(amalthea, forSyDeSystemGraph);
            fromTaskToVertex(amalthea, forSyDeSystemGraph);
            //convertProcessPrototype(amalthea, forSyDeSystemGraph);
        }
    }

    default void fromRunnableToVertex(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        amalthea.getSwModel().getRunnables().forEach(runnable -> {
            final Vertex runnableVertex = new Vertex(runnable.getName(), VertexTrait.IMPL_EXECUTABLE, VertexTrait.VISUALIZATION_VISUALIZABLE);
            forSyDeSystemGraph.addVertex(runnableVertex);
            if (runnable.getRunnableItems() != null) {
                runnable.getRunnableItems().forEach(item -> {
                    // if it is read or write
                    if (item instanceof LabelAccess) {
                        final LabelAccess labelAccess = (LabelAccess) item;
                        // assume that the label creation is completed
                        equivalent(((LabelAccess) item).getData()).ifPresent(labelVertex -> {
                            runnableVertex.ports.add(((LabelAccess) item).getData().getName());
                            switch (labelAccess.getAccess()) {
                                case READ:
                                    forSyDeSystemGraph.connect(
                                            labelVertex,
                                            runnableVertex,
                                            null,
                                            ((LabelAccess) item).getData().getName(),
                                            EdgeTrait.EXECUTION_COMMUNICATIONEDGE,
                                            EdgeTrait.VISUALIZATION_VISUALCONNECTION
                                    );
                                    break;
                                case WRITE:
                                    forSyDeSystemGraph.connect(
                                            runnableVertex,
                                            labelVertex,
                                            ((LabelAccess) item).getData().getName(),
                                            EdgeTrait.EXECUTION_COMMUNICATIONEDGE,
                                            EdgeTrait.VISUALIZATION_VISUALCONNECTION
                                    );
                                    break;
                            }
                        });
                    }
                    // if it is an execution through "ticks"
                    else if (item instanceof Ticks) {
                        final Ticks ticks = (Ticks) item;
                        runnableVertex.addTraits(VertexTrait.IMPL_INSTRUMENTEDEXECUTABLE);
                        final InstrumentedExecutable instrumentedExecutable = new InstrumentedExecutableViewer(runnableVertex);
                        if (ticks.getDefault() != null) {
                            instrumentedExecutable.setOperationRequirements(
                                    Map.of("defaultTicks", Map.of("tick", ticks.getDefault().getUpperBound()))
                            );
                        } else if (ticks.getExtended() != null) {
                            instrumentedExecutable.setOperationRequirements(
                                ticks.getExtended().stream().collect(Collectors.toMap(
                                        entry -> entry.getKey().getName(),
                                        entry -> Map.of("ticks", entry.getValue().getUpperBound())
                                ))
                            );
                        }
                    }
                    // finally, if it is the executio needs
                    else if (item instanceof ExecutionNeed) {
                        runnableVertex.addTraits(VertexTrait.IMPL_INSTRUMENTEDEXECUTABLE);
                        final InstrumentedExecutable instrumentedExecutable = new InstrumentedExecutableViewer(runnableVertex);
                        final ExecutionNeed executionNeed = (ExecutionNeed) item;
                        if (executionNeed.getNeeds() != null) {
                            instrumentedExecutable.setOperationRequirements(
                                Map.of("defaultNeeds",
                                    executionNeed.getNeeds().stream().collect(Collectors.toMap(
                                            entry -> entry.getKey(),
                                            entry -> entry.getValue().getUpperBound()
                                    ))
                                )
                            );
                        }
                    }
                });
            }
            if (runnable.getSize() != null) {
                runnableVertex.addTraits(VertexTrait.IMPL_INSTRUMENTEDEXECUTABLE);
                final InstrumentedExecutable instrumentedExecutable = new InstrumentedExecutableViewer(runnableVertex);
                instrumentedExecutable.setSizeInBits(runnable.getSize().getNumberBits());
            }
            // make sure that if its instrumented, it has some default value until
            // ForSyDe IO uses defaults values properly
            else if (InstrumentedExecutable.conforms(runnableVertex)) {
                final InstrumentedExecutable instrumentedExecutable = new InstrumentedExecutableViewer(runnableVertex);
                instrumentedExecutable.setSizeInBits(0L);
            }
            addEquivalence(runnable, runnableVertex);
        });
    }

    default void fromTaskToVertex(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        amalthea.getSwModel().getTasks().forEach(task -> {
            final Vertex taskVertex = new Vertex(task.getName(), VertexTrait.EXECUTION_TASK);
            final GreyBox taskGreyBox = GreyBox.enforce(taskVertex);
            forSyDeSystemGraph.addVertex(taskVertex);
            taskVertex.ports.add("callSequence");
            final Task fTask = new TaskViewer(taskVertex);
            if (task.getStimuli() != null) {
                task.getStimuli().forEach(stimulus -> {
                    if (stimulus instanceof PeriodicStimulus) {
                        taskVertex.addTraits(VertexTrait.EXECUTION_PERIODICTASK);
                        taskVertex.ports.add("periodicStimulus");
                        equivalent(stimulus).ifPresent(stimulusVertex -> {
                            forSyDeSystemGraph.connect(stimulusVertex, taskVertex, "stimulated", "periodicStimulus", EdgeTrait.EXECUTION_EVENTEDGE);
                        });
                    } else if (stimulus instanceof InterProcessStimulus) {
                        final InterProcessStimulus interProcessStimulus = (InterProcessStimulus) stimulus;
                        final ReactiveTask reactiveTask = ReactiveTask.enforce(taskVertex);
                        // all input reactions are AND semantics coming from InterProcessTrigger
                        equivalents(interProcessStimulus).forEach(reactionVertex -> {
                            forSyDeSystemGraph.connect(reactionVertex, taskVertex, "sucessor", "reactiveStimulus", EdgeTrait.EXECUTION_EVENTEDGE);
                            final List<Integer> reactiveGroups = reactiveTask.getReactiveANDGroups() == null ?
                                new ArrayList<>() :
                                reactiveTask.getReactiveANDGroups();
                            reactiveGroups.add(0);
                            reactiveTask.setReactiveANDGroups(reactiveGroups);
                        });
                    }
                });
            }
            if (task.getActivityGraph() != null) {
                // do tree search due to possible nesting
                task.getActivityGraph().eAllContents().forEachRemaining(item -> {
                    if (item instanceof RunnableCall) {
                        final RunnableCall runnableCall = (RunnableCall) item;
                        equivalent(runnableCall.getRunnable()).ifPresent(runnableVertex -> {
                            forSyDeSystemGraph.connect(taskVertex, runnableVertex, "callSequence", EdgeTrait.EXECUTION_CONTAINMENTEDGE);
                            forSyDeSystemGraph.connect(taskVertex, runnableVertex, "contained", EdgeTrait.VISUALIZATION_VISUALCONTAINMENT);
                        });
                    } else if (item instanceof InterProcessTrigger) {
                        final InterProcessTrigger interProcessTrigger = (InterProcessTrigger) item;
                        equivalents(interProcessTrigger.getStimulus()).forEach(precedenceVertex -> {
                            forSyDeSystemGraph.connect(taskVertex, precedenceVertex, null,"predecessor", EdgeTrait.EXECUTION_EVENTEDGE);
                        });
                    }
                });
            }
            addEquivalence(task, taskVertex);
        });
    }

    default void fromLabelToVertex(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        amalthea.getSwModel().getLabels().forEach(label -> {
            final Vertex channelVertex = new Vertex(label.getName(), VertexTrait.EXECUTION_CHANNEL, VertexTrait.VISUALIZATION_VISUALIZABLE);
            forSyDeSystemGraph.addVertex(channelVertex);
            final Channel channel = new ChannelViewer(channelVertex);
            channel.setMaxElems(1);
            // if the number of bits is not gigantic, it should be OK.
            if (label.getSize() != null) {
                channel.setElemSizeInBits(label.getSize().getNumberBits());
            } else {
                channel.setElemSizeInBits(0L);
            }
            addEquivalence(label, channelVertex);
        });
    }

    default void convertActivations(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        amalthea.getSwModel().getActivations().forEach(activation -> {
            final Vertex timerVertex = new Vertex(activation.getName(), VertexTrait.EXECUTION_PERIODICSTIMULUS);
            final forsyde.io.java.typed.viewers.execution.PeriodicStimulus periodicStimulusVertex =
                    forsyde.io.java.typed.viewers.execution.PeriodicStimulus.enforce(timerVertex);
            forSyDeSystemGraph.addVertex(timerVertex);
            if (activation instanceof PeriodicActivation) {
                final PeriodicActivation periodicActivation = (PeriodicActivation) activation;
                if (periodicActivation.getRecurrence() != null) {
                    periodicStimulusVertex.setPeriodNumerator(periodicActivation.getRecurrence().getValue().longValue());
                    periodicStimulusVertex.setPeriodDenominator(fromTimeUnitToLong(periodicActivation.getRecurrence().getUnit()));
                } else {
                    periodicStimulusVertex.setPeriodNumerator(1L);
                    periodicStimulusVertex.setPeriodDenominator(1L);
                }
                if (periodicActivation.getOffset() != null) {
                    periodicStimulusVertex.setOffsetNumerator(periodicActivation.getOffset().getValue().longValue());
                    periodicStimulusVertex.setOffsetDenominator(fromTimeUnitToLong(periodicActivation.getOffset().getUnit()));
                } else {
                    periodicStimulusVertex.setOffsetNumerator(0L);
                    periodicStimulusVertex.setOffsetDenominator(1L);
                }
                addEquivalence(activation, timerVertex);
            }
        });
    }

//    default void convertProcessPrototype(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
//        amalthea.getSwModel().getProcessPrototypes().forEach(processPrototype ->
//            processPrototype.getOrderPrecedenceSpec().forEach(orderPrecedenceSpec ->
//                equivalent(orderPrecedenceSpec.getOrigin()).ifPresent(originRunnableVertex ->
//                    equivalent(orderPrecedenceSpec.getTarget()).ifPresent(targetRunnableVertex -> {
//                        // first check if any of the created tasks contain this runnable
//                        // otherwise create a forsyde task just for it
//                        final Task originTask = forSyDeSystemGraph.incomingEdgesOf(originRunnableVertex).stream()
//                                .map(forSyDeSystemGraph::getEdgeSource)
//                                .filter(Task::conforms)
//                                .map(Task::enforce)
//                                .findAny()
//                                .orElseGet(() -> {
//                                    final Vertex srcTaskVertex = new Vertex(originRunnableVertex.identifier + "Task",
//                                            VertexTrait.EXECUTION_TASK);
//                                    final Task srcTaskViewer = Task.enforce(srcTaskVertex);
//                                    forSyDeSystemGraph.addVertex(srcTaskVertex);
//                                    forSyDeSystemGraph.connect(srcTaskVertex, originRunnableVertex, "callSequence", EdgeTrait.EXECUTION_CONTAINMENTEDGE);
//                                    return srcTaskViewer;
//                                });
//                        // first check if any of the created tasks contain this runnable
//                        // otherwise create a forsyde task just for it [same for target]
//                        final Task targetTask = forSyDeSystemGraph.incomingEdgesOf(targetRunnableVertex).stream()
//                                .map(forSyDeSystemGraph::getEdgeSource)
//                                .filter(Task::conforms)
//                                .map(Task::enforce)
//                                .findAny()
//                                .orElseGet(() -> {
//                                    final Vertex dstTaskVertex = new Vertex(targetRunnableVertex.identifier + "Task",
//                                            VertexTrait.EXECUTION_TASK);
//                                    final Task dstTaskViewer = Task.enforce(dstTaskVertex);
//                                    forSyDeSystemGraph.addVertex(dstTaskVertex);
//                                    forSyDeSystemGraph.connect(dstTaskVertex, targetRunnableVertex, "callSequence", EdgeTrait.EXECUTION_CONTAINMENTEDGE);
//                                    return dstTaskViewer;
//                                });
//                        addEquivalence(processPrototype, originTask.getViewedVertex());
//                        addEquivalence(processPrototype, targetTask.getViewedVertex());
//                        // then add the precedence if necessary
//                        if (!originTask.equals(targetTask)) {
//                            final Vertex precedenceConstraintVertex = new Vertex(
//                                    originRunnableVertex.getIdentifier() + "_to_" + targetRunnableVertex.getIdentifier(),
//                                    VertexTrait.EXECUTION_PRECEDENCECONSTRAINT);
//                            final PrecedenceConstraint precedenceConstraint = PrecedenceConstraint.enforce(precedenceConstraintVertex);
//                            forSyDeSystemGraph.addVertex(precedenceConstraintVertex);
//                            forSyDeSystemGraph.connect(precedenceConstraintVertex, originTask.getViewedVertex(), "predecessor",
//                                    EdgeTrait.EXECUTION_CONSTRAINTEDGE);
//                            forSyDeSystemGraph.connect(precedenceConstraintVertex, targetTask.getViewedVertex(), "sucessor",
//                                    EdgeTrait.EXECUTION_CONSTRAINTEDGE);
//                            addEquivalence(processPrototype, precedenceConstraintVertex);
//                        }
//                    })
//                )
//            )
//        );
//    }

    private long fromTimeUnitToLong(TimeUnit timeUnit) {
        switch (timeUnit) {
            case S: return 1L;
            case MS: return 1000L;
            case US: return 1000000L;
            case NS: return 1000000000L;
            //case 1000000000000: return TimeUnit.PS;
            default: return 1000000000000L;
        }
    }
}