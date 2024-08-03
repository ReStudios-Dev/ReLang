/*
* GENERATED
*/
public native class ASTGenerator {
    /* line: 66, column: 5 */ private readonly array<Token> tokens;
    /* line: 67, column: 5 */ private readonly Token eof;
    /* line: 68, column: 5 */ private int current;
    /* line: 69, column: 5 */ private readonly array<ASTError> errors;
    /* line: 70, column: 5 */ private readonly str code;
    /* line: 71, column: 5 */ private readonly str source;
    /* line: 72, column: 5 */ private bool ignoreNonCritical;
    /* line: 73, column: 5 */ private PsiListener listener;


    /* line: 75, column: 5 */
    native public ASTGenerator(array<Token> tokens, str code, str source);

    /* line: 87, column: 5 */
    native public PsiListener getPSIListener();

    /* line: 92, column: 5 */
    native public ASTGenerator setPSIListener(PsiListener listener);

    /* line: 98, column: 5 */
    native public bool isIgnoreNonCritical();

    /* line: 102, column: 5 */
    native public ASTGenerator setIgnoreNonCritical(bool ignoreNonCritical);

    /* line: 108, column: 5 */
    native public array<ASTError> getAstErrors();

    /* line: 113, column: 5 */
    native private Token emptyToken(int pos);

    /* line: 118, column: 5 */
    native public BlockStatement parseProgram();

    /* line: 128, column: 5 */
    native public Statement parseStatement();

    /* line: 131, column: 5 */
    native public Statement parseStatement(bool checkSemicolon);

    /* line: 223, column: 5 */
    native public Statement parseClassDefinition();

    /* line: 273, column: 5 */
    native private array<str> parseCustomTypesDeclaration();

    /* line: 290, column: 5 */
    native private ClassBlock parseClassBlock(ClassType type);

    /* line: 304, column: 5 */
    native private ClassBlock parseEnumClassBlock();

    /* line: 340, column: 5 */
    native private ClassBlock parseInterfaceClassBlock();

    /* line: 358, column: 5 */
    native private ClassBlock parseBaseClassBlock();

    /* line: 374, column: 5 */
    native private ClassBlock parseAbstractClassBlock();

    /* line: 383, column: 5 */
    native private ClassBlock asClassBlock(array<Statement> statements);

    /* line: 405, column: 5 */
    native private Statement parseClassInsideDeclaration();

    /* line: 414, column: 5 */
    native private Statement parseExpressionStatement();

    /* line: 429, column: 5 */
    native private ForiStatement parseForiStatement();

    /* line: 448, column: 5 */
    native private ForStatement parseForStatement();

    /* line: 466, column: 5 */
    native private WhileStatement parseWhileStatement();

    /* line: 473, column: 5 */
    native private DoWhileStatement parseDoWhileStatement();

    /* line: 481, column: 5 */
    native private ForeachStatement parseForeachStatement();

    /* line: 494, column: 5 */
    native private ThrowStatement parseThrowStatement();

    /* line: 499, column: 5 */
    native private TryStatement parseTryStatement();

    /* line: 505, column: 5 */
    native private array<CatchNode> parseCatchNodes();

    /* line: 516, column: 5 */
    native private CatchNode parseCatchNode();

    /* line: 524, column: 5 */
    native private IfStatement parseIfStatement();

    /* line: 535, column: 5 */
    native private ReturnStatement parseReturn();

    /* line: 541, column: 5 */
    native private ExitStatement parseExit();

    /* line: 547, column: 5 */
    native private ErrorStatement parseError();

    /* line: 555, column: 5 */
    native private OutputStatement parseOutput();

    /* line: 564, column: 5 */
    native private MethodDeclarationStatement parseMethodDeclaration();

    /* line: 618, column: 5 */
    native private array<Expression> parseSuper();

    /* line: 629, column: 5 */
    native private array<Visibility> parseVisibilities(bool realPick);

    /* line: 638, column: 5 */
    native private Visibility parseVisibility();

    /* line: 656, column: 5 */
    native private BlockStatement parseCodeBlock();

    /* line: 673, column: 5 */
    native private array<VariableDeclarationStatement> parseArgumentsOfMethodDeclaration();

    /* line: 676, column: 5 */
    native private array<VariableDeclarationStatement> parseArgumentsOfMethodDeclaration(bool nullOnError);

    /* line: 704, column: 5 */
    native private VariableDeclarationStatement parseVariableArgumentDeclaration();

    /* line: 707, column: 5 */
    native private VariableDeclarationStatement parseVariableArgumentDeclaration(bool nullOnError);

    /* line: 721, column: 5 */
    native private MethodCallStatement parseMethodCall();

    /* line: 731, column: 5 */
    native private array<Expression> parseExpressionList();

    /* line: 755, column: 5 */
    native public bool nextOperator();

    /* line: 768, column: 5 */
    native private bool parseMethodMiddle(int initialPosition);

    /* line: 784, column: 5 */
    native public bool nextMethod();

    /* line: 815, column: 5 */
    native public bool nextVariable();

    /* line: 839, column: 5 */
    native public bool nextClass();

    /* line: 855, column: 5 */
    native public OperatorOverloadStatement parseOperatorMethod();

    /* line: 887, column: 5 */
    native private str parseBinaryOperation();

    /* line: 924, column: 5 */
    native private VariableDeclarationStatement parseVariableDeclaration();

    /* line: 941, column: 5 */
    native private array<Type> parseCustomTypes(bool nullOnError);

    /* line: 970, column: 5 */
    native private Type parseType();

    /* line: 973, column: 5 */
    native private Type parseType(bool nullOnError);

    /* line: 1015, column: 5 */
    native public Expression parseExpression();

    /* line: 1028, column: 5 */
    native private Expression parseCasting();

    /* line: 1056, column: 5 */
    native private Expression parseAssignment();

    /* line: 1079, column: 5 */
    native private Expression parseHardArray();

    /* line: 1102, column: 5 */
    native private Expression parseLogical();

    /* line: 1131, column: 5 */
    native private Expression parseBitLogical();

    /* line: 1144, column: 5 */
    native private Expression parseBitBinary();

    /* line: 1159, column: 5 */
    native public str parseBitBinaryOperator(TokenType p, TokenType c, TokenType n);

    /* line: 1171, column: 5 */
    native private Expression parseAddition();

    /* line: 1184, column: 5 */
    native private Expression parseMultiplication();

    /* line: 1197, column: 5 */
    native private Expression parseExponentiation();

    /* line: 1210, column: 5 */
    native private Expression parseUnary();

    /* line: 1225, column: 5 */
    native private Expression parseBitUnary();

    /* line: 1234, column: 5 */
    native private Expression parseClassInstantiation();

    /* line: 1248, column: 5 */
    native private Expression parseMembers();

    /* line: 1249, column: 5 */
    native private Expression parseMembers(bool ignoreMethodCallCheck);

    /* line: 1263, column: 5 */
    native private Expression parseMethodCallExpression();

    /* line: 1277, column: 5 */
    native private Expression parseArrayItem();

    /* line: 1288, column: 5 */
    native private Expression parsePrimary();

    /* line: 1342, column: 5 */
    native private LambdaExpression parseLambda();

    /* line: 1363, column: 5 */
    native private bool isClassDefinition(TokenType type);

    /* line: 1375, column: 5 */
    native private bool isAssignOperator(TokenType type);

    /* line: 1397, column: 5 */
    native private bool isVisibility(TokenType type);

    /* line: 1409, column: 5 */
    native private bool isType(TokenType type);

    /* line: 1410, column: 5 */
    native private bool isType(TokenType type, bool voidInclude);

    /* line: 1424, column: 5 */
    native private bool match(TokenType types);

    /* line: 1434, column: 5 */
    native private void consume(TokenType type, str message);

    /* line: 1450, column: 5 */
    native private bool check(TokenType type);

    /* line: 1458, column: 5 */
    native private Token advance();

    /* line: 1467, column: 5 */
    native private Token peek();

    /* line: 1470, column: 5 */
    native private Token peek(int step);

    /* line: 1474, column: 5 */
    native private Token peekSafe(int step);

    /* line: 1478, column: 5 */
    native private Token previous();

    /* line: 1482, column: 5 */
    native private Token previousSafe();

    /* line: 1486, column: 5 */
    native private void moveBack(int step);

    /* line: 1490, column: 5 */
    native private bool isAtEnd();

    /* line: 1496, column: 5 */
    native private void unexpectedToken(Token token);

    /* line: 1508, column: 5 */
    native private void unexpected(Token token, str expected);

    /* line: 1527, column: 5 */
    native private void thr(Token token, str string);

    /* line: 1535, column: 5 */
    native private void unexpectedStatement(Statement statement, str expected);


}