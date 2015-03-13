package com.tapstream.ga4j.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Parameters implements Iterable<Entry<String, String>>{
    final private Map<String, String> params;
    
    public Parameters(){
        params = new HashMap<String, String>(); 
    }
    
    public Map<String, String> asMap(){
        return Collections.unmodifiableMap(params);
    }
    
    public int size() {
        return params.size();
    }
    
    public boolean hasKey(String name) {
        return params.containsKey(name);
    }
    
    public String get(String name) {
        return params.get(name);
    }
    
    public Integer getInt(String name) {
        return Integer.parseInt(get(name));
    }
    
    public void set(String name, String value){
        params.put(name, value);
    }
    
    public void set(String name, int value){
        set(name, Integer.toString(value));
    }
    
    public boolean setIfNotEmpty(String name, String value) {
        if (value != null && !value.isEmpty()) {
            set(name, value);
            return true;
        }
        return false;   
    }
    
    public boolean setIfNotEmpty(String name, int value) {
        return setIfNotEmpty(name, Integer.toString(value));
    }
    
    public Map<String, String> getCookieMap(){
        Map<String, String> results = new HashMap<String, String>();
        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.startsWith("_")) {
                results.put(key,  value);
            }
        }
        return results;
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        return asMap().entrySet().iterator();
    }
}
