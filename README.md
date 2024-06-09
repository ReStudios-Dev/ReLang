# It's ReLang!

Welcome to the strongly typed object-oriented scripting language repository. Its syntax and functionality are extremely reminiscent of Java and C#. The interpreter does not use any reflections, files, threads or libraries. This means that it can be transferred to absolutely any environment and to any language that supports exceptions and OOP

## Features
- 📑 Implicit/Explicit operators. Overloading binary and unary operators.
- ⚠️ Strong typing
- 🧲 Flexibility
- 📦 Object orientation
- 🧮 Subtyping (MyClass\<MyType\>)
- 🌉 Supports bridges (Native \<-\> ReLang)

## Running
You can find the code shown here: [tests](https://github.com/Swimer-MC/ReLang/tree/main/src/test/java/org/restudios)
##### Interpreter instantiate:
```java
ReLang relang = new ReLang();
```

##### Setup input and output streams:
```java
PrintStream out = System.out; // output stream
PrintStream err = System.err; // error stream
relang.setOutput(out, err);
```

#####  Load SLL (Standart Language library) and runtime code:
Loading SLL code can be done in several ways:
1. Load each SLL class individually
2. Loading SLL from one file (having previously created one)
4. Stuff all SLL classes into a string variable

To perform the second and third methods, you will need to open a text editor and paste the contents of each file from the `sll` folder (and subfolders) into it. It doesn’t matter in what order and the number of spaces between classes doesn’t matter. Next, you can save the portable version of sll you created into a single file and load it. You can also push the contents into a string variable in your application if your environment does not allow reading from files

We will consider the first option:

Loader.java
```java
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
```
our code:
```java
Map<String, String> map = Loader.load(new File("sll" /* sll folder */));  
Loader.load(map, new File("test.rl" /* file with code to run */));

// import into interpreter
for (Map.Entry<String, String> entry : map.entrySet()) {  
    relang.getClassLoader().addClassPath(new ClassPath(entry.getKey() /* source (absolute file path) */, entry.getValue() /* code */));  
}
```
##### Run:
```java
int exitCode = relang.run();
```
Exit code - application termination code. If the application closed naturally, then the exit code will be 0. If an uncaught exception occurred - 1. You can also use the `exit` keyword and pass your exit code to it. This keyword means force quitting the ReLang application. Please note that you can also pass 0 and 1 in `exit`. Example of using exit: `exit 5;`
