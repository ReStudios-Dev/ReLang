package org.restudios;

import com.sun.net.httpserver.HttpServer;
import org.restudios.relang.ClassPath;
import org.restudios.relang.ReLang;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.ASTError;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
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

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", exchange -> {

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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                error.printStackTrace(new PrintStream(baos));
                String result = "<h1>500 Internal server error</h1><hr><h3>"+baos+"</h3>";
                error.printStackTrace(System.err);

                exchange.sendResponseHeaders(500, result.length());
                exchange.getResponseBody().write(result.getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().close();
                return;
            }

            ClassInstance ci = relang.getContext().getClass("Server").instantiate(relang.getContext());
            RLClass string = relang.getContext().getClass(DynamicSLLClass.STRING);
            Value ua = Value.value(exchange.getRequestHeaders().getFirst("User-Agent"), relang.getContext());
            Value v = ci.findMethodFromNameAndArguments(relang.getContext(), "request", ua).runMethod(ci.getContext(), relang.getContext(), ua);
            String result = v.finalExpression().stringValue();
            exchange.sendResponseHeaders(200, result.length());
            exchange.getResponseBody().write(result.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();

        });
        server.setExecutor(null); // creates a default executor
        server.start();




        System.out.println();
        System.out.println("----------");
        //System.out.println("Exit code: "+exitCode);
        //System.out.println("Time elapsed: " + (System.currentTimeMillis() - l) + "ms");

    }

}
