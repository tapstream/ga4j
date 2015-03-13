package com.tapstream.ga4j.domain;

public class Page {
    
    private String path;
    private String title;
    private String charset;
    private String referrer;
    
    /**
     * Page load time in milliseconds.
     */
    private Integer loadTime;
    
    public Page(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("The page path must start with a slash (/)");
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Integer getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Integer loadTime) {
        this.loadTime = loadTime;
    }
}
