package org.restudios;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        //CompilationUnit cu = StaticJavaParser.parse("interface TestInterface {} public class Test extends Object implements TestInterface { public int i = 4; public static Test instance; public static void main(String[] args) {System.out.println();} class AAA { class CCC{ public int i = 2;}} class BBB{} }");
        CompilationUnit cu = StaticJavaParser.parse(new File("C:\\Users\\swimery\\IdeaProjects\\ReLang\\src\\main\\java\\org\\restudios\\relang\\parser\\ast\\ASTGenerator.java"));

        List<ClassOrInterfaceDeclaration> rootClasses = new ArrayList<>();
        cu.accept(new RootClassCollector(), rootClasses);
        List<ClassNode> classes = new ArrayList<>(rootClasses.stream().map(ClassNode::new).toList());
        String result = classes.stream().map(classNode -> String.join("\n", classNode.stringify())).collect(Collectors.joining("\n\n"));
        Files.writeString(Path.of("C:\\Users\\swimery\\IdeaProjects\\ReLang\\JavaToReLang\\out.rl"), result);
    }
    static class RootClassCollector extends VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<ClassOrInterfaceDeclaration> collector) {
            super.visit(n, collector);
            if(!n.isInnerClass()) collector.add(n);
        }
    }
}