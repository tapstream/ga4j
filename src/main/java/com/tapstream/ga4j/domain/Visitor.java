package com.tapstream.ga4j.domain;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.tapstream.ga4j.Utils;

public class Visitor {
    private Integer uniqueId;
    private Integer firstVisitTime;
    private Integer previousVisitTime;
    private Integer currentVisitTime;
    private Integer visitCount;
    private String ipAddress;
    private String userAgent;
    private String locale;
    private String flashVersion;
    private Integer javaEnabled;
    private String screenColorDepth;
    private String screenResolution;
    
    public Visitor(){
        int now = Utils.getUnixTimestamp();
        this.firstVisitTime = now;
        this.previousVisitTime = now;
        this.currentVisitTime = now;
        this.visitCount = 1;
        this.javaEnabled = 0;
    }

    public void updateFromUtma(String utma) throws NoSuchElementException, NumberFormatException {
        StringTokenizer tokenizer = new StringTokenizer(utma, ".");
        try{
            tokenizer.nextToken();
            uniqueId = Integer.parseInt(tokenizer.nextToken());
            firstVisitTime = Utils.parseGaTimestamp(tokenizer.nextToken());
            previousVisitTime = Utils.parseGaTimestamp(tokenizer.nextToken());
            currentVisitTime = Utils.parseGaTimestamp(tokenizer.nextToken());
            visitCount = Integer.parseInt(tokenizer.nextToken());


        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("Unable to update from UTMA. Not enough blocks. " + utma);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Unable to parse number from UTMA. " + utma);
        }
    }
    
    public void updateFromServerMeta(String remoteAddr, String xForwardedFor, String userAgent, String acceptLanguage){
        this.ipAddress = Utils.getClientIp(remoteAddr, xForwardedFor);
        this.userAgent = userAgent;
        this.locale = Utils.parseAcceptLanguage(acceptLanguage);
    }
    
    public void updateFromSession(Session session) {
        if (session.getStartTime() != currentVisitTime) {
            previousVisitTime = currentVisitTime;
            currentVisitTime = session.getStartTime();
            visitCount++;
        }
    }

    private String getIdStringToHash() {
        StringBuilder builder = new StringBuilder(2048);
        if (userAgent != null)
            builder.append(userAgent);
        if (screenResolution != null)
            builder.append(screenResolution);
        if (screenColorDepth != null)
            builder.append(screenColorDepth);
        return builder.toString();
    }
    
    public Integer getUniqueId() {
        if (uniqueId == null) {
            uniqueId = Utils.getRandomInt() ^ Utils.generateHash(getIdStringToHash()) & 0x7FFFFFFF;
        }
        return uniqueId;
    }

    public Integer getFirstVisitTime() {
        return firstVisitTime;
    }

    public Integer getPreviousVisitTime() {
        return previousVisitTime;
    }

    public Integer getCurrentVisitTime() {
        return currentVisitTime;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getLocale() {
        return locale;
    }

    public String getFlashVersion() {
        return flashVersion;
    }

    public Integer getJavaEnabled() {
        return javaEnabled;
    }

    public String getScreenColorDepth() {
        return screenColorDepth;
    }

    public String getScreenResolution() {
        return screenResolution;
    }
    
     
}
