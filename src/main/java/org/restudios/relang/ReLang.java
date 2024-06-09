package org.restudios.relang;

import org.restudios.relang.parser.ast.ASTGenerator;
import org.restudios.relang.parser.ast.PsiListener;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.GlobalContext;
import org.restudios.relang.parser.exceptions.ExitExp;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.lexer.LexerV2;
import org.restudios.relang.parser.modules.threading.AThreadManager;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.NativeClass;
import org.restudios.relang.parser.utils.RLOutput;
import org.restudios.relang.parser.utils.RLStackTraceElement;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ReLang {
    /**
     * Context
     */
    private final Context context;

    /**
     * Class loader
     */
    private final ReLangClassLoader classLoader;

    /**
     * Debug mode. In debugging mode, all exceptions
     * will be processed by the programming language
     */
    private boolean debug;

    private List<BlockStatement> preparedCode;

    public ReLang() {
        context = new GlobalContext();
        classLoader = new ReLangClassLoader();
        debug = false;
        preparedCode = null;
    }

    /**
     * Get current thread manager
     */
    public AThreadManager getThreadManager() {
        return context.getThreadManager();
    }

    /**
     * The standard uses an UnsupportedThreadManager which throws an
     * error whenever the user tries to create a thread,
     * but Thread.getCurrentThread() returns a thread named main,
     * which cannot be interacted with. If you need to use
     * threads and your environment supports them, you can extend
     * AThreadManager by writing your own thread system. Don't be
     * afraid, it's extremely easy.
     * @param threadManager Thread manager
     */
    public ReLang setThreadManager(AThreadManager threadManager) {
        context.setThreadManager(threadManager);
        return this;
    }

    /**
     * All variables, classes and methods are located here.
     * Also, here is the necessary information: information
     * about output streams, a thread manager, a stack trace
     * and other information for executing the code.
     */
    public Context getContext() {
        return context;
    }

    /**
     * In debugging mode, all exceptions will be processed
     * by the programming language
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * In debugging mode, all exceptions will be processed
     * by the programming language
     */
    public ReLang setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * Add a native class (the so-called bridge class). Allows
     * communication between language code and your project.
     * <br><br>
     * <b>IMPORTANT!</b> Before initializing native classes
     * and adding them to this list, run the ReLang::prepare()
     * function, but if you don't have native methods, it's
     * not necessary
     */
    public ReLang addNativeClass(NativeClass clazz){
        context.getNativeClasses().add(clazz);
        return this;
    }

    /**
     * Class loader. It stores a list of ReLang programming
     * language codes to execute
     */
    public ReLangClassLoader getClassLoader(){
        return this.classLoader;
    }

    /**
     * Output streams:
     * Normal (out - information),
     * Errors (err - exceptions, errors)
     */
    public void setOutput(RLOutput output){
        context.setOutput(output);
    }

    /**
     * Output streams:
     * Normal (out - information),
     * Errors (err - exceptions, errors)
     */
    public void setOutput(PrintStream out, PrintStream err){
        setOutput(new RLOutput(err, out));
    }

    /**
     * Output streams:
     * Normal (out - information, exceptions, errors, etc.),
     */
    public void setOutput(PrintStream out){
        setOutput(new RLOutput(out));
    }

    /**
     * Prepare code for execution. Not necessary if you are not adding native classes
     */
    public void prepare(){
        if(preparedCode == null){
            ArrayList<ClassPath> classPaths = classLoader.getClassPaths();
            this.preparedCode = prepare(classPaths);
        }
    }

    /**
     * Run the program
     * @return Exit code (set with "exit [code];"). Default - 0
     */
    public int run(){
        if(preparedCode == null){
            prepare();
        }
        return run(preparedCode);
    }

    /**
     * Execute your line/lines of code
     * @param source Code source (for example, absolute file path)
     * @param code Lines of ReLang code
     * @return Exit code (set with "exit [code];"). Default - 0
     */
    public int execute(String source, String code){
        return run(prepare(Collections.singletonList(new ClassPath(source, code))));
    }

    /**
     * Same as execute(String, String), but with the source "native"
     * @param code Lines of ReLang code
     * @return Exit code (set with "exit [code];"). Default - 0
     */
    public int execute(String code){
        return execute("native", code);
    }


    private int run(List<BlockStatement> statements){
        int exitCode = 0;
        try {
            for (BlockStatement blockStatement : statements) {
                blockStatement.runProgram(context);
            }
        }catch (ExitExp e){
            exitCode = e.code;
        } catch (RLException rle){
            exitCode = 1;
            if(debug){
                System.out.println("-------------");
                for (RLStackTraceElement element : rle.getTrace().getElements()) {
                    System.out.println(element);
                }
                throw new RuntimeException(rle);
            }else{
                rle.instantiate(context).tryToPrintException();
            }
        }
        context.getOutput().exitCode = exitCode;
        return exitCode;
    }
    private BlockStatement parseCode(ClassPath path){
        LexerV2 lexer = new LexerV2(path.getSource(), path.getCode());
        ArrayList<Token> lexemes = lexer.lex();
        ASTGenerator ast = new ASTGenerator(lexemes, path.getCode(), path.getSource());
        return ast.parseProgram();
    }
    private BlockStatement parseCode(ClassPath path, PsiListener listener){
        LexerV2 lexer = new LexerV2(path.getSource(), path.getCode());
        ArrayList<Token> lexemes = lexer.lex();
        ASTGenerator ast = new ASTGenerator(lexemes, path.getCode(), path.getSource());
        ast.setPSIListener(listener);
        return ast.parseProgram();
    }
    private List<BlockStatement> prepare(List<ClassPath> classPaths) {
        List<BlockStatement> statementList = classPaths.stream().map(this::parseCode).collect(Collectors.toList());
        statementList.forEach(blockStatement -> blockStatement.prepare(context));
        return statementList;
    }
    private List<BlockStatement> prepare(List<ClassPath> classPaths, PsiListener listener) {
        List<BlockStatement> statementList = classPaths.stream().map(classPath -> parseCode(classPath, listener)).collect(Collectors.toList());
        statementList.forEach(blockStatement -> blockStatement.prepare(context));
        return statementList;
    }
}
