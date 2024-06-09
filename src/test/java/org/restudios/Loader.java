package org.restudios;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Loader {
    private static ArrayList<File> getAllRL(File root){
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
    public static Map<String, String> load(File root) throws IOException {
        ArrayList<File> files = getAllRL(root);
        Map<String, String> result = new LinkedHashMap<>();
        for (File file : files) load(result, file);
        return result;
    }
    public static void load(Map<String, String> result, File file) throws IOException {
        String source = file.getAbsolutePath();
        String code = new String(Files.readAllBytes(file.toPath()));
        result.put(source, code);

    }
}
