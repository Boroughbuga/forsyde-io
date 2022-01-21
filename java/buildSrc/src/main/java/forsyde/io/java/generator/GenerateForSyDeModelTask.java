package forsyde.io.java.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.squareup.javapoet.*;
import forsyde.io.java.generator.dsl.ForSyDeTraitDSLLexer;
import forsyde.io.java.generator.dsl.ForSyDeTraitDSLParser;
import forsyde.io.java.generator.specs.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.text.WordUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.Incremental;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateForSyDeModelTask extends DefaultTask implements Task {

    @Incremental
    @InputFile
    @Deprecated
    File inputModelJson = getProject().file("meta.json");

    @InputFile
    @Deprecated
    File inputModelXml = getProject().file("meta.json");

    @InputFile
    File inputModelDSL = getProject().file("traithierarchy.traitdsl");

    @InputDirectory
    File rootOutDir = getProject().getProjectDir();

    @OutputFiles
    List<File> outFiles = new ArrayList<>();

    @TaskAction
    public void execute() {
        /*
        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module());
         */
        try {
            final ForSyDeTraitDSLLexer forSyDeTraitDSLLexer = new ForSyDeTraitDSLLexer(CharStreams.fromPath(inputModelDSL.toPath()));
            final CommonTokenStream commonTokenStream = new CommonTokenStream(forSyDeTraitDSLLexer);
            final ForSyDeTraitDSLParser forSyDeTraitDSLParser = new ForSyDeTraitDSLParser(commonTokenStream);
            final ForSyDeIOTraitDSLListener forSyDeTraitDSLListener = new ForSyDeIOTraitDSLListener();
            final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
            parseTreeWalker.walk(forSyDeTraitDSLListener, forSyDeTraitDSLParser.rootTraitHierarchy());
            final TraitHierarchy traitHierarchy = forSyDeTraitDSLListener.traitHierarchy;//objectMapper.readValue(inputModelJson, TraitHierarchy.class);
            generateFiles(traitHierarchy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateFiles(TraitHierarchy model) throws IOException {
        Path root = rootOutDir.toPath();
        Path generatedDir = root.resolve(Paths.get("src-gen/main/java"));
        Path enumsPath = root.resolve(Paths.get("src-gen/main/java/forsyde/io/java/core/"));
        Path viewersPath = root.resolve("src-gen/main/java/forsyde/io/java/typed/viewers/");
        Files.createDirectories(enumsPath) ;
        Files.createDirectories(viewersPath);

        outFiles.add(JavaFile.builder("forsyde.io.java.core", generateEdgeTraitsEnum(model)).build().writeToFile(generatedDir.toFile()));
        outFiles.add(JavaFile.builder("forsyde.io.java.core", generateVertexTraitEnum(model)).build().writeToFile(generatedDir.toFile()));

//		Files.writeString(edgePath, edgeStr, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//		Files.writeString(vertexPath, vertexStr, StandardOpenOption.WRITE, StandardOpenOption.CREATE);


        for (VertexTraitSpec trait : model.vertexTraits) {
//			Path interfacePath = root
//					.resolve("src-gen/main/java/forsyde/io/java/typed/viewers/" + interfaceSpec.name + ".java");
            final TypeSpec interfaceSpec = generateVertexInterface(trait);
            final String extraPackages = trait.getNamespaces().isEmpty() ?
                    "" : "." + String.join(".", trait.getNamespaces());
            outFiles.add(JavaFile.builder("forsyde.io.java.typed.viewers" + extraPackages, interfaceSpec)
                    .build().writeToFile(generatedDir.toFile()));
            // Files.writeString(interfacePath, interfaceStr, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        }

        for (VertexTraitSpec trait : model.vertexTraits) {
            final TypeSpec viewerSpec = generateVertexViewer(trait);
            final String extraPackages = trait.getNamespaces().isEmpty() ?
                    "" : "." + String.join(".", trait.getNamespaces());
            outFiles.add(JavaFile.builder("forsyde.io.java.typed.viewers" + extraPackages, viewerSpec)
                    .build().writeToFile(generatedDir.toFile()));
            // Files.writeString(interfacePath, interfaceStr, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        }

    }

    protected String toCamelCase(String word) {
        return WordUtils.capitalize(word);
        //return WordUtils.capitalizeFully(word, '_').replace("_", "");
    }

    public TypeName metaToJavaType(PropertyTypeSpec prop) {
        return PropertyTypeSpecs.cases()
                .StringVertexProperty(() -> (TypeName) ClassName.get(String.class))
                .IntVertexProperty(() -> ClassName.get(Integer.class))
                .BooleanVertexProperty(() -> ClassName.get(Boolean.class))
                .FloatVertexProperty(() -> ClassName.get(Float.class))
                .DoubleVertexProperty(() -> ClassName.get(Double.class))
                .LongVertexProperty(() -> ClassName.get(Long.class))
                .ArrayVertexProperty((p) -> ParameterizedTypeName.get(ClassName.get(List.class), metaToJavaType(p)))
                .IntMapVertexProperty((p) -> ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(Integer.class),
                        metaToJavaType(p)))
                .StringMapVertexProperty((p) -> ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
                        metaToJavaType(p)))
                .apply(prop);
        /*
        switch (prop.name) {
            case INTMAP:
                return ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(Integer.class),
                        metaToJavaType(prop.valueType.get()));
            case STRINGMAP:
                return ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class),
                        metaToJavaType(prop.valueType.get()));
            case ARRAY:
                return ParameterizedTypeName.get(ClassName.get(ArrayList.class), metaToJavaType(prop.valueType.get()));
            case BOOLEAN:
                return ClassName.get(Boolean.class);
            case INTEGER:
                return ClassName.get(Integer.class);
            case FLOAT:
                return ClassName.get(Float.class);
            case DOUBLE:
                return ClassName.get(Double.class);
            case LONG:
                return ClassName.get(Long.class);
            default:
                return ClassName.get(String.class);
        }
        */
    }

    public MethodSpec generatePropertyGetter(PropertySpec prop) {
        TypeName typeOut = metaToJavaType(prop.type);
        ParameterizedTypeName optionalOut = ParameterizedTypeName.get(ClassName.get(Optional.class), typeOut);
        MethodSpec.Builder getPropMethod = MethodSpec.methodBuilder("get" + toCamelCase(prop.name))
                .addAnnotation(
                        AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build())
                .addJavadoc("getter for required property \"$L\".\n", prop.name)
                .addJavadoc("@return \"$L\".\n\" property", prop.name)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT).returns(typeOut);
        prop.comment.ifPresent(comment -> getPropMethod.addJavadoc("$L", comment));
        getPropMethod.addStatement("return ($T) getViewedVertex().getProperties().get(\"$L\").unwrap()", typeOut,
                prop.name);
        return getPropMethod.build();
    }

    public MethodSpec generatePropertySetter(PropertySpec prop) {
        TypeName typeIn = metaToJavaType(prop.type);
        // ParameterizedTypeName optionalOut =
        // ParameterizedTypeName.get(ClassName.get(Optional.class), typeOut);
        ;
        MethodSpec.Builder getPropMethod = MethodSpec.methodBuilder("set" + toCamelCase(prop.name))
                .addParameter(typeIn, WordUtils.uncapitalize(toCamelCase(prop.name)))
                .addJavadoc("setter for named required property \"$L\".\n", prop.name)
                .addJavadoc("@param $L value for required property \"$L\".\n", WordUtils.uncapitalize(toCamelCase(prop.name)), prop.name)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT);
        TypeName propClass = ClassName.get("forsyde.io.java.core", "VertexProperty");
        getPropMethod.addStatement("getViewedVertex().getProperties().put(\"$L\", $T.create($L))", prop.name,
                propClass, WordUtils.uncapitalize(toCamelCase(prop.name)));
//		}
        return getPropMethod.build();
    }

    public MethodSpec generatePortGetter(PortSpec port) {
        final String traitName = port.vertexTrait.name.replace("::", "_").toUpperCase();
        final String extraPackages = port.vertexTrait.getNamespaces().isEmpty() ?
                "" : "." + String.join(".", port.vertexTrait.getNamespaces());
        TypeName vertexClass = ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, port.vertexTrait.getTraitLocalName());
        ParameterizedTypeName optionalOut = ParameterizedTypeName.get(ClassName.get(Optional.class), vertexClass);
        ParameterizedTypeName listOut = ParameterizedTypeName.get(ClassName.get(List.class), vertexClass);
        ParameterizedTypeName setOut = ParameterizedTypeName.get(ClassName.get(Set.class), vertexClass);
        ParameterizedTypeName arrayType = ParameterizedTypeName.get(ClassName.get(ArrayList.class), vertexClass);
        ParameterizedTypeName setType = ParameterizedTypeName.get(ClassName.get(HashSet.class), vertexClass);
        MethodSpec.Builder getPortMethod = MethodSpec.methodBuilder("get" + toCamelCase(port.name) + "Port")
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .addParameter(ClassName.get("forsyde.io.java.core", "ForSyDeSystemGraph"), "model");
        // .addParameter(vertexClass, "vertex");
        if (port.multiple.orElse(true)) {
            if (port.ordered.orElse(false)) {
                getPortMethod.returns(listOut);
                String statement = "return $T.getMultipleNamedPort(model, getViewedVertex(), \"$L\", \"$L\", \"$L\").stream().map(v -> $T.safeCast(v).get()).collect($T.toList())";
                getPortMethod.addStatement(statement,
                        ClassName.get("forsyde.io.java.core", "VertexAcessor"),
                        port.name,
                        port.vertexTrait.getTraitLocalName(),
                        port.direction.toString().toLowerCase(),
                        vertexClass,
                        Collectors.class);
//				getPortMethod.addStatement("$T outList = new $T()", arrayType, arrayType);
            } else {
                getPortMethod.returns(setOut);
                String statement = "return $T.getMultipleNamedPort(model, getViewedVertex(), \"$L\", \"$L\", \"$L\").stream().map(v -> $T.safeCast(v).get()).collect($T.toSet())";
                getPortMethod.addStatement(statement,
                        ClassName.get("forsyde.io.java.core", "VertexAcessor"),
                        port.name,
                        port.vertexTrait.getTraitLocalName(),
                        port.direction.toString().toLowerCase(),
                        vertexClass,
                        Collectors.class);
//				getPortMethod.addStatement("$T outList = new $T()", setType, setType);
            }
        } else {
            getPortMethod.returns(optionalOut);
            getPortMethod.addStatement("return $T.getNamedPort(model, getViewedVertex(), \"$L\", \"$L\", \"$L\").map(v -> $T.safeCast(v).get())",
                    ClassName.get("forsyde.io.java.core", "VertexAcessor"),
                    port.name,
                    port.vertexTrait.getTraitLocalName(),
                    port.direction.toString().toLowerCase(),
                    vertexClass);
        }
        return getPortMethod.build();
    }

    public TypeSpec generateVertexTraitEnum(TraitHierarchy model) {
        ClassName generalTraitClass = ClassName.get("forsyde.io.java.core", "Trait");
        TypeSpec.Builder vertexTraitEnumBuilder = TypeSpec.enumBuilder("VertexTrait")
                .addSuperinterface(generalTraitClass).addModifiers(Modifier.PUBLIC);
        // // refinement method
        MethodSpec.Builder refinesMethod = MethodSpec.methodBuilder("refines").returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "VertexTrait"), "one")
                .addParameter(ClassName.get("forsyde.io.java.core", "VertexTrait"), "other")
                .beginControlFlow("switch (one)");
        List<VertexTraitSpec> traits = model.vertexTraits.stream().collect(Collectors.toList());
        for (VertexTraitSpec trait : traits) {
            final String enumTraitName = trait.name.replace("::", "_").toUpperCase();
            vertexTraitEnumBuilder.addEnumConstant(enumTraitName);
            refinesMethod.addCode("case " + enumTraitName + ":\n");
            refinesMethod.beginControlFlow("switch (other)");
            // def superIterator = new DepthFirstIterator<String, DefaultEdge>(traitGraph,
            // vertexTrait.key)
            Queue<VertexTraitSpec> refinedTraits = new LinkedList<>(List.of(trait));
            while (!refinedTraits.isEmpty()) {
                VertexTraitSpec refinedTrait = refinedTraits.poll();
                final String refinedEnumTraitName = refinedTrait.name.replace("::", "_").toUpperCase();
                refinesMethod.addCode("case " + refinedEnumTraitName + ": ");
                refinesMethod.addStatement("return true");
                refinedTraits.addAll(refinedTrait.refinedTraits);
            }
            refinesMethod.addStatement("default: return false");
            refinesMethod.endControlFlow();
        }
        refinesMethod.addStatement("default: return false");
        refinesMethod.endControlFlow();
        vertexTraitEnumBuilder.addMethod(refinesMethod.build());
        vertexTraitEnumBuilder.addMethod(MethodSpec.methodBuilder("refines").returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC).addParameter(ClassName.get("forsyde.io.java.core", "Trait"), "other")
                .addStatement(
                        "return other instanceof VertexTrait ? VertexTrait.refines(this, (VertexTrait) other) : false")
                .build());
        vertexTraitEnumBuilder
                .addMethod(MethodSpec.methodBuilder("getName").returns(ClassName.bestGuess("java.lang.String"))
                        .addModifiers(Modifier.PUBLIC).addStatement("return this.toString()").build());
        return vertexTraitEnumBuilder.build();
    }

    public TypeSpec generateEdgeTraitsEnum(TraitHierarchy model) {
        ClassName traitClass = ClassName.get("forsyde.io.java.core", "Trait");
        TypeSpec.Builder edgeEnumBuilder = TypeSpec.enumBuilder("EdgeTrait").addSuperinterface(traitClass)
                .addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder refinesMethod = MethodSpec.methodBuilder("refines").returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "EdgeTrait"), "one")
                .addParameter(ClassName.get("forsyde.io.java.core", "EdgeTrait"), "other")
                .beginControlFlow("switch (one)");
        List<EdgeTraitSpec> traits = model.edgeTraits.stream().collect(Collectors.toList());
        for (EdgeTraitSpec trait : traits) {
            final String enumTraitName = trait.name.replace("::", "_").toUpperCase();
            edgeEnumBuilder.addEnumConstant(enumTraitName);
            refinesMethod.addCode("case " + enumTraitName + ":\n");
            refinesMethod.beginControlFlow("switch (other)");
            Queue<EdgeTraitSpec> refinedTraits = new LinkedList<>(List.of(trait));
            while (!refinedTraits.isEmpty()) {
                EdgeTraitSpec refinedTrait = refinedTraits.poll();
                final String refinedEnumTraitName = refinedTrait.name.replace("::", "_").toUpperCase();
                refinesMethod.addCode("case " + refinedEnumTraitName + ": ");
                refinesMethod.addStatement("return true");
                refinedTraits.addAll(refinedTrait.refinedTraits);
            }
            refinesMethod.addStatement("default: return false");
            refinesMethod.endControlFlow();
        }
        refinesMethod.addStatement("default: return false");
        refinesMethod.endControlFlow();
        edgeEnumBuilder.addMethod(refinesMethod.build());
        edgeEnumBuilder.addMethod(MethodSpec.methodBuilder("refines").returns(TypeName.BOOLEAN)
                .addModifiers(Modifier.PUBLIC).addParameter(ClassName.get("forsyde.io.java.core", "Trait"), "other")
                .addStatement("return other instanceof EdgeTrait ? EdgeTrait.refines(this, (EdgeTrait) other) : false")
                .build());
        edgeEnumBuilder.addMethod(MethodSpec.methodBuilder("getName").returns(ClassName.bestGuess("java.lang.String"))
                .addModifiers(Modifier.PUBLIC).addStatement("return this.toString()").build());
        return edgeEnumBuilder.build();
    }

    public TypeSpec generateVertexInterface(VertexTraitSpec trait) {
        /*
        List<VertexTraitSpec> traits = model.vertexTraits.stream().collect(Collectors.toList());
        List<TypeSpec> traitList = new ArrayList<>(traits.size());
        for (VertexTraitSpec trait : traits) {
        */
        final String enumTraitName = trait.name.replace("::", "_").toUpperCase();
        final String extraPackages = trait.getNamespaces().isEmpty() ?
                "" : "." + String.join(".", trait.getNamespaces());
        TypeSpec.Builder traitInterface = TypeSpec.interfaceBuilder(ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName()))
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(trait.comment);
        for (VertexTraitSpec refinedTrait : trait.refinedTraits) {
            final String refinedExtraPackages = refinedTrait.getNamespaces().isEmpty() ?
                    "" : "." + String.join(".", refinedTrait.getNamespaces());
            traitInterface
                    .addSuperinterface(ClassName.get("forsyde.io.java.typed.viewers" + refinedExtraPackages, refinedTrait.getTraitLocalName()));
        }
        traitInterface.addSuperinterface(ClassName.get("forsyde.io.java.core", "VertexViewer"));

        for (PropertySpec prop : trait.requiredProperties) {
            traitInterface.addMethod(generatePropertyGetter(prop));
            traitInterface.addMethod(generatePropertySetter(prop));
        }
        for (PortSpec port : trait.requiredPorts) {
            traitInterface.addMethod(generatePortGetter(port));
        }
        MethodSpec.Builder conformsMethod = MethodSpec.methodBuilder("conforms")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "Vertex"), "vertex")
                .returns(ClassName.get(Boolean.class));
        conformsMethod.beginControlFlow("for ($T t : vertex.getTraits())",
                ClassName.get("forsyde.io.java.core", "Trait"));
        conformsMethod.addStatement("if(t.refines($T.$L)) return true",
                ClassName.get("forsyde.io.java.core", "VertexTrait"), enumTraitName);
        conformsMethod.endControlFlow();
        conformsMethod.addStatement("return false");
        traitInterface.addMethod(conformsMethod.build());

        MethodSpec.Builder conformsMethodViewer = MethodSpec.methodBuilder("conforms")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "VertexViewer"), "viewer")
                .returns(ClassName.get(Boolean.class));
        conformsMethodViewer.beginControlFlow("for ($T t : viewer.getViewedVertex().getTraits())",
                ClassName.get("forsyde.io.java.core", "Trait"));
        conformsMethodViewer.addStatement("if(t.refines($T.$L)) return true",
                ClassName.get("forsyde.io.java.core", "VertexTrait"), enumTraitName);
        conformsMethodViewer.endControlFlow();
        conformsMethodViewer.addStatement("return false");
        traitInterface.addMethod(conformsMethodViewer.build());

        MethodSpec.Builder safeCast = MethodSpec.methodBuilder("safeCast")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "Vertex"), "vertex")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class),
                        ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName())));
        safeCast.addStatement("return conforms(vertex) ? $T.of(new $T(vertex)) : Optional.empty()",
                ClassName.get(Optional.class),
                ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName() + "Viewer"));
        traitInterface.addMethod(safeCast.build());

        MethodSpec.Builder safeCastViewer = MethodSpec.methodBuilder("safeCast")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("forsyde.io.java.core", "VertexViewer"), "viewer")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class),
                        ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName())));
        safeCastViewer.addStatement(
                "return conforms(viewer.getViewedVertex()) ? $T.of(new $T(viewer.getViewedVertex())) : Optional.empty()",
                ClassName.get(Optional.class),
                ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName() + "Viewer"));
        traitInterface.addMethod(safeCastViewer.build());
        return traitInterface.build();
        /*
            traitList.add(traitInterface.build());
        }
        return traitList;
         */
    }

    public TypeSpec generateVertexViewer(VertexTraitSpec trait) {
        /*
        List<VertexTraitSpec> traits = model.vertexTraits.stream().collect(Collectors.toList());
        List<TypeSpec> viewersList = new ArrayList<>(traits.size());
        for (VertexTraitSpec trait : traits) {
         */
        final String enumTraitName = trait.name.replace("::", "_").toUpperCase();
        final String extraPackages = trait.getNamespaces().isEmpty() ?
                "" : "." + String.join(".", trait.getNamespaces());

        final TypeSpec.Builder viewerClass = TypeSpec.classBuilder(ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName()  + "Viewer"))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("forsyde.io.java.typed.viewers" + extraPackages, trait.getTraitLocalName()))
                //.superclass(ClassName.get("forsyde.io.java.core", "VertexViewer"))
                .addJavadoc(trait.comment)
                .addField(ClassName.get("forsyde.io.java.core", "Vertex"), "viewedVertex", Modifier.PUBLIC,
                        Modifier.FINAL);

        final MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder()
                .addParameter(ClassName.get("forsyde.io.java.core", "Vertex"), "vertex")
                .addModifiers(Modifier.PUBLIC);
        constructorMethod.addStatement("viewedVertex = vertex");
        viewerClass.addMethod(constructorMethod.build());
        viewerClass.addMethod(MethodSpec.methodBuilder("getViewedVertex").addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("forsyde.io.java.core", "Vertex")).addStatement("return viewedVertex")
                .build());
        viewerClass.addMethod(
                MethodSpec.methodBuilder("hashCode")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return getIdentifier().hashCode() + \"$L\".hashCode()", trait.getTraitLocalName())
                        .returns(int.class)
                        .build());
        viewerClass.addMethod(
                MethodSpec.methodBuilder("equals")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Object.class, "other")
                        .addStatement("return other instanceof $L ? (($L) other).getIdentifier().equals(getIdentifier()) : false",
                                trait.getTraitLocalName() + "Viewer", trait.getTraitLocalName() + "Viewer")
                        .returns(boolean.class)
                        .build());
        viewerClass.addMethod(
                MethodSpec.methodBuilder("toString")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return \"$L{\" + getViewedVertex().toString() + \"}\"", trait.getTraitLocalName() + "Viewer")
                        .returns(String.class)
                        .build());
        return viewerClass.build();
        /*
            viewersList.add(viewerClass.build());
        }
        return viewersList;

         */
    }

    private Integer directionToInt(PortDirection dir) {
        switch (dir) {
            case INCOMING:
                return 0;
            case OUTGOING:
                return 1;
            default:
                return 2;
        }
    }


    public File getInputModelJson() {
        return inputModelJson;
    }


    public File getInputModelXml() {
        return inputModelXml;
    }

    public File getRootOutDir() {
        return rootOutDir;
    }

    public List<File> getOutFiles() {
        return outFiles;
    }

    public File getInputModelDSL() {
        return inputModelDSL;
    }

    public void setInputModelDSL(File inputModelDSL) {
        this.inputModelDSL = inputModelDSL;
    }

}