package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class Main {

    public static void main(String[] args) throws IOException {
        Launcher launcher = new Launcher();

        String rootProject = "C:\\Users\\syuuj\\jsoup";
        launcher.addInputResource(rootProject+"\\src\\main\\java");
        List<String> JarFile=addJarSourceFile(Paths.get(rootProject));
        //JarFile=filterConflictingJars(JarFile);
        String[] array = JarFile.toArray(new String[0]);

        launcher.getEnvironment().setSourceClasspath(array);

        //launcher.getEnvironment().setNoClasspath(false);
        
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setAutoImports(true);

        CtModel model = launcher.buildModel();
        
        System.out.println("Model built successfully!");
        System.out.println("Classes found: " + model.getAllTypes().size());
        Visitor visitor = new Visitor();

        for (CtType<?> clazz : model.getAllTypes()) {
            System.out.println(clazz.getQualifiedName());
            clazz.accept(visitor);
            System.out.println();
        }
        visitor.excuteMetrics();
        visitor.printCSV(args[0]);
    }

    private static List<String> addJarSourceFile(Path path) throws IOException {
        List<String> JarFile;
        try (Stream<Path> paths = Files.walk(path)) {
            JarFile = paths.filter(p -> p.toString().endsWith(".jar")).map(p -> p.toString()).toList();
        }
        return JarFile;
    }

    private static List<String> filterConflictingJars(List<String> jars){
        List<String> safe=new ArrayList<>();
        for(String jar:jars){
            try (JarFile jf = new JarFile(jar)){
                boolean hasConflict=jf.stream().anyMatch(entry->{
                    String name=entry.getName();
                    return name.startsWith("org/w3c/dom")
                        ||name.startsWith("javax/xml/")
                        ||name.startsWith("org/xml/sax/");
                });
                if(!hasConflict){
                    safe.add(jar);
                }else{
                    System.out.println("Conflict : "+jar);
                }
            } catch (Exception e) {
            }
        }
        return safe;
    }
}
