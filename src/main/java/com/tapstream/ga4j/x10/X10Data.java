package com.tapstream.ga4j.x10;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.tapstream.ga4j.Utils;

public class X10Data {
    final private static String DELIM_BEGIN = "(";
    final private static String DELIM_END = ")"; 
    final private static String DELIM_SET = "*";
    final private static String DELIM_NUM_VALUE = "!";
    final private static int MINIMUM = 1;
    
    private SortedMap<Integer, String> data = new TreeMap<Integer, String>();
    
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public void put(int index, String value) {
        data.put(index, value);
    }
    
    public String toUrlString() {
        List<String> results = new ArrayList<String>();
        int lastIndex = 0;
        for (Entry<Integer, String> entry: data.entrySet()) {
            Integer index = entry.getKey();
            String value = entry.getValue();
            if (index == null)
                continue;
            if (value == null || value.isEmpty())
                continue;
            
            // Append the index if index is not the starting value or if the
            // index values are not contiguous
            StringBuilder builder = new StringBuilder(128);
            if (index != MINIMUM && (index - 1) != lastIndex) {
                builder.append(index);
                builder.append(DELIM_NUM_VALUE);
            }
            
            builder.append(escapeStringValue(value));
            results.add(builder.toString());
            lastIndex = index;
        }
        
        return DELIM_BEGIN + Utils.join(results, DELIM_SET) + DELIM_END;
    }
    
    public static String escapeStringValue(String value) {
        StringBuilder builder = new StringBuilder();
        for (char x : value.toCharArray()) {
            switch (x) {
            case '\'':
                builder.append("'0");
                break;
            case ')':
                builder.append("'1");
                break;
            case '*':
                builder.append("'2");
                break;
            case '!':
                builder.append("'3");
                break;
            default:
                builder.append(x);
                break;
            }
        }
        return builder.toString();
    }
}
