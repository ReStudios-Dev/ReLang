/**
* String class. Any text within quotes returns this class
*/
public abstract class str {

    /* native methods */
    /**
    * Returns the length of the string.
    */
    native public int length();

    /**
    * Returns a substring starting at the specified index fromIndex.
    */
    native public str substring(int fromIndex);

    /**
    * Returns a substring starting at the specified index fromIndex and ending at index toIndex.
    */
    native public str substring(int fromIndex, int length);

    /**
    * Returns a string, without the first character
    */
    native public str removeFirst();

    /**
    * Returns a string, without the last character
    */
    native public str removeLast();

    /**
    * Returns true if the string begins with the passed seq string, false otherwise
    */
    native public bool starts(str string);

    /**
    * Returns true if the string ends with the passed seq string, false otherwise
    */
    native public bool ends(str string);

    /**
    * Returns a string in uppercase
    */
    native public str upper();

    /**
    * Returns a lowercase string
    */
    native public str lower();

    /**
    * Returns a string in which the first character is uppercase
    */
    native public str capitalize();

    /**
    * Returns a string, without leading or trailing spaces
    */
    native public str trim();

    /**
    * Returns true if the string matches the regular expression
    */
    native public bool matches(str regex);

    /**
    * Returns true if the string contains another, false otherwise
    */
    native public bool contains(str seq);

    /**
    * Returns true if the string is equal to another, ignoring case, otherwise false
    */
    native public bool equalsIgnoreCase(str seq);

    /**
    * Returns true if the string is equal to another, otherwise false
    */
    native public bool equals(str seq);

    /**
    * Returns the character at a specific position in a string
    */
    native public char charAt(int pos);

    /**
    * Returns the index of the beginning of the string found on the current (otherwise -1)
    */
    native public int indexOf(str seq);

    /**
    * Returns the same string, but with the find string replaced by the replace string
    */
    native public str replace(str find, str replace);

    /**
    * Returns a string with string added at the end
    */
    native public str append(str string);

    /**
    * Splits a string. The by argument is a regular expression
    */
    native public array<str> split(str by);
    /* /native methods */


    /**
    * str -> int
    */
    explicit operator int(str string){
        return CastUtils.strToInt(string);
    }

    /**
    * <string> + <string>
    */
    str operator +(str s1, str s2){
        return s1.append(s2);
    }
    /**
    * <string> + <int>
    */
    str operator +(str s1, int s2){
        return s1 + CastUtils.intToStr(s2);
    }
    /**
    * <int> + <string>
    */
    str operator +(int s1, str s2){
        return CastUtils.intToStr(s1) + s2;
    }

}