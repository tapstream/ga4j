package com.tapstream.ga4j.domain;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.tapstream.ga4j.Utils;

public class Session {
    
    private int sessionId;
    private int trackCount;
    private int startTime;
    
    public Session(int sessionId, int trackCount, int startTime){
        this.sessionId = sessionId;
        this.trackCount = trackCount;
        this.startTime = startTime;
    }
    
    public Session(){
        this(Utils.getRandomInt(), 0, Utils.getUnixTimestamp());
    }
    
    public void updateFromUtmb(String utmb) {
        try{
            StringTokenizer tokenizer = new StringTokenizer(utmb, ".");
            tokenizer.nextToken();
            trackCount = Integer.parseInt(tokenizer.nextToken());
            tokenizer.nextToken();
            startTime = Utils.parseGaTimestamp(tokenizer.nextToken());
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("UTMB value didn't contain enough blocks: " + utmb);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("UTMB value didn't contain valid integers: " + utmb);
        }
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public int getStartTime() {
        return startTime;
    }
    
    public void incrementTrackCount() {
        trackCount++;
    }
}
