package com.tapstream.ga4j.domain;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.http.client.utils.URIBuilder;

import com.tapstream.ga4j.Utils;

public class Campaign {

    public static enum Type {
        DIRECT("direct"), ORGANIC("organic"), REFERRAL("referral");
        
        final private String id;
        
        private Type(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
    }
    
    public static final String CAMPAIGN_DELIMITER = "|";
    
    private Type type;
    private String name;
    private String source;
    private String medium;
    private String content;
    private String term;
    private Integer creationTime;
    private Integer responseCount;
    private String id;
    private String gClickId;
    private String dClickId;
    
    public Campaign(Type type) {
        this.creationTime = Utils.getUnixTimestamp();
        this.type = type;
        this.responseCount = 0;
        switch (type) {
        case DIRECT:
            name = "(direct)";
            source = "(direct)";
            medium = "(none)";
            break;
        case REFERRAL:
            name = "(referral)";
            medium = "referral";
            break;
        case ORGANIC:
            name = "(organic)";
            medium = "organic";
            break;
        }
    }
    
    public Parameters getOptionalUtmzParams(){
        Parameters params = new Parameters();
        params.setIfNotEmpty("utmcid", id);
        params.setIfNotEmpty("utmcsr", source);
        params.setIfNotEmpty("utmgclid", dClickId);
        params.setIfNotEmpty("utmdclid", dClickId);
        params.setIfNotEmpty("utmccn", name);
        params.setIfNotEmpty("utmcmd", medium);
        params.setIfNotEmpty("utmctr", term);
        params.setIfNotEmpty("utmcct", content);
        return params;
    }
    
    public String getOptionalUtmzFragment() {
        StringBuilder utmzBuilder = new StringBuilder();
        Iterator<Entry<String, String>> it = getOptionalUtmzParams().iterator();
        while (it.hasNext()) {
            Entry<String, String> param = it.next();
            utmzBuilder.append(param.getKey());
            utmzBuilder.append("=");

            // This is all the escaping ga.js does apparently
            utmzBuilder.append(param.getValue().replace("+", "%20").replace(" ", "%20"));
            
            if (it.hasNext())
                utmzBuilder.append(Campaign.CAMPAIGN_DELIMITER);
        }
        return utmzBuilder.toString();
    }
    
    public static Campaign createFromReferrer(String referrer) throws URISyntaxException {
        Campaign campaign = new Campaign(Type.REFERRAL);
        URIBuilder uriBuilder = new URIBuilder(referrer);
        campaign.source = uriBuilder.getHost();
        campaign.content = uriBuilder.getPath();
        return campaign;
    }
    
    public void updateFromUtmz(String utmz)  {
        final StringTokenizer tokenizer = new StringTokenizer(utmz, ".");
        try {
            tokenizer.nextToken(); // Domain hash
            creationTime = Utils.parseGaTimestamp(tokenizer.nextToken()); // Time of initial visit
            tokenizer.nextToken(); //Session id
            responseCount = Integer.parseInt(tokenizer.nextToken()); // Campaign number
            
            while (tokenizer.hasMoreTokens()) {
                String parameterString = tokenizer.nextToken(CAMPAIGN_DELIMITER);
                StringTokenizer parameterTokenizer = new StringTokenizer(parameterString, "=", false);
                String key = parameterTokenizer.nextToken();
                String value = URLDecoder.decode(parameterTokenizer.nextToken("").substring(1), "UTF-8");

                assert value.getBytes()[0] != '=';
                
                if ("utmcid".equals(key))
                    this.id = value;
                else if ("utmcsr".equals(key))
                    this.source = value;
                else if ("utmgclid".equals(key))
                    this.gClickId = value;
                else if ("utmdclid".equals(key))
                    this.dClickId = value;
                else if ("utmccn".equals(key))
                    this.name = value;
                else if ("utmcmd".equals(key))
                    this.medium = value;
                else if ("utmctr".equals(key))
                    this.term = value;
                else if ("utmcct".equals(key))
                    content = value;
            }
            
        }
        catch (NoSuchElementException e) {throw new IllegalArgumentException("Malformed UTMZ: " + utmz); }
        catch (NumberFormatException e) {throw new IllegalArgumentException("Unable to parse int from utmz: " + utmz);} 
        catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Integer creationTime) {
        this.creationTime = creationTime;
    }

    public Integer getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(Integer responseCount) {
        this.responseCount = responseCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getgClickId() {
        return gClickId;
    }

    public void setgClickId(String gClickId) {
        this.gClickId = gClickId;
    }

    public String getdClickId() {
        return dClickId;
    }

    public void setdClickId(String dClickId) {
        this.dClickId = dClickId;
    }
    
    public void incrementResponseCount() {
        responseCount++;
    }

}
