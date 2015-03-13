package com.tapstream.ga4j.domain;

public class CustomVariable {

    public static enum Scope{
        VISITOR(1), SESSION(2), PAGE(3);
        
        private final int id;
        
        private Scope(int id){
            this.id = id;
        }
        
        public int getId(){
            return id;
        }
    }
    
    final private int index;
    final private String name;
    final private String value;
    final private Scope scope;
    
    public CustomVariable(int index, String name, String value, Scope scope) {
        
        if (index < 0 || index > 4)
            throw new IllegalArgumentException("Custom variable index must be between 0 and 4 inclusive");
        if (name.length() + value.length() > 128)
            throw new IllegalArgumentException("Custom variable name + value must be less than 128 characters");
        
        this.index = index;
        this.name = name;
        this.value = value;
        this.scope = scope;
        
    }
    
    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Scope getScope() {
        return scope;
    }


    
}
