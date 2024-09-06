package org.restudios;

import org.restudios.relang.ClassPath;
import org.restudios.relang.ReLang;
import org.restudios.relang.parser.analyzer.AnalyzerError;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        long l = System.currentTimeMillis();

        ReLang relang = new ReLang();
        relang.setDebug(false);
        relang.setOutput(System.out, System.err);

        Map<String, String> map = Loader.load(new File("sll"));
        Loader.load(map, new File(args[0]));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            relang.getClassLoader().addClassPath(new ClassPath(entry.getKey(), entry.getValue()));
        } 
        relang.prepare();

        try {
            relang.analyze();
        }catch (AnalyzerError error){
            error.printStackTrace(System.err);
        }

        int exitCode = relang.run();

        System.out.println();
        System.out.println("----------");
        System.out.println("Exit code: "+exitCode);
        System.out.println("Time elapsed: "+(System.currentTimeMillis()-l)+"ms");

    }

}
