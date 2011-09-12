package com.sndyuk.puzzle.util;

public class CmdCounter {

    private int u = 0, r = 0, d = 0, l = 0;
    
    public int getU() {
        return u;
    }
    public int getR() {
        return r;
    }
    public int getD() {
        return d;
    }
    public int getL() {
        return l;
    }

    public void addAll(String cmdStr) {
        if (cmdStr == null || cmdStr.length() == 0) {
            return;
        }
        for (char c : cmdStr.toCharArray()) {
            add(c);
        }
    }
    
    public void add(char c) {
        
        switch (c) {
        case 'U':
            u++;
            break;
        case 'R':
            r++;
            break;
        case 'D':
            d++;
            break;
        case 'L':
            l++;
            break;
        }
    }
    
    
}
