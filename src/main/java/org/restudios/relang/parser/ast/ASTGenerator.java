package org.restudios.relang.parser.ast;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.TBoolean;
import org.restudios.relang.parser.ast.types.UnaryImplicitOperationType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.BinaryExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.GroupingExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.IdentifierExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.LambdaExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.ListExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.LogicalExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.implicit.UnaryImplicitExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.BooleanExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.NullExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.StringExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.TBooleanExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.numbers.CharExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.numbers.FloatExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.literals.numbers.IntegerExpression;
import org.restudios.relang.parser.ast.types.nodes.extra.AnnotationDefinition;
import org.restudios.relang.parser.ast.types.nodes.statements.AssigmentStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ClassDeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ClassInstantiationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ConstructorDeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.DoWhileStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.EmptyStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ErrorStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ExitStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ForStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ForeachStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ForiStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.IfStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.MemberStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.MethodCallStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.MethodDeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.OperatorOverloadStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.OutputStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ReturnStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.ThrowStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.TryStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.UnaryStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.WhileStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.classes.ClassBlock;
import org.restudios.relang.parser.ast.types.nodes.statements.classes.EnumClassBlock;
import org.restudios.relang.parser.ast.types.nodes.statements.overloading.ArithmeticOverloadStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.overloading.CastOverloadStatement;
import org.restudios.relang.parser.ast.types.nodes.statements.trying.CatchNode;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.tokens.TokenType;
import org.restudios.relang.parser.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SuppressWarnings({"SameParameterValue"})
public class ASTGenerator {
    private final ArrayList<Token> tokens;
    private final Token eof;
    private int current;
    private final ArrayList<ASTError> errors;
    private final String code;
    private final String source;
    private boolean ignoreNonCritical = false;
    private PsiListener listener = () -> exp -> {};

    public ASTGenerator(ArrayList<Token> tokens, String code, String source) {
        this.tokens = tokens;
        this.code = code;
        this.source = source;
        errors = new ArrayList<>();
        if(tokens.isEmpty()){
            eof = new Token(TokenType.EMPTY, "ast", "", 0, new LineCol(1,1), new LineCol(1,1));
        }else{
            Token last = tokens.get(tokens.size() - 1);
            eof = new Token(TokenType.EMPTY, "ast", "", last.getPosition(), last.getFrom(), last.getTo());
        }
    }
    @SuppressWarnings("unused")
    public PsiListener getPSIListener() {
        return listener;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ASTGenerator setPSIListener(PsiListener listener) {
        this.listener = listener;
        return this;
    }

    @SuppressWarnings("unused")
    public boolean isIgnoreNonCritical() {
        return ignoreNonCritical;
    }
    @SuppressWarnings("unused")
    public ASTGenerator setIgnoreNonCritical(boolean ignoreNonCritical) {
        this.ignoreNonCritical = ignoreNonCritical;
        return this;
    }

    @SuppressWarnings("unused")
    public ArrayList<ASTError> getAstErrors() {
        return errors;
    }

    private Token emptyToken(int pos) {
        LineCol lc = Utils.findStringPosition(this.code, pos);
        return new Token(TokenType.EMPTY, "ast", "", pos, lc, lc);
    }

    public BlockStatement parseProgram() {
        ArrayList<Statement> statements = new ArrayList<>();
        while (!this.isAtEnd()){
            Statement s = this.parseStatement();
            statements.add(s);
        }
        statements.removeIf(value -> (value instanceof EmptyStatement));
        return new BlockStatement(eof, statements);
    }

    public Statement parseStatement(){
        return parseStatement(false);
    }
    public Statement parseStatement(boolean checkSemicolon) {
        Statement statement = null;
        Token token = this.peek();
        PsiMarker marker = listener.mark();
        if(token == null){
            this.unexpected(this.eof, "Statement");
            return marker.touch(new EmptyStatement(this.eof));
        }
        if(this.match(TokenType.SEMICOLON) || this.match(TokenType.COMMENT)){
            return marker.touch(new EmptyStatement(token));
        }

        if(this.isType(token.getType(), true) && this.peek(1) != null){
            if(this.peekSafe(1).type == TokenType.OPEN_PARENTHESES){
                statement = this.parseMethodCall();
            }
        }
        if(statement == null && nextMethod()){
            statement = parseMethodDeclaration();
        }
        if(statement == null && nextVariable()){
            statement = parseVariableDeclaration();
        }
        if(statement == null && nextClass()){
            statement = parseClassDefinition();
        }
        if(statement == null && nextOperator()) {
            statement = parseOperatorMethod();
        }
        if(statement == null && (token.type == TokenType.IMPLICIT || token.type == TokenType.EXPLICIT)){
            statement = parseOperatorMethod();
        }
        if(token.type == TokenType.IDENTIFIER){
            if (this.isAtEnd() && statement == null){
                this.unexpectedToken(token);
            }
        }


        if(statement == null && token.type == TokenType.ERR){
            statement = this.parseError();
        }
        if(statement == null && token.type == TokenType.OUT){
            statement = this.parseOutput();
        }
        if(statement == null && token.type == TokenType.RETURN){
            statement = this.parseReturn();
        }
        if(statement == null && token.type == TokenType.EXIT){
            statement = this.parseExit();
        }
        if(statement == null && token.type == TokenType.OPEN_BRACE){
            statement = this.parseCodeBlock();
        }
        if(statement == null && token.type == TokenType.THROW){
            statement = this.parseThrowStatement();
        }
        if(statement == null && token.type == TokenType.TRY){
            statement = this.parseTryStatement();
        }
        if(statement == null && token.type == TokenType.IF){
            statement = this.parseIfStatement();
        }
        if(statement == null && token.type == TokenType.FOREACH){
            statement = this.parseForeachStatement();
        }
        if(statement == null && token.type == TokenType.DO){
            statement = this.parseDoWhileStatement();
        }
        if(statement == null && token.type == TokenType.WHILE){
            statement = this.parseWhileStatement();
        }
        if(statement == null && token.type == TokenType.FOR){
            statement = this.parseForStatement();
        }
        if(statement == null && token.type == TokenType.FORI){
            statement = this.parseForiStatement();
        }
        if(statement == null){
            statement = this.parseExpressionStatement();
        }
        if(statement == null){
            unexpected(peek(), "Statement");
            advance();
            return marker.touch(new EmptyStatement(previous()));
        }
        if(checkSemicolon && this.previous() != null && this.previousSafe().type != TokenType.CLOSE_BRACE && this.previousSafe().type != TokenType.OPEN_BRACE && this.previousSafe().type != TokenType.SEMICOLON
                && this.previousSafe().type != TokenType.COMMENT){
            this.consume(TokenType.SEMICOLON, "Expected ;");
        }
        return marker.touch(statement);
    }
    public Statement parseClassDefinition() {
        Token pf = peek();
        List<AnnotationDefinition> annotationDefinitions = annotationDefinitions();
        ArrayList<Visibility> vis = parseVisibilities(true);
        boolean isNative = match(TokenType.NATIVE);

        boolean isAbstract = match(TokenType.ABSTRACT);
        ClassType type = ClassType.CLASS;

        if (isAbstract) {
            consume(TokenType.CLASS, "Class");
            type = ClassType.ABSTRACT;
        }

        if (!isAbstract && match(TokenType.INTERFACE)) {
            type = ClassType.INTERFACE;
        } else if (!isAbstract && match(TokenType.ENUM)) {
            type = ClassType.ENUM;
        }  else if (!isAbstract && match(TokenType.ANNOTATION)) {
            type = ClassType.ANNOTATION;
        } else if (!isAbstract) {
            consume(TokenType.CLASS, "Class");
        }

        Token name = null;
        if (match(TokenType.IDENTIFIER)) {
            name = previous();
        } else {
            unexpected(peek(), "Class name");
        }
        ArrayList<String> types = new ArrayList<>();
        if(!isAtEnd() && peek().type == TokenType.OPEN_ANGLE_BRACKET){
            types = parseCustomTypesDeclaration();
        }
        Expression extending = null;
        ArrayList<Expression> implementations = new ArrayList<>();

        if (match(TokenType.EXTENDS)) {
            extending = parseExpression();
        }

        if (match(TokenType.IMPLEMENTS)) {
            do {
                implementations.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        ClassBlock block = parseClassBlock(type);
        return new ClassDeclarationStatement(pf, name!=null ? name.getString() : "", isNative, types, type, vis, block, extending, implementations, annotationDefinitions);
    }
    private ArrayList<String> parseCustomTypesDeclaration(){
        ArrayList<String> types = new ArrayList<>();
        consume(TokenType.OPEN_ANGLE_BRACKET, "Expected <");
        while (!match(TokenType.CLOSE_ANGLE_BRACKET)){
            if(isAtEnd()){
                unexpected(eof, ">");
                return types;
            }
            if(match(TokenType.IDENTIFIER)){
                types.add(previousSafe().string);
            }
            if(peek().type != TokenType.CLOSE_ANGLE_BRACKET){
                consume(TokenType.COMMA, "Expected ,");
            }
        }
        return types;
    }
    private ClassBlock parseClassBlock(ClassType type) {
        switch (type) {
            case ABSTRACT:
                return parseAbstractClassBlock();
            case CLASS:
            case ANNOTATION:
                return parseBaseClassBlock();
            case ENUM:
                return parseEnumClassBlock();
            case INTERFACE:
                return parseInterfaceClassBlock();
        }
        return null;
    }

    private ClassBlock parseEnumClassBlock() {
        ArrayList<String> values = new ArrayList<>();
        ArrayList<Statement> statements = new ArrayList<>();
        consume(TokenType.OPEN_BRACE, "Expected {");
        while (!match(TokenType.CLOSE_BRACE)) {
            if (isAtEnd()) {
                unexpected(eof, "}");
                return new EnumClassBlock(values, asClassBlock(statements));
            }

            Token n = advance();
            values.add(n.getString());
            if(match(TokenType.SEMICOLON)){

                while (!match(TokenType.CLOSE_BRACE)) {
                    Statement dec = parseClassInsideDeclaration();

                    if (dec instanceof MethodDeclarationStatement) {
                        MethodDeclarationStatement m = (MethodDeclarationStatement) dec;
                        if (m.isAbstract && !m.isNative) {
                            thr(dec.getToken(), "Enum classes cannot include abstract methods");
                        }
                    }
                    statements.add(dec);
                }
                break;
            }
            if (match(TokenType.CLOSE_BRACE)) {
                break;
            } else if (!match(TokenType.COMMA)) {
                unexpected(peek(), ",");
            }
        }
        return new EnumClassBlock(values, asClassBlock(statements));
    }

    private ClassBlock parseInterfaceClassBlock() {
        ArrayList<Statement> statements = new ArrayList<>();
        consume(TokenType.OPEN_BRACE, "Expected {");
        while (!match(TokenType.CLOSE_BRACE)) {
            Statement dec = parseClassInsideDeclaration();
            if (dec instanceof ConstructorDeclarationStatement) {
                thr(dec.getToken(), "Interfaces cannot include a constructor");
            }
            if (dec instanceof MethodDeclarationStatement) {
                if (!((MethodDeclarationStatement) dec).isAbstract) {
                    thr(dec.getToken(), "Interfaces cannot include non abstract methods");
                }
            }
            statements.add(dec);
        }
        return asClassBlock(statements);
    }

    private ClassBlock parseBaseClassBlock() {
        ArrayList<Statement> statements = new ArrayList<>();
        consume(TokenType.OPEN_BRACE, "Expected {");
        while (!match(TokenType.CLOSE_BRACE)) {
            Statement dec = parseClassInsideDeclaration();
            if (dec instanceof MethodDeclarationStatement) {
                MethodDeclarationStatement m = (MethodDeclarationStatement) dec;
                if (m.isAbstract && !m.isNative) {
                    thr(dec.getToken(), "Classes cannot include abstract methods");
                }
            }
            statements.add(dec);
        }
        return asClassBlock(statements);
    }

    private ClassBlock parseAbstractClassBlock() {
        ArrayList<Statement> statements = new ArrayList<>();
        consume(TokenType.OPEN_BRACE, "Expected {");
        while (!match(TokenType.CLOSE_BRACE)) {
            statements.add(parseClassInsideDeclaration());
        }
        return asClassBlock(statements);
    }

    private ClassBlock asClassBlock(ArrayList<Statement> statements) {
        ArrayList<VariableDeclarationStatement> variables = new ArrayList<>();
        ArrayList<MethodDeclarationStatement> methods = new ArrayList<>();
        ArrayList<ConstructorDeclarationStatement> constructors = new ArrayList<>();
        ArrayList<ClassDeclarationStatement> classes = new ArrayList<>();
        ArrayList<OperatorOverloadStatement> operators = new ArrayList<>();
        for (Statement statement : statements) {
            if (statement instanceof ConstructorDeclarationStatement) {
                constructors.add((ConstructorDeclarationStatement) statement);
            } else if (statement instanceof MethodDeclarationStatement) {
                methods.add((MethodDeclarationStatement) statement);
            } else if (statement instanceof ClassDeclarationStatement) {
                classes.add((ClassDeclarationStatement) statement);
            } else if (statement instanceof VariableDeclarationStatement) {
                variables.add((VariableDeclarationStatement) statement);
            } else if (statement instanceof OperatorOverloadStatement) {
                operators.add((OperatorOverloadStatement) statement);
            }
        }
        return new ClassBlock(variables, methods, constructors, classes, operators);
    }

    private Statement parseClassInsideDeclaration() {
        Statement statement = parseStatement();
        if (!(statement instanceof ClassDeclarationStatement) && !(statement instanceof OperatorOverloadStatement) && !(statement instanceof MethodDeclarationStatement)
                && !(statement instanceof VariableDeclarationStatement) && !(statement instanceof EmptyStatement)) {
            unexpectedStatement(statement, "Class declaration, operator overloading, method declaration, or variable declaration");
        }
        return statement;
    }

    private Statement parseExpressionStatement() {
        Token pf = peek();


        Expression exp = parseExpression();
        if (!(exp instanceof Statement)) {
            //return Statement.ofExpression(exp);
            unexpected(pf, "Statement");
            return null;
        }
        return (Statement) exp;
    }



    private ForiStatement parseForiStatement() {
        Token pf = advance();
        boolean opened = match(TokenType.OPEN_PARENTHESES);

        VariableDeclarationStatement init = parseVariableArgumentDeclaration();
        consume(TokenType.SEMICOLON, "Expected ;");
        Expression initValue = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ;");
        Expression maxValue = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ;");
        Expression stepValue = parseExpression();

        if (opened) {
            consume(TokenType.CLOSE_PARENTHESES, "Expected )");
        }
        Statement code = parseStatement();
        return new ForiStatement(pf, init, initValue, maxValue, stepValue, code);
    }

    private ForStatement parseForStatement() {
        Token pf = advance();
        boolean opened = match(TokenType.OPEN_PARENTHESES);

        Statement init = parseStatement(false);
        consume(TokenType.SEMICOLON, "Expected ;");
        Expression condition = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ;");
        Statement step = parseStatement(false);

        if (opened) {
            consume(TokenType.CLOSE_PARENTHESES, "Expected )");
        }
        Statement code = parseStatement();

        return new ForStatement(pf, init, condition, step, code);
    }

    private WhileStatement parseWhileStatement() {
        Token pf = advance();
        Expression condition = parseExpression();
        Statement body = parseStatement();
        return new WhileStatement(pf, condition, body);
    }

    private DoWhileStatement parseDoWhileStatement() {
        Token pf = advance();
        Statement body = parseStatement();
        consume(TokenType.WHILE, "While keyword");
        Expression condition = parseExpression();
        return new DoWhileStatement(pf, condition, body);
    }

    private ForeachStatement parseForeachStatement() {
        Token pf = advance();
        boolean opened = match(TokenType.OPEN_PARENTHESES);
        VariableDeclarationStatement variable = parseVariableArgumentDeclaration();
        consume(TokenType.COLON, "Expected :");
        Expression array = parseExpression();
        if (opened) {
            consume(TokenType.CLOSE_PARENTHESES, "Expected )");
        }
        Statement code = parseStatement();
        return new ForeachStatement(pf, variable, array, code);
    }

    private ThrowStatement parseThrowStatement() {
        Token pf = advance();
        Expression exp = parseExpression();
        return new ThrowStatement(pf, exp);
    }
    private TryStatement parseTryStatement() {
        Token pf = advance();
        Statement body = parseStatement();
        List<CatchNode> nodes = parseCatchNodes();
        return new TryStatement(pf, body, nodes);
    }
    private List<CatchNode> parseCatchNodes(){
        if(peek().type != TokenType.CATCH){
            unexpected(peek(), "catch");
            return new ArrayList<>();
        }
        List<CatchNode> nodes = new ArrayList<>();
        while (!isAtEnd() && peek().type == TokenType.CATCH){
            nodes.add(parseCatchNode());
        }
        return nodes;
    }
    private CatchNode parseCatchNode(){
        if(!match(TokenType.CATCH)){
            unexpected(peek(), "catch");
        }
        List<VariableDeclarationStatement> vars = parseArgumentsOfMethodDeclaration();
        Statement body = parseStatement();
        return new CatchNode(vars, body);
    }
    private IfStatement parseIfStatement() {
        Token pf = advance();
        Expression condition = parseExpression();
        Statement success = parseStatement();
        Statement fail = null;
        if (match(TokenType.ELSE)) {
            fail = parseStatement();
        }
        return new IfStatement(pf, condition, success, fail);
    }

    private ReturnStatement parseReturn() {
        Token pf = advance();
        Expression exp = parseExpression();
        return new ReturnStatement(pf, exp);
    }

    private ExitStatement parseExit() {
        Token pf = advance();
        Expression exp = parseExpression();
        return new ExitStatement(pf, exp);
    }

    private ErrorStatement parseError() {
        Token pf = advance(); // out ;
        if(peek().type == TokenType.SEMICOLON){
            return new ErrorStatement(pf);
        }
        Expression exp = parseExpression();
        return new ErrorStatement(pf, exp);
    }
    private OutputStatement parseOutput() {
        Token pf = advance(); // out ;
        if(peek().type == TokenType.SEMICOLON){
            return new OutputStatement(pf);
        }
        Expression exp = parseExpression();
        return new OutputStatement(pf, exp);
    }

    private MethodDeclarationStatement parseMethodDeclaration() {
        boolean isNative = match(TokenType.NATIVE);

        Token pf = peek();
        ArrayList<Visibility> vis = new ArrayList<>();
        if (isVisibility(pf.getType())) {
            vis = parseVisibilities(true);
        }

        boolean isAbstract = false;
        Token at = null;
        if (match(TokenType.ABSTRACT)) {
            isAbstract = true;
            at = previous();
        }else if(match(TokenType.OVERRIDE)){
            vis.add(Visibility.OVERRIDE);
        }
        boolean constructor = peek().getType() == TokenType.IDENTIFIER && peekSafe(1).getType() == TokenType.OPEN_PARENTHESES;
        Type returnType = null;
        if (!isAtEnd() && !constructor && isType(peek().getType(), true)) {
            returnType = parseType(false);
        }
        List<Expression> callSuper;
        Token name = advance();



        ArrayList<VariableDeclarationStatement> args = parseArgumentsOfMethodDeclaration();
        BlockStatement code = null;
        callSuper = parseSuper();
        if (!isAtEnd() && peek().getType() == TokenType.OPEN_BRACE && !isAbstract && !isNative) {
            code = parseCodeBlock();
        } else if (isAtEnd()) {
            if (isAbstract) {
                unexpected(eof, ";");
            } else {
                unexpected(eof, "{");
            }
            code = new BlockStatement(eof, new ArrayList<>());
        } else {
            consume(TokenType.SEMICOLON, "Expected ;");
            at = previous();
            isAbstract = !isNative;
        }

        if (returnType == null && constructor) {
            if (isAbstract) {
                unexpected(at, "Constructor cannot be abstract");
            }
            return new ConstructorDeclarationStatement(pf, name.getString(), null, vis, args, code, callSuper);
        }
        return new MethodDeclarationStatement(pf, name.getString(), returnType, vis, args, code, isAbstract, isNative);
    }

    private List<Expression> parseSuper(){
        List<Expression> callSuper = null;
        if(match(TokenType.COLON)){
            if(!match(TokenType.SUPER)){
                unexpected(peek(), "super");
            }
            callSuper = parseExpressionList();
        }
        return callSuper;
    }

    private ArrayList<Visibility> parseVisibilities(boolean realPick) {
        ArrayList<Visibility> vis = new ArrayList<>();
        while (isVisibility(peek().getType())) {
            vis.add(parseVisibility());
            if(realPick) advance();
        }
        return vis;
    }

    private Visibility parseVisibility() {
        Token token = peek();
        switch (token.getType()) {
            case PUBLIC:
                return Visibility.PUBLIC;
            case PRIVATE:
                return Visibility.PRIVATE;
            case STATIC:
                return Visibility.STATIC;
            case READONLY:
                return Visibility.READONLY;
            case FINAL:
                return Visibility.FINAL;
        }
        unexpected(token, "Visibility");
        return Visibility.PRIVATE;
    }

    private BlockStatement parseCodeBlock() {
        Token pf = peek();
        ArrayList<Statement> statements = new ArrayList<>();
        if(!match(TokenType.OPEN_BRACE)){
            thr(peek(), "Excepted {");
            return new BlockStatement(pf, new ArrayList<>());
        }
        while (!match(TokenType.CLOSE_BRACE)) {
            statements.add(parseStatement());
            if (isAtEnd()) {
                unexpected(eof, "}");
                return new BlockStatement(pf, statements);
            }
        }
        return new BlockStatement(pf, statements);
    }

    private ArrayList<VariableDeclarationStatement> parseArgumentsOfMethodDeclaration(){
        return parseArgumentsOfMethodDeclaration(false);
    }
    private ArrayList<VariableDeclarationStatement> parseArgumentsOfMethodDeclaration(boolean nullOnError) {
        ArrayList<VariableDeclarationStatement> expressions = new ArrayList<>();
        if (!match(TokenType.OPEN_PARENTHESES)) {
            if (nullOnError) return null;
            unexpected(peek(), "Open parentheses");
        }
        while (!match(TokenType.CLOSE_PARENTHESES)) {
            expressions.add(parseVariableArgumentDeclaration(nullOnError));
            if (isAtEnd()) {
                if (nullOnError) return null;
                unexpected(eof, "Close angle bracket");
                return expressions;
            }
            if (peek().getType() != TokenType.CLOSE_PARENTHESES) {
                if (!match(TokenType.COMMA)) {
                    if (nullOnError) return null;
                    unexpected(emptyToken(previousSafe().getPosition()), "Comma");
                }
            }
        }
        if (previousSafe().getType() != TokenType.CLOSE_PARENTHESES) {
            if (nullOnError) return null;
            unexpected(peek(), "Close angle bracket");
            return expressions;
        }
        return expressions;
    }

    private VariableDeclarationStatement parseVariableArgumentDeclaration(){
        return parseVariableArgumentDeclaration(false);
    }
    private VariableDeclarationStatement parseVariableArgumentDeclaration(boolean nullOnError) {
        Token tf = peek();
        PsiMarker marker = listener.mark();
        Type type = parseType(nullOnError);
        Token name = null;
        if (!match(TokenType.IDENTIFIER)) {
            if (nullOnError) return null;
            unexpected(peek(), "Variable name");
        }else{
            name = previous();
        }
        return marker.touch(new VariableDeclarationStatement(tf, type, name == null ? "" : name.getString(), new ArrayList<>(), null));
    }

    private MethodCallStatement parseMethodCall() {
        Token name = advance();
        Expression left = new IdentifierExpression(name, name.string);
        while (peek().type == TokenType.OPEN_PARENTHESES){
            left = new MethodCallStatement(name, left, parseExpressionList());
        }
        if(left instanceof MethodCallStatement) return (MethodCallStatement) left;
        return null;
    }

    private ArrayList<Expression> parseExpressionList() {
        ArrayList<Expression> expressions = new ArrayList<>();
        if (!match(TokenType.OPEN_PARENTHESES)) {
            unexpected(peek(), "Open parentheses");
        }
        while (!match(TokenType.CLOSE_PARENTHESES)) {
            expressions.add(parseExpression());
            if (isAtEnd()) {
                unexpected(eof, "Close angle bracket");
                return expressions;
            }
            if (peek().getType() != TokenType.CLOSE_PARENTHESES) {
                if (!match(TokenType.COMMA)) {
                    unexpected(emptyToken(previousSafe().getPosition()), "Comma");
                }
            }
        }
        if (previousSafe().getType() != TokenType.CLOSE_PARENTHESES) {
            unexpected(peek(), "Close angle bracket");
            return expressions;
        }
        return expressions;
    }

    public boolean nextOperator(){
        int initialPosition = current;
        if(isVisibility(peek().getType()))return false;
        if (parseMethodMiddle(initialPosition)) return false;
        if(match(TokenType.OPERATOR)){
            current = initialPosition;
            return true;
        }

        current = initialPosition;
        return false;
    }

    private boolean parseMethodMiddle(int initialPosition) {
        if(match(TokenType.OVERRIDE)){
            current = initialPosition;
            return true;
        }
        Type returnType = null;
        if (!isAtEnd() && isType(peek().getType(), true)) {
            returnType = parseType(false);
        }
        if(returnType == null){
            current = initialPosition;
            return true;
        }
        return false;
    }

    public boolean nextMethod(){
        int initialPosition = current;
        match(TokenType.NATIVE);
        parseVisibilities(true);
        if(match(TokenType.OVERRIDE)){
            current = initialPosition;
            return true;
        }
        if(peek().getType() == TokenType.IDENTIFIER && peekSafe(1).getType() == TokenType.OPEN_PARENTHESES){
            current = initialPosition;
            return true;
        }
        if (!isAtEnd() && isType(peek().getType(), true)) {
            parseType(false);
        }
        if(isAtEnd()){
            unexpected(eof, "method name");
            return false;
        }
        if(peek().getType() != TokenType.IDENTIFIER){
            current = initialPosition;
            return false;
        }
        advance();
        if(match(TokenType.OPEN_PARENTHESES)){
            current = initialPosition;
            return true;
        }
        current = initialPosition;
        return false;
    }
    public boolean nextVariable(){
        int initialPosition = current;
        if(isVisibility(peek().getType())) parseVisibilities(true);
        if (parseMethodMiddle(initialPosition)) return false;
        if(isAtEnd()){
            unexpected(eof, "variable name");
            return false;
        }
        if(peek().getType() != TokenType.IDENTIFIER){
            current = initialPosition;
            return false;
        }
        advance();
        if(match(TokenType.OPEN_PARENTHESES)){
            current = initialPosition;
            return false;
        }
        if(match(TokenType.SEMICOLON) || match(TokenType.EQUAL)){
            current = initialPosition;
            return true;
        }
        current = initialPosition;
        return false;
    }
    public boolean nextClass(){
        int initialPosition = current;
        annotationDefinitions();
        if(isVisibility(peek().getType())) parseVisibilities(true);
        if(match(TokenType.OVERRIDE)){
            current = initialPosition;
            return false;
        }
        match(TokenType.NATIVE);
        if(isClassDefinition(peek().getType())){
            current = initialPosition;
            return true;
        }
        current = initialPosition;
        return false;
    }

    private List<AnnotationDefinition> annotationDefinitions(){
        List<AnnotationDefinition> result = new ArrayList<>();
        while (peek().type == TokenType.ANNOTATION){
            result.add(annotationDefinition());
        }
        return result;
    }
    private AnnotationDefinition annotationDefinition(){
        if(match(TokenType.ANNOTATION)){
            Expression annotation = parsePrimary();
            List<Expression> arguments = new ArrayList<>();
            if(peek().type == TokenType.OPEN_PARENTHESES){
                arguments = parseExpressionList();
            }
            return new AnnotationDefinition(annotation, arguments);
        }
        throw new RuntimeException("Invalid annotation definition");
    }

    public OperatorOverloadStatement parseOperatorMethod(){
        Token pf = peek();
        if(match(TokenType.IMPLICIT, TokenType.EXPLICIT)){
            boolean implicit = pf.type == TokenType.IMPLICIT;
            consume(TokenType.OPERATOR, "Expected \"operator\" after implicit/explicit");
            Type castTo = parseType();
            consume(TokenType.OPEN_PARENTHESES, "Expected (");
            VariableDeclarationStatement vds = parseVariableArgumentDeclaration();
            consume(TokenType.CLOSE_PARENTHESES, "Expected )");
            BlockStatement bs = parseCodeBlock();
            return new CastOverloadStatement(pf, bs, implicit, vds, castTo);
        }
        if(isType(pf.type)) {
            PsiMarker marker = listener.mark();
            Type type = parseType();
            marker.touch(type.type);
            consume(TokenType.OPERATOR, "Expected \"operator\" after type");

            Token ppf = peek();
            String operation = parseBinaryOperation();
            if (operation == null) {
                unexpected(ppf, "Binary operator");
                return new ArithmeticOverloadStatement(pf, new BlockStatement(ppf, new ArrayList<>()), type, "+", new ArrayList<>());
            }
            ArrayList<VariableDeclarationStatement> args = parseArgumentsOfMethodDeclaration();
            BlockStatement bs = parseCodeBlock();
            return new ArithmeticOverloadStatement(pf, bs, type, operation, args);

        }
        unexpected(pf, "Invalid overloading statement");
        return null;
    }
    private String parseBinaryOperation(){
        TokenType type = peek().type;
        switch (type) {
            case PLUS:
            case MINUS:
            case DIVIDE:
            case STAR:
            case MODULO:
            case POW:
            case BIT_AND:
            case BIT_OR:
            case BIT_XOR:
            case EQUALS:
            case NOT_EQUAL:
            case GREATER_OR_EQUAL:
            case LESS_OR_EQUAL:
                return advance().string;
            case OPEN_ANGLE_BRACKET:
            case CLOSE_ANGLE_BRACKET:
                advance();
                if(match(type)){
                    if(match(type)){
                        if(Objects.equals(previousSafe().string, "<")){
                            return null;
                        }
                        assert previous() != null;
                        return previousSafe().string+previousSafe().string+previousSafe().string;
                    }
                    return previousSafe().string+previousSafe().string;
                }else{
                    return previousSafe().string;
                }
            case NOT:
                if(match(TokenType.EQUAL))return "!=";
        }
        return null;
    }
    private VariableDeclarationStatement parseVariableDeclaration() {
        Token tf = peek();
        ArrayList<Visibility> vis = parseVisibilities(true);
        Type type = parseType();
        Token name = null;
        if (!match(TokenType.IDENTIFIER)) {
            unexpected(peek(), "Variable name");
        }else{
            name = previous();
        }
        Expression value = null;
        if (match(TokenType.EQUAL)) {
            value = parseExpression();
        }
        return new VariableDeclarationStatement(tf, type, name == null ? "" : name.getString(), vis, value);
    }

    private ArrayList<Type> parseCustomTypes(boolean nullOnError) {
        if (!match(TokenType.OPEN_ANGLE_BRACKET)) {
            if (nullOnError) return null;
            unexpected(peek(), "Open angle bracket");
            return new ArrayList<>();
        }
        ArrayList<Type> types = new ArrayList<>();
        while (!match(TokenType.CLOSE_ANGLE_BRACKET)) {
            types.add(parseType());
            if (isAtEnd()) {
                if (nullOnError) return null;
                unexpected(eof, "Close angle bracket");
                return types;
            }
            if (peek().getType() != TokenType.CLOSE_ANGLE_BRACKET) {
                if (!match(TokenType.COMMA)) {
                    if (nullOnError) return null;
                    unexpected(emptyToken(previousSafe().getPosition()), "Comma");
                }
            }
        }
        if (previousSafe().getType() != TokenType.CLOSE_ANGLE_BRACKET) {
            if (nullOnError) return null;
            unexpected(peek(), "Close angle bracket");
            return types;
        }
        return types;
    }

    private Type parseType(){
        return parseType(false);
    }
    private Type parseType(boolean nullOnError) {
        Type type;
        ArrayList<Type> subTypes = new ArrayList<>();
        Token tokenOfType = advance();
        if (!isAtEnd() && peek().getType() == TokenType.OPEN_ANGLE_BRACKET) {
            if(peek(1) != null && peekSafe(1).type == TokenType.OPEN_ANGLE_BRACKET) {
                moveBack(1);
                return null;
            }
            subTypes = parseCustomTypes(nullOnError);
        }
        if (tokenOfType.getType() == TokenType.IDENTIFIER) {
            type = new Type(tokenOfType, subTypes, new IdentifierExpression(tokenOfType, tokenOfType.getString()));
        } else {
            switch (tokenOfType.getType()) {
                case INTEGER:
                    type = new Type(tokenOfType, Primitives.INTEGER);
                    break;
                case CHAR:
                    type = new Type(tokenOfType, Primitives.CHAR);
                    break;
                case FLOAT:
                    type = new Type(tokenOfType, Primitives.FLOAT);
                    break;
                case BOOL:
                    type = new Type(tokenOfType, Primitives.BOOL);
                    break;
                case TBOOL:
                    type = new Type(tokenOfType, Primitives.TBOOL);
                    break;
                case VOID:
                    type = new Type(tokenOfType, Primitives.VOID);
                    break;
                default:
                    if (nullOnError) return null;
                    unexpected(tokenOfType, "Primitive type");
                    type = new Type(tokenOfType, Primitives.NULL);
            }
        }
        return type;
    }

    public Expression parseExpression() {
        if (isAtEnd()) {
            unexpected(eof, "Expression");
            return new Expression(eof) {
                @Override
                public Value eval(Context context) {
                    return Value.nullValue();
                }
            };
        }
        return parseCasting();
    }

    private Expression parseCasting() {
        Token pf = peek();
        if (pf != null && pf.getType() == TokenType.OPEN_PARENTHESES) {
            Token lg = peek(1);
            if (lg == null) {
                unexpected(null, "Type to cast");
                return new Expression(eof) {
                    @Override
                    public Value eval(Context context) {
                        return Value.nullValue();
                    }
                };
            }
            if (isType(lg.getType())) {
                Token lt = peek(2);
                if (lt != null && lt.getType() == TokenType.CLOSE_PARENTHESES) {
                    PsiMarker marker = listener.mark();
                    advance();
                    Type type = parseType();
                    advance();
                    Expression expr = parseExpression();
                    return marker.touch(new CastExpression(lg, type, expr));
                }
            }
        }
        return parseAssignment();
    }

    private Expression parseAssignment() {
        PsiMarker marker = listener.mark();
        Expression expression = parseHardArray();
        if (!isAtEnd()) {
            if (isAssignOperator(peek().getType())) {
                Token equals = advance();
                Expression value = parseExpression();

                if (expression instanceof IdentifierExpression || expression instanceof MemberStatement || expression instanceof UnaryImplicitExpression) {
                    return marker.touch(new AssigmentStatement(expression.getToken(), expression, equals.string, value));
                }

                unexpected(equals, "Invalid assignment target.");
            }
        }

        return marker.touch(expression);
    }





    private Expression parseHardArray() {
        PsiMarker marker = listener.mark();
        Token pf = peek();
        if (match(TokenType.OPEN_SQUARE_BRACKET)) {
            ArrayList<Expression> items = new ArrayList<>();
            while (!check(TokenType.CLOSE_SQUARE_BRACKET)) {
                Expression right = parseExpression();
                items.add(right);
                if (isAtEnd()) {
                    unexpected(right.getToken(), "]");
                    return marker.touch(new ListExpression(pf, items));
                }
                if (peek().getType() != TokenType.CLOSE_SQUARE_BRACKET) {
                    consume(TokenType.COMMA, "Expected ,");
                }
            }
            consume(TokenType.CLOSE_SQUARE_BRACKET, "Expected ]");
            return marker.touch(new ListExpression(pf, items));
        }

        return marker.touch(parseLogical());
    }

    private Expression parseLogical() {
        PsiMarker marker = listener.mark();
        Expression left = parseBitLogical();
        if (peek(1) != null) {
            if (peek().getType() == TokenType.OPEN_ANGLE_BRACKET && peekSafe(1).getType() != TokenType.OPEN_ANGLE_BRACKET) {
                advance();
                Expression right = parseExpression();
                return marker.touch(new LogicalExpression(left.getToken(), left, "<", right));
            }
            if (peek().getType() == TokenType.CLOSE_ANGLE_BRACKET && peekSafe(1).getType() != TokenType.CLOSE_ANGLE_BRACKET) {
                advance();
                Expression right = parseExpression();
                return marker.touch(new LogicalExpression(left.getToken(), left, ">", right));
            }
        }
        while (match(TokenType.NOT_EQUAL, TokenType.EQUALS, TokenType.LESS_OR_EQUAL, TokenType.GREATER_OR_EQUAL)) {
            Token operator = previousSafe();
            Expression right = parseBitLogical();
            left = marker.touch(new LogicalExpression(left.getToken(), left, operator.getString(), right));
        }
        while (match(TokenType.OR, TokenType.AND)) {
            Token operator = previousSafe();
            Expression right = parseExpression();
            left = marker.touch(new LogicalExpression(left.getToken(), left, operator.getString(), right));
        }

        return marker.touch(left);
    }

    private Expression parseBitLogical() {
        PsiMarker marker = listener.mark();
        Expression left = parseBitBinary();

        while (match(TokenType.BIT_OR, TokenType.BIT_AND, TokenType.BIT_XOR)) {
            Token operator = previousSafe();
            Expression right = parseBitLogical();
            left = marker.touch(new BinaryExpression(left.getToken(), left, operator.getString(), right));
        }

        return marker.touch(left);
    }

    private Expression parseBitBinary() {
        PsiMarker marker = listener.mark();
        Expression left = parseAddition();
        if ( peek(3) != null) {
            String parsed = parseBitBinaryOperator(peek().type, peekSafe(1).type, peekSafe(2).type);
            if(parsed != null){
                for (int i = 0; i < parsed.toCharArray().length; i++) {
                    advance();
                }
                Expression right = parseBitLogical();
                return marker.touch(new BinaryExpression(left.getToken(), left, parsed, right));
            }
        }
        return marker.touch(left);
    }
    public String parseBitBinaryOperator(TokenType p, TokenType c, TokenType n){
        if (p == TokenType.OPEN_ANGLE_BRACKET && c == TokenType.OPEN_ANGLE_BRACKET) {
            return "<<";

        }
        if (p == TokenType.CLOSE_ANGLE_BRACKET && c == TokenType.CLOSE_ANGLE_BRACKET) {
            boolean tp = n == TokenType.CLOSE_ANGLE_BRACKET;
            return ">>"+(tp ? ">" : "");
        }
        return null;
    }

    private Expression parseAddition() {
        PsiMarker marker = listener.mark();
        Expression left = parseMultiplication();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previousSafe();
            Expression right = parseBitLogical();
            left = marker.touch(new BinaryExpression(left.getToken(), left, operator.getString(), right));
        }

        return marker.touch(left);
    }

    private Expression parseMultiplication() {
        PsiMarker marker = listener.mark();
        Expression left = parseExponentiation();

        while (match(TokenType.STAR, TokenType.DIVIDE, TokenType.MODULO)) {
            Token operator = previousSafe();
            Expression right = parseExponentiation();
            left = marker.touch(new BinaryExpression(left.getToken(), left, operator.getString(), right));
        }

        return marker.touch(left);
    }

    private Expression parseExponentiation() {
        PsiMarker marker = listener.mark();
        Expression left = parseUnary();

        while (match(TokenType.POW)) {
            Token operator = previousSafe();
            Expression right = parseBitLogical();
            left = marker.touch(new BinaryExpression(left.getToken(), left, operator.getString(), right));
        }

        return marker.touch(left);
    }

    private Expression parseUnary() {
        PsiMarker marker = listener.mark();
        if (match(TokenType.MINUS, TokenType.NOT, TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = previousSafe();
            Expression right = parseBitUnary();
            return marker.touch(new UnaryStatement(right.getToken(), operator.string, right, true));
        }
        Expression left = parseBitUnary();
        if (match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = previousSafe();
            return marker.touch(new UnaryStatement(left.getToken(), operator.string, left, false));
        }
        return marker.touch(left);
    }

    private Expression parseBitUnary() {
        PsiMarker marker = listener.mark();
        if (match(TokenType.BIT_NOT)) {
            Token operator = previousSafe();
            Expression right = parseBitLogical();
            return marker.touch(new UnaryStatement(right.getToken(), operator.string, right, true));
        }
        return marker.touch(parseClassInstantiation());
    }
    private Expression parseClassInstantiation() {
        PsiMarker marker = listener.mark();
        Token pf = peek();
        if (match(TokenType.NEW)) {
            Expression clazz = parseMembers(true);
            List<Type> types = new ArrayList<>();
            if(peek().type == TokenType.OPEN_ANGLE_BRACKET){
                types = parseCustomTypes(false);
            }
            ArrayList<Expression> args = parseExpressionList();
            return marker.touch(new ClassInstantiationStatement(pf, clazz, types, args));
        }
        return marker.touch(parseMembers());
    }
    private Expression parseMembers(){return parseMembers(false);}
    private Expression parseMembers(boolean ignoreMethodCallCheck) {
        PsiMarker marker = listener.mark();
        Expression left = ignoreMethodCallCheck ? parsePrimary() : parseMethodCallExpression();

        while (match(TokenType.DOT)) {

            Expression right = parseMethodCallExpression();
            left = marker.touch(new MemberStatement(left.getToken(), left, right));
        }

        return marker.touch(left);
    }


    private Expression parseMethodCallExpression() {
        PsiMarker marker = listener.mark();
        Expression name = parseArrayItem();
        if (!isAtEnd()) {
            Expression before = name;
            while (peek().type == TokenType.OPEN_PARENTHESES){
                ArrayList<Expression> el = parseExpressionList();
                before = marker.touch(new MethodCallStatement(name.getToken(), before, el));
            }

            if(before instanceof MethodCallStatement) return before;
        }
        return marker.touch(name);
    }
    private Expression parseArrayItem() {
        PsiMarker marker = listener.mark();
        Token pf = peek();
        Expression left = parsePrimary();
        if (match(TokenType.OPEN_SQUARE_BRACKET)) {
            Expression exp = parseExpression();
            consume(TokenType.CLOSE_SQUARE_BRACKET, "Expected ]");
            return marker.touch(new UnaryImplicitExpression(pf, left, UnaryImplicitOperationType.ARRAY_GET_ITEM, exp));
        }
        return marker.touch(left);
    }
    private Expression parsePrimary() {
        PsiMarker marker = listener.mark();
        if (this.match(TokenType.BOOL_LITERAL)) {
            return marker.touch(new BooleanExpression(this.previous(), Objects.equals(this.previousSafe().string, "true")));
        }
        if(this.match(TokenType.TBOOL_LITERAL)){
            return marker.touch(new TBooleanExpression(this.previous(), Objects.equals(this.previousSafe().string, "low") ? TBoolean.LOW: Objects.equals(this.previousSafe().string, "medium") ?TBoolean.MEDIUM:TBoolean.HIGH));
        }
        if (this.match(TokenType.INTEGER_LITERAL)) {
            return marker.touch(new IntegerExpression(this.previous(), Integer.parseInt(this.previousSafe().string)));
        }
        if (this.match(TokenType.FLOAT_LITERAL)) {
            String f = this.previousSafe().string;
            if(f.endsWith("f")) f = f.substring(0, f.length()-1);
            return marker.touch(new FloatExpression(this.previous(), Double.parseDouble(f)));
        }
        if (this.match(TokenType.STRING_LITERAL)) {
            return marker.touch(new StringExpression(this.previous(), Utils.unescapeString(Utils.removeFirstAndLastChar(this.previousSafe().string))));
        }
        if (this.match(TokenType.CHAR_LITERAL)) {
            return marker.touch(new CharExpression(this.previous(), this.previousSafe().string.charAt(0)));
        }
        if (this.match(TokenType.NULL)) {
            return marker.touch(new NullExpression(this.previous()));
        }
        LambdaExpression lambda = this.parseLambda();
        if(lambda != null) return marker.touch(lambda);
        if (peek().type == TokenType.OPEN_PARENTHESES) {
            if(peekSafe(1).type != TokenType.CLOSE_PARENTHESES){
                advance();
                PsiMarker m = listener.mark();
                LambdaExpression l = this.parseLambda();
                Expression expression = this.parseExpression();
                m.touch(expression);
                this.consume(TokenType.CLOSE_PARENTHESES, "Expected ')' after expression.");
                return marker.touch(new GroupingExpression(expression.token, expression));
            }
        }

        if (this.match(TokenType.IDENTIFIER)) {
            return marker.touch(new IdentifierExpression(this.previous(), this.previousSafe().string));
        }
        if(this.match(TokenType.CLASS)){
            return marker.touch(new IdentifierExpression(this.peek(), this.previousSafe().string));
        }
        if(this.isAtEnd()){
            this.unexpected(this.eof, "Expression");
            return marker.touch(new IdentifierExpression(eof, ""));
        }else {
            this.unexpected(this.peek(), "Expression");
            return marker.touch(new IdentifierExpression(this.peek(), ""));
        }

    }
    private LambdaExpression parseLambda() {
        if (peek(1) != null) {
            int rm = current;
            Token pf = peek();

            ArrayList<VariableDeclarationStatement> args = parseArgumentsOfMethodDeclaration(true);
            if (!match(TokenType.LAMBDA) || args == null) {
                current = rm;
                return null;
            }
            try {
                Expression e = parseExpression();
                return new LambdaExpression(pf, args, e);
            } catch (Exception ignored) {
            }
            Statement code = parseStatement(false);
            return new LambdaExpression(pf, args, code);
        }
        return null;
    }

    private boolean isClassDefinition(TokenType type) {
        switch (type) {
            case INTERFACE:
            case ANNOTATION:
            case CLASS:
            case ENUM:
            case ABSTRACT:
                return true;
        }
        return false;
    }

    private boolean isAssignOperator(TokenType type) {
        switch (type) {
            case EQUAL:
            case PLUS_EQUAL:
            case MINUS_EQUAL:
            case STAR_EQUAL:
            case DIVIDE_EQUAL:
            case BIT_OR_EQUAL:
            case BIT_AND_EQUAL:
            case BIT_XOR_EQUAL:
            case BIT_LEFT_SIGNED_SHIFT_EQUAL:
            case BIT_RIGHT_SIGNED_SHIFT_EQUAL:
            case BIT_RIGHT_UNSIGNED_SHIFT_EQUAL:
            case POW_EQUAL:
            case NULLABLE_EQUAL:
            case MODULO_EQUAL:
                return true;
        }
        return false;
    }


    private boolean isVisibility(TokenType type) {
        switch (type) {
            case PRIVATE:
            case PUBLIC:
            case READONLY:
            case FINAL:
            case STATIC:
                return true;
        }
        return false;
    }

    private boolean isType(TokenType type) {return isType(type, false);}
    private boolean isType(TokenType type, boolean voidInclude) {
        if(type == TokenType.VOID && voidInclude)return true;
        switch (type) {
            case CHAR:
            case INTEGER:
            case FLOAT:
            case BOOL:
            case TBOOL:
            case IDENTIFIER:
                return true;
        }
        return false;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private void consume(TokenType type, String message) {
        if (check(type)) {
            advance();
            return;
        }
        Token token = peek();
        if (token == null) {
            token = eof;
        }
        ASTError err = new ASTError(token, source, message);
        errors.add(err);
        if(!ignoreNonCritical){
            err.critical();
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().getType() == type;
    }


    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }


    private Token peek(){
        return peek(0);
    }
    private Token peek(int step) {
        if(tokens.size() <= current+step || step < 0) return null;
        return tokens.get(current + step);
    }
    private Token peekSafe(int step) {
        return Objects.requireNonNull(tokens.get(current + step));
    }

    private Token previous() {
        if(current-1 < 0) return null;
        return tokens.get(current - 1);
    }
    private Token previousSafe() {
        return tokens.get(current - 1);
    }

    private void moveBack(int step) {
        current -= step;
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }



    private void unexpectedToken(Token token) {
        if (token == null) {
            token = eof;
        }
        String string = "Unexpected " + token.string;
        ASTError err = new ASTError(token, source, string);
        errors.add(err);
        if(!ignoreNonCritical){
            err.critical();
        }
    }

    private void unexpected(Token token, String expected) {
        if (token == null) {
            token = eof;
        }
        String received = token.string;
        if (received.replace("\n", "").trim().isEmpty()) {
            received = null;
        }
        String string = expected + " expected, provided: " + token.string;
        if (received == null) {
            string = expected + " expected";
        }
        ASTError err = new ASTError(token, source, string);
        errors.add(err);
        if(!ignoreNonCritical){
            err.critical();
        }
    }

    private void thr(Token token, String string) {
        ASTError err = new ASTError(token, source, string);
        errors.add(err);
        if(!ignoreNonCritical) {
            err.critical();
        }
    }

    private void unexpectedStatement(Statement statement, String expected) {
        String string = expected + " expected, provided: " + statement.token.getString();

        ASTError err = new ASTError(statement.getToken(), source, string);
        errors.add(err);
        if(!ignoreNonCritical){
            err.critical();
        }
    }

}
