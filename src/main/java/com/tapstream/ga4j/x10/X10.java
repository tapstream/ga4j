package com.tapstream.ga4j.x10;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class X10 {
    
    final public static int SITESPEED_PROJECT_ID = 14;
    final public static int CUSTOMVAR_NAME_PROJECT_ID = 8;
    final public static int CUSTOMVAR_VALUE_PROJECT_ID = 9;
    final public static int CUSTOMVAR_SCOPE_PROJECT_ID = 11;
    
    public static final int OBJECT_KEY_TYPE = 1;
    public static final int TYPE_KEY_TYPE = 2;
    public static final int LABEL_KEY_TYPE = 3;
    
    public static final int VALUE_VALUE_TYPE = 1;
    
    private SortedMap<Integer, X10Project> projects = new TreeMap<Integer, X10Project>();
    
    public boolean hasProject(int projectId) {
        return projects.containsKey(projectId);
    }
    
    public void putKey(int projectId, int type, String value) {
        X10Project project = projects.get(projectId);
        if (project == null) {
            project = new X10Project(projectId);
            projects.put(projectId, project);
        }
        project.putKey(type, value);
    }
    
    public void putValue(int projectId, int type, String value) {
        X10Project project = projects.get(projectId);
        if (project == null) {
            project = new X10Project(projectId);
            projects.put(projectId, project);
        }
        project.putValue(type, value);
    }
    
    public String toUrlString() {
        StringBuilder builder = new StringBuilder(1024);
        for (Entry<Integer, X10Project> projectEntry : projects.entrySet()) {
            builder.append(projectEntry.getKey());
            builder.append(projectEntry.getValue().toUrlString());
        }
        return builder.toString();
    }


}
