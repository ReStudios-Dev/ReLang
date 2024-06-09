package org.restudios.relang.parser;

import org.restudios.relang.ClassPath;
import org.restudios.relang.ReLang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {
    public static ArrayList<File> getAllRL(File root){
        ArrayList<File> files = new ArrayList<>();
        File[] f = root.listFiles();
        if(f == null) return files;
        for (File file : f) {
            if(file.isFile()){
                if(file.getName().endsWith(".rl")){
                    files.add(file);
                }
            }else if(file.isDirectory()){
                files.addAll(getAllRL(file));
            }
        }
        return files;
    }
    public static void main(String[] args) throws IOException {
        long l = System.currentTimeMillis();

        ReLang relang = new ReLang();
        relang.setDebug(false);
        relang.setOutput(System.out, System.err);

        for (File sll : getAllRL(new File("sll"))) {
            relang.getClassLoader().addClassPath(new ClassPath(sll.getAbsolutePath(), new String(Files.readAllBytes(sll.toPath()))));
        }
        File input = new File("test.rl");
        relang.getClassLoader().addClassPath(new ClassPath(input.getAbsolutePath(), new String(Files.readAllBytes(input.toPath()))));


        int exitCode = relang.run();


        System.out.println();
        System.out.println("----------");
        System.out.println("Exit code: "+exitCode);
        System.out.println("Time elapsed: "+(System.currentTimeMillis()-l)+"ms");

    }

}
