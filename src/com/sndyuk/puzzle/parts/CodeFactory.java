package com.sndyuk.puzzle.parts;

import java.util.HashMap;
import java.util.Map;


public class CodeFactory {

    private static final char[] DEFAULT_CHAR_MAP = {
        '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'A', 'B', 'C',
        'D', 'E', 'F', 'G', 'H', 'I',
        'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z', '0'
        };

    protected static final char ZERO = '0';
    protected static final char BLOCK = '=';

    private Map<Character, Code> createCodeCache(char[] charMap) {
        Map<Character, Code> codeCache = new HashMap<>(charMap.length - 1);
        for (int i = 0; i < charMap.length; i++) {
            codeCache.put(charMap[i], new Code(charMap[i], i, this));
        }
        codeCache.put(BLOCK, new Code(BLOCK, charMap.length, this));
        return codeCache;
    }

    protected final char[] charMap;
    protected final Map<Character, Code> codeCache;

    private static final CodeFactory DEFAULT_CODE_FACTORY = new CodeFactory();

    public static CodeFactory getDefaultCodeFactory() {
        return DEFAULT_CODE_FACTORY;
    }

    public static char[] getDefaultCharMap(int size) {
        char[] newArr = new char[size];
        System.arraycopy(DEFAULT_CHAR_MAP, 0, newArr, 0, size - 1);
        newArr[size - 1] = ZERO;
        return newArr;
    }
    
    private CodeFactory() {
        this(DEFAULT_CHAR_MAP);
    }

    public CodeFactory(char[] charMap) {
        for (int i = 0; i < charMap.length; i++) {
            if (charMap[i] == BLOCK) {
                charMap[i] = DEFAULT_CHAR_MAP[i];
            }
        }
        this.charMap = charMap;
        this.codeCache = createCodeCache(charMap);
    }

    public Code getCode(char c) {
        return codeCache.get(c);
    }

    public Code end() {
        return getCode(charMap[charMap.length - 1]);
    }
    
    protected static char getHash(int index, char c) {
        if (c == BLOCK) {
            return BLOCK;
        }
       for (int i = 0; i < DEFAULT_CHAR_MAP.length; i++) {
           if (DEFAULT_CHAR_MAP[i] == c) {
               return DEFAULT_CHAR_MAP[i];
           }
    }
       throw new AssertionError();
    }
}
