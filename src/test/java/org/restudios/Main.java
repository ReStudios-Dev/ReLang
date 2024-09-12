package org.restudios;

import com.sun.net.httpserver.HttpServer;
import org.restudios.relang.ClassPath;
import org.restudios.relang.ReLang;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.ASTError;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
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

        int exitCode = 1;


        try {
            relang.prepare();
            relang.analyze();
            exitCode = relang.run();
        } catch (AnalyzerError | ASTError error) {
            error.printStackTrace(System.err);
        }


        System.out.println();
        System.out.println("----------");
        System.out.println("Exit code: "+exitCode);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - l) + "ms");

    }

}
