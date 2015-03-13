package com.tapstream.ga4j.x10;



public class X10Project {
    
    private static final String VALUE_TYPE_QUALIFIER = "v";
    
    final private int id;
    final private X10Data keys = new X10Data();
    final private X10Data values = new X10Data();

    
    public X10Project(int id) {
        this.id = id;
    }
    
    public void putKey(int type, String value) {
        keys.put(type, value);
    }
    
    public void putValue(int type, String value) {
        values.put(type, value);
    }
    
    public String toUrlString() {
        StringBuilder builder = new StringBuilder(1024);
        boolean needsTypeQualifier = false;
        
        if (keys.isEmpty()) {
            needsTypeQualifier = true;
        } else { 
            builder.append(keys.toString());
        }
        
        if (!values.isEmpty()) {
            if (needsTypeQualifier)
                builder.append(VALUE_TYPE_QUALIFIER);
            builder.append(values.toString());
        }
        
        return builder.toString();
    }
    
    public int getId() {
        return id;
    }

}
