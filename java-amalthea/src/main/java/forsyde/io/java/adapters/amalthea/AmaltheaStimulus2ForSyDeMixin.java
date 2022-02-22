package forsyde.io.java.adapters.amalthea;

import forsyde.io.java.adapters.EquivalenceModel2ModelMixin;
import forsyde.io.java.core.ForSyDeSystemGraph;
import forsyde.io.java.core.Vertex;
import forsyde.io.java.core.VertexTrait;
import forsyde.io.java.typed.viewers.execution.*;
import org.eclipse.app4mc.amalthea.model.*;
import org.eclipse.app4mc.amalthea.model.PeriodicStimulus;
import org.eclipse.app4mc.amalthea.model.Task;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AmaltheaStimulus2ForSyDeMixin extends EquivalenceModel2ModelMixin<INamed, Vertex> {

    default void fromStimulusToForSyDe(Amalthea amalthea, ForSyDeSystemGraph forSyDeSystemGraph) {
        amalthea.getStimuliModel().getStimuli().forEach((stimulus) -> {
            if (stimulus instanceof PeriodicStimulus) {
                final PeriodicStimulus periodicStimulus = (PeriodicStimulus) stimulus;
                final Vertex timerVertex = new Vertex(stimulus.getName(), VertexTrait.EXECUTION_PERIODICSTIMULUS);
                timerVertex.ports.add("stimulated");
                forSyDeSystemGraph.addVertex(timerVertex);
                final forsyde.io.java.typed.viewers.execution.PeriodicStimulus periodicStimulusVertex = new PeriodicStimulusViewer(timerVertex);
                if (periodicStimulus.getRecurrence() != null) {
                    periodicStimulusVertex.setPeriodNumerator(periodicStimulus.getRecurrence().getValue().longValue());
                    periodicStimulusVertex.setPeriodDenominator(fromTimeUnitToLong(periodicStimulus.getRecurrence().getUnit()));
                } else {
                    periodicStimulusVertex.setPeriodNumerator(1L);
                    periodicStimulusVertex.setPeriodDenominator(1L);
                }
                if (periodicStimulus.getOffset() != null) {
                    periodicStimulusVertex.setOffsetNumerator(periodicStimulus.getOffset().getValue().longValue());
                    periodicStimulusVertex.setOffsetDenominator(fromTimeUnitToLong(periodicStimulus.getOffset().getUnit()));
                } else {
                    periodicStimulusVertex.setOffsetNumerator(0L);
                    periodicStimulusVertex.setOffsetDenominator(1L);
                }
                addEquivalence(stimulus, timerVertex);
            } else if (stimulus instanceof InterProcessStimulus) {
                final InterProcessStimulus interProcessStimulus = (InterProcessStimulus) stimulus;
                final Vertex precedenceVertex = new Vertex(stimulus.getName());
                final List<Task> triggeringTasks = amalthea.getSwModel().getTasks().stream().filter(t ->
                    t.getActivityGraph().getItems().stream()
                            .anyMatch(e -> e instanceof InterProcessTrigger && ((InterProcessTrigger) e).getStimulus().equals(interProcessStimulus))
                    ).collect(Collectors.toList());
                if (triggeringTasks.size() > 1) {
                    final ReactiveANDStimulus reactiveANDStimulus = ReactiveANDStimulus.enforce(precedenceVertex);
                } else {
                    final SimpleReactiveStimulus simpleReactiveStimulus = SimpleReactiveStimulus.enforce(precedenceVertex);
                    if (interProcessStimulus.getCounter() != null) {
                        final DownsampleReactiveStimulus repetitionReactiveStimulus = DownsampleReactiveStimulus.enforce(precedenceVertex);
                        repetitionReactiveStimulus.setInitialPredecessorSkips(interProcessStimulus.getCounter().getOffset());
                        repetitionReactiveStimulus.setRepetitivePredecessorSkips(interProcessStimulus.getCounter().getPrescaler());
                    }
                }
                forSyDeSystemGraph.addVertex(precedenceVertex);
                addEquivalence(interProcessStimulus, precedenceVertex);
            }
        });
    }

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
