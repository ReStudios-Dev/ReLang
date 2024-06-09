package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.LineCol;

public class Utils {
    public static LineCol findStringPosition(String text, int position) {
        int lineNumber = 1;
        int charPosition = 0;
        int currentPos = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lineNumber++;
                charPosition = 0;
            } else {
                charPosition++;
            }

            if (currentPos == position) {
                break;
            }

            currentPos++;
        }
        return new LineCol(lineNumber, charPosition);
    }
    public static String removeFirstAndLastChar(String input) {
        if (input.length() <= 2) {
            return "";
        } else {
            return input.substring(1, input.length() - 1);
        }
    }
    public static String unescapeString(String input) {
        StringBuilder builder = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escape) {
                switch (c) {
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case '\'':
                        builder.append('\'');
                        break;
                    case '\"':
                        builder.append('\"');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    default:
                        builder.append('\\');
                        builder.append(c);
                        break;
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

}
