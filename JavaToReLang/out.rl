/*
* GENERATED
*/
@ReflectionClass("java.io.File")
public native class File implements Serializable, Comparable<File> {
    private static FileSystem FS;
    private str path;
    private PathStatus status;
    private int prefixLength;
    public static char separatorChar;
    public static str separator;
    public static char pathSeparatorChar;
    public static str pathSeparator;
    private static Unsafe UNSAFE;
    private static int PATH_OFFSET;
    private static int PREFIX_LENGTH_OFFSET;
    private static int serialVersionUID;
    private Path filePath;
    static bool $assertionsDisabled;


    native public str getName();

    native public bool equals(Object arg0);

    native public int length();

    native public str toString();

    native public int hashCode();

    native public bool isHidden();

    native public int compareTo(File arg0);

    native public int compareTo(Object arg0);

    native public String[] list();

    native public String[] list(FilenameFilter arg0);

    native public bool isAbsolute();

    native public str getParent();

    native private void readObject(ObjectInputStream arg0);

    native private void writeObject(ObjectOutputStream arg0);

    native public bool delete();

    native public bool setReadOnly();

    native public bool canRead();

    native public str getPath();

    native public URI toURI();

    native public URL toURL();

    native public str getAbsolutePath();

    native public bool exists();

    native public bool createNewFile();

    native public bool renameTo(File arg0);

    native public bool isDirectory();

    native bool isInvalid();

    native public str getCanonicalPath();

    native private static str slashify(str arg0, bool arg1);

    native public File getAbsoluteFile();

    native private String[] normalizedList();

    native public bool mkdir();

    native public File getCanonicalFile();

    native public File getParentFile();

    native public bool mkdirs();

    native public bool setWritable(bool arg0);

    native public bool setWritable(bool arg0, bool arg1);

    native public bool setReadable(bool arg0, bool arg1);

    native public bool setReadable(bool arg0);

    native public bool setExecutable(bool arg0, bool arg1);

    native public bool setExecutable(bool arg0);

    native public static File[] listRoots();

    native public static File createTempFile(str arg0, str arg1);

    native public static File createTempFile(str arg0, str arg1, File arg2);

    native int getPrefixLength();

    native public bool canWrite();

    native public bool isFile();

    native public int lastModified();

    native public void deleteOnExit();

    native public File[] listFiles(FileFilter arg0);

    native public File[] listFiles(FilenameFilter arg0);

    native public File[] listFiles();

    native public bool setLastModified(int arg0);

    native public bool canExecute();

    native public int getTotalSpace();

    native public int getFreeSpace();

    native public int getUsableSpace();

    native public Path toPath();


    /*
    * GENERATED
    */
    @ReflectionClass("java.io.File$PathStatus")
    private static native enum PathStatus extends Enum {
        public static PathStatus INVALID;
        public static PathStatus CHECKED;
        private static PathStatus[] $VALUES;
    
    
        native public static PathStatus[] values();
    
        native public static PathStatus valueOf(str arg0);
    
        native private static PathStatus[] $values();
    
    
    }

    /*
    * GENERATED
    */
    @ReflectionClass("java.io.File$TempDirectory")
    private static native class TempDirectory {
        private static File TMPDIR;
        private static SecureRandom RANDOM;
    
    
        native private static int shortenSubName(int arg0, int arg1, int arg2);
    
        native static File location();
    
        native static File generateFile(str arg0, str arg1, File arg2);
    
    
    }

}