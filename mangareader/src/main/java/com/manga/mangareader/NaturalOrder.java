package com.manga.mangareader;

import java.math.BigInteger;
import java.util.Comparator;

public class NaturalOrder implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char x = a.charAt(i), y = b.charAt(j);
            if(Character.isDigit(x) && Character.isDigit(y)) {
                int si = i, sj = j;
                while(i < a.length() && Character.isDigit(a.charAt(i))) i++;
                while(j < b.length() && Character.isDigit(b.charAt(j))) j++;
                int c = new BigInteger(a.substring(si, i)).compareTo(new BigInteger(b.substring(sj, j)));
                if(c != 0) return c;
            } else {
                int c = Character.compare(Character.toLowerCase(x), Character.toLowerCase(y));
                if( c != 0) return c;
                i++;
                j++;
            }
        }
        return Integer.compare(a.length() - i, b.length() - j);

    }
    
}
