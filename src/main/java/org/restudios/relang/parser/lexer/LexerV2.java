package org.restudios.relang.parser.lexer;

import org.restudios.relang.parser.ast.LineCol;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.tokens.TokenType;
import org.restudios.relang.parser.utils.Utils;

import java.util.*;

public class LexerV2 {
    private final String source;

    private final String original;
    private final String text;
    private int position;
    private final LineCol eof;

    private final ArrayList<LexerError> lexerErrors = new ArrayList<>();

    public LexerV2(String source, String original) {
        this.source = source;
        this.original = original;
        this.text = original;
        this.position = 0;
        eof = Utils.findStringPosition(original, original.length());
    }

    @SuppressWarnings("unused")
    public ArrayList<LexerError> getLexerErrors() {
        return lexerErrors;
    }

    public ArrayList<Token> lex() {
        ArrayList<Token> tokens = new ArrayList<>();

        while (text.length() > position) {
            Token nextToken = next();
            position += nextToken.string.length();
            if(nextToken.type != TokenType.EMPTY && nextToken.type != TokenType.COMMENT) tokens.add(nextToken);
        }

        return tokens;
    }

    public Token next() {
        String buff = this.text.substring(this.position);

        Token keyword = parseKeyword(buff);
        if(keyword != null) return keyword;

        Token empty = parseEmpty(buff);
        if(empty != null) return empty;

        Token comment = parseComment(buff);
        if(comment != null) return comment;

        Token blockComment = parseBlockComment(buff);
        if(blockComment != null) return blockComment;

        Token stringLiteral = parseLiteralString(buff);
        if(stringLiteral != null) return stringLiteral;

        Token characterLiteral = parseLiteralCharacter(buff);
        if(characterLiteral != null) return characterLiteral;

        Token integerLiteral = parseIntegerLiteral(buff);
        if(integerLiteral != null) return integerLiteral;

        Token floatLiteral = parseFloatLiteral(buff);
        if(floatLiteral != null) return floatLiteral;

        Token special = parseSpecial(buff);
        if(special != null) return special;

        Token annotation = parseAnnotation(buff);
        if(annotation != null) return annotation;

        Token identifier = parseIdentifier(buff);
        if(identifier != null) return identifier;



        this.error("Unexpected");
        return null;
    }
    private Token parseAnnotation(String buff){
        if(!buff.startsWith("@")) return null;
        return new Token(TokenType.ANNOTATION, source, "@", position, lc(), lc(position + 1));
    }
    private Token parseIdentifier(String buff){
        StringBuilder all = new StringBuilder();
        if(!Character.isAlphabetic(buff.charAt(0)) && buff.charAt(0) != '_') return null;
        for (char c : buff.toCharArray()) {
            if(!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_') {
                break;
            }
            all.append(c);
        }
        return new Token(TokenType.IDENTIFIER, source, all.toString(), position, lc(), lc(position + all.length()));
    }
    private Token parseSpecial(String buff){
        LinkedHashMap<String, TokenType> special = new LinkedHashMap<>();
        special.put("==", TokenType.EQUALS);
        special.put("!=", TokenType.NOT_EQUAL);
        special.put("<=", TokenType.LESS_OR_EQUAL);
        special.put(">=", TokenType.GREATER_OR_EQUAL);
        special.put("&&", TokenType.AND);
        special.put("||", TokenType.OR);
        special.put("++", TokenType.INCREMENT);
        special.put("--", TokenType.DECREMENT);
        special.put("=", TokenType.EQUAL);
        special.put("...", TokenType.TRIPLE_DOT);
        special.put(".", TokenType.DOT);
        special.put(",", TokenType.COMMA);
        special.put(":", TokenType.COLON);
        special.put(";", TokenType.SEMICOLON);

        special.put("+=", TokenType.PLUS_EQUAL);
        special.put("-=", TokenType.MINUS_EQUAL);
        special.put("*=", TokenType.STAR_EQUAL);
        special.put("/=", TokenType.DIVIDE_EQUAL);

        special.put("|=", TokenType.BIT_OR_EQUAL);
        special.put("&=", TokenType.BIT_AND_EQUAL);
        special.put("^=", TokenType.BIT_XOR_EQUAL);
        special.put("<<=", TokenType.BIT_LEFT_SIGNED_SHIFT_EQUAL);
        special.put(">>=", TokenType.BIT_RIGHT_SIGNED_SHIFT_EQUAL);
        special.put(">>>=", TokenType.BIT_RIGHT_UNSIGNED_SHIFT_EQUAL);

        special.put("**=", TokenType.POW_EQUAL);
        special.put("??=", TokenType.NULLABLE_EQUAL);
        special.put("%=", TokenType.MODULO_EQUAL);

        special.put("->", TokenType.LAMBDA);

        special.put("**", TokenType.POW);
        special.put("?", TokenType.QUESTION_MARK);
        special.put("%", TokenType.MODULO);
        special.put("!", TokenType.NOT);
        special.put("#", TokenType.HEX);
        special.put("{", TokenType.OPEN_BRACE);
        special.put("}", TokenType.CLOSE_BRACE);
        special.put("(", TokenType.OPEN_PARENTHESES);
        special.put(")", TokenType.CLOSE_PARENTHESES);
        special.put("[", TokenType.OPEN_SQUARE_BRACKET);
        special.put("]", TokenType.CLOSE_SQUARE_BRACKET);
        special.put("<", TokenType.OPEN_ANGLE_BRACKET);
        special.put(">", TokenType.CLOSE_ANGLE_BRACKET);

        special.put("+", TokenType.PLUS);
        special.put("-", TokenType.MINUS);
        special.put("*", TokenType.STAR);
        special.put("/", TokenType.DIVIDE);

        special.put("|", TokenType.BIT_OR);
        special.put("&", TokenType.BIT_AND);
        special.put("~", TokenType.BIT_NOT);
        special.put("^", TokenType.BIT_XOR);

        for (Map.Entry<String, TokenType> kw : special.entrySet()) {
            if(buff.startsWith(kw.getKey())){
                return new Token(kw.getValue(), source, buff.substring(0, kw.getKey().length()), position,
                        lc(), lc(position+kw.getKey().length()));
            }
        }
        return null;
    }
    private Token parseIntegerLiteral(String buff) {
        if (Character.isDigit(buff.charAt(0))) {
            StringBuilder all = new StringBuilder();
            char[] characters = buff.toCharArray();
            int position = 0;

            // Hex check
            if (characters.length > 1 && characters[0] == '0' &&
                    (characters[1] == 'x' || characters[1] == 'X')) {
                // Skip "0x" or "0X"
                all.append("0x");
                position += 2;
                while (position < characters.length &&
                        (Character.isDigit(characters[position]) ||
                                (characters[position] >= 'a' && characters[position] <= 'f') ||
                                (characters[position] >= 'A' && characters[position] <= 'F'))) {
                    all.append(characters[position]);
                    position++;
                }
            }
            // Check e notation
            else if (buff.contains("e") || buff.contains("E")) {
                while (position < characters.length &&
                        (Character.isDigit(characters[position]) ||
                                characters[position] == '.')) {
                    all.append(characters[position]);
                    position++;
                }
                if (position < characters.length &&
                        (characters[position] == 'e' || characters[position] == 'E')) {
                    all.append(characters[position]);
                    position++;
                    if (position < characters.length &&
                            (characters[position] == '+' || characters[position] == '-')) {
                        all.append(characters[position]);
                        position++;
                    }
                    while (position < characters.length &&
                            Character.isDigit(characters[position])) {
                        all.append(characters[position]);
                        position++;
                    }
                }
            }
            // Decimals
            else {
                while (position < characters.length &&
                        Character.isDigit(characters[position])) {
                    all.append(characters[position]);
                    position++;
                }
            }
            String n = buff.substring(all.length());
            if(n.startsWith("f") || n.startsWith(".")) return null;
            return new Token(TokenType.INTEGER_LITERAL, source, all.toString(), position, lc(), lc(this.position + all.length()));
        }
        return null;
    }
    private Token parseFloatLiteral(String buff){
        if(Character.isDigit(buff.charAt(0))){
            StringBuilder all = new StringBuilder();
            char[] characters = buff.toCharArray();
            boolean hasDot = false;
            for (int i = 0; i < characters.length; i++) {
                if(!Character.isDigit(characters[i]) && characters[i] != '.' && characters[i] != 'f'){
                    break;
                }
                if(characters[i] == '.' && hasDot){
                    error("Unexpected dot", i);
                }
                if(characters[i] == '.'){
                    hasDot = true;
                }
                all.append(characters[i]);
                if(characters[i] == 'f') {
                    break;
                }
            }
            return new Token(TokenType.FLOAT_LITERAL, source, all.toString(), position, lc(), lc(position+all.length()));
        }
        return null;
    }
    private Token parseLiteralCharacter(String buff) {
        if (buff.startsWith("'")) {
            buff = buff.substring(1);
            StringBuilder string = new StringBuilder("'");
            char[] characters = buff.toCharArray();
            if (characters.length > 0) {
                if (characters[0] == '\\') {
                    if (characters.length > 2 && characters[2] == '\'') {
                        string.append("\\").append(characters[1]);
                    } else {
                        error("Expected '", 2);
                        return null;
                    }
                } else {
                    string.append(characters[0]);
                }
            } else {
                error("Excepted char", 1);
                return null;
            }
            if (characters.length == 1 && characters[0] != '\'') {
                error("Unexpected '", 1);
                return null;
            }
            string.append("'");
            return new Token(TokenType.CHAR_LITERAL, source, string.toString(), position, lc(), lc(position + string.length()));
        }
        return null;
    }

    private Token parseLiteralString(String buff){
        if(buff.startsWith("\"")){
            buff = buff.substring(1);
            StringBuilder string = new StringBuilder("\"");
            char[] characters = buff.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                if(i == 0 && characters[i] == '"') break;
                if(characters[i] == '"' && characters[i-1] != '\\')break;
                string.append(characters[i]);
            }
            string.append("\"");
            return new Token(TokenType.STRING_LITERAL, source, string.toString(), position, lc(), lc(position+string.length()));
        }
        return null;
    }
    private Token parseBlockComment(String buff){
        if(buff.startsWith("/*")){
            StringBuilder comment = new StringBuilder();
            char[] charArray = buff.toCharArray();
            for (int i = 0, len = charArray.length; i < len; i++) {
                if(len-1 == i){
                    this.error("Unexpected end of file");
                    break;
                }
                char c = charArray[i];
                if (charArray[i] == '*' && charArray[i+1] == '/') {
                    comment.append("*/");
                    break;
                }
                comment.append(c);
            }
            return new Token(TokenType.COMMENT, source, comment.toString(), position, lc(), lc(position+comment.length()));
        }
        return null;
    }
    private Token parseComment(String buff){
        if(buff.startsWith("//")){
            StringBuilder comment = new StringBuilder();
            for (char c : buff.toCharArray()) {
                if(c == '\n')break;
                comment.append(c);
            }
            return new Token(TokenType.COMMENT, source, comment.toString(), position, lc(), lc(position+comment.length()));
        }
        return null;
    }
    private Token parseEmpty(String buff){
        if(buff.startsWith(" ") || buff.startsWith("\r") || buff.startsWith("\t") || buff.startsWith("\n")){
            return new Token(TokenType.EMPTY, source, buff.charAt(0)+"", position, lc(), lc(position+1));
        }
        return null;
    }
    private Token parseKeyword(String buff){
        LinkedHashMap<String, TokenType> keywords = new LinkedHashMap<>();
        keywords.put("void", TokenType.VOID);
        keywords.put("null", TokenType.NULL);
        keywords.put("err", TokenType.ERR);
        keywords.put("out", TokenType.OUT);
        keywords.put("exit", TokenType.EXIT);
        keywords.put("throw", TokenType.THROW);
        keywords.put("try", TokenType.TRY);
        keywords.put("catch", TokenType.CATCH);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("instanceof", TokenType.INSTANCEOF);
        keywords.put("foreach", TokenType.FOREACH);
        keywords.put("for", TokenType.FOR);
        keywords.put("fori", TokenType.FORI);
        keywords.put("do", TokenType.DO);
        keywords.put("while", TokenType.WHILE);
        keywords.put("super", TokenType.SUPER);
        keywords.put("implicit", TokenType.IMPLICIT);
        keywords.put("explicit", TokenType.EXPLICIT);
        keywords.put("operator", TokenType.OPERATOR);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("static", TokenType.STATIC);
        keywords.put("readonly", TokenType.READONLY);
        keywords.put("final", TokenType.FINAL);
        keywords.put("new", TokenType.NEW);
        keywords.put("return", TokenType.RETURN);
        keywords.put("true", TokenType.BOOL_LITERAL);
        keywords.put("false", TokenType.BOOL_LITERAL);
        keywords.put("low", TokenType.TBOOL_LITERAL);
        keywords.put("medium", TokenType.TBOOL_LITERAL);
        keywords.put("high", TokenType.TBOOL_LITERAL);
        keywords.put("tbool", TokenType.TBOOL);
        keywords.put("bool", TokenType.BOOL);
        keywords.put("int", TokenType.INTEGER);
        keywords.put("char", TokenType.CHAR);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("abstract", TokenType.ABSTRACT);
        keywords.put("native", TokenType.NATIVE);
        keywords.put("override", TokenType.OVERRIDE);
        keywords.put("interface", TokenType.INTERFACE);
        keywords.put("annotation", TokenType.ANNOTATION);
        keywords.put("class", TokenType.CLASS);
        keywords.put("implements", TokenType.IMPLEMENTS);
        keywords.put("extends", TokenType.EXTENDS);
        keywords.put("enum", TokenType.ENUM);
        for (Map.Entry<String, TokenType> kw : keywords.entrySet()) {
            if(buff.startsWith(kw.getKey())){
                String next = buff.substring(kw.getKey().length());
                if(!next.isEmpty()){
                    char f = next.toCharArray()[0];
                    if(Character.isAlphabetic(f) || Character.isDigit(f)) continue;
                }

                return new Token(kw.getValue(), source, buff.substring(0, kw.getKey().length()), position,
                        lc(), lc(position+kw.getKey().length()));
            }
        }
        return null;
    }
    private LineCol lc(){
        return lc(this.position);
    }
    private LineCol lc(int pos) {
        if(original.length() < pos)return this.eof;
        return Utils.findStringPosition(this.original, pos);
    }
    private void error(String text){
        error(text, 0);
    }
    private void error(String text, int i) {
        LineCol lc = this.lc(position+i);
        LexerError error = new LexerError(lc, this.source, text);
        lexerErrors.add(error);
        error.critical();
    }
}
