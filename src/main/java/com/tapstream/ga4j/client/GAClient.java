package com.tapstream.ga4j.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tapstream.ga4j.Utils;
import com.tapstream.ga4j.domain.Cookies;
import com.tapstream.ga4j.domain.CustomVariable;
import com.tapstream.ga4j.domain.Page;
import com.tapstream.ga4j.domain.Parameters;
import com.tapstream.ga4j.domain.Session;
import com.tapstream.ga4j.domain.Tracker;
import com.tapstream.ga4j.domain.Visitor;
import com.tapstream.ga4j.exceptions.RequestError;
import com.tapstream.ga4j.x10.X10;



public class GAClient implements IGAClient {
    
    final private static Logger logger = LoggerFactory.getLogger(GAClient.class);
    
    private GAConfig config;
    
    private HttpClient httpClient;
    
    public GAClient() {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setDefaultMaxPerRoute(25);
        cm.setMaxTotal(25);
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);
        httpParams.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 1000);
        init(new GAConfig(), new DefaultHttpClient(cm, httpParams));
    }
    
    public GAClient(GAConfig config, HttpClient httpClient) {
        init(config, httpClient);
    }
    
    private void init(GAConfig config, HttpClient httpClient){
        this.config = config;
        this.httpClient = httpClient;
    }
    
    @Override
    public void close() throws IOException {
        if (httpClient == null)
            return;
        ClientConnectionManager connMan = httpClient.getConnectionManager();
        if (connMan == null)
            return;
        connMan.shutdown();
    }
    
    public void trackPageView(Tracker tracker, Page page, Session session, Visitor visitor) throws RequestError {
        Parameters params = buildTrackPageViewParameters(tracker, page, session, visitor);
        
        session.incrementTrackCount();
        
        if (session.getTrackCount() > 500)
            logger.warn("Google Analytics does not guarantee to process more than 500 requests per session");       
    
        if (tracker.hasCampaign())
            tracker.getCampaign().incrementResponseCount();
        
        try {
            sendRequest(buildRequest(params, visitor));
        } catch (IOException e) {
            throw new RequestError(e);
        }
    }
    
    public void sendRequest(HttpRequestBase request) throws RequestError, ClientProtocolException, IOException {
        HttpResponse response = httpClient.execute(request);
        
        try {
            if (response == null)
                return;
            HttpEntity entity = response.getEntity();
            if (entity == null)
                return;
            EntityUtils.consumeQuietly(entity);
        } finally {
            request.releaseConnection();
        }
    }
    
    public HttpRequestBase buildRequest(Parameters params, Visitor visitor) {
        
        List<NameValuePair> nvps = new ArrayList<NameValuePair>(params.size());
        
        for (Entry<String, String> entry : params) {
            String key = entry.getKey();
            if (key == null || key.isEmpty())
                continue;
            String value = entry.getValue();
            if (value == null || value.isEmpty())
                continue;
            nvps.add(new BasicNameValuePair(key, value));           
        }
        
        // ga.js style url encoding
        String encodedParams = Utils.convertToGAStyleEncoding(URLEncodedUtils.format(nvps, "UTF-8"));
        HttpRequestBase request;
        
        try {
            if (encodedParams.length() > 2036) {
                URI uri = new URIBuilder(config.getEndpoint()).build();
                HttpPost post = new HttpPost(uri);
                post.setEntity(new StringEntity(encodedParams, "UTF-8"));
                post.addHeader("Content-Type", "text/plain");
                request = post;
            } else {
                URI uri = new URIBuilder(config.getEndpoint())
                        .setQuery(encodedParams)
                        .build();
                request = new HttpGet(uri);
            }
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        
        request.addHeader("User-Agent", visitor.getUserAgent());
        request.addHeader("X-Forwarded-For", visitor.getIpAddress());
        
        return request;
    }
    
    
    public Parameters buildTrackPageViewParameters(Tracker tracker, Page page, Session session, Visitor visitor) {
        final Parameters params = buildCommonParameters(tracker, session, visitor);
        params.set("utmt", null);
        params.set("utmp", page.getPath());
        params.set("utmdt", page.getTitle());
        
        // Optional
        params.setIfNotEmpty("utmcs", page.getCharset());
        params.setIfNotEmpty("utmr", page.getReferrer());
        
        if (page.getLoadTime() != null && params.getInt("utmn") % 100 < config.getSiteSpeedSampleRate()) {
            X10 x10 = new X10();
            Double key = Math.max(Math.min(Math.floor((double)page.getLoadTime() / 100), 5000), 0) * 100;
            x10.putKey(X10.SITESPEED_PROJECT_ID, X10.OBJECT_KEY_TYPE, Double.toString(key));
            x10.putValue(X10.SITESPEED_PROJECT_ID, X10.VALUE_VALUE_TYPE, Integer.toString(page.getLoadTime()));
        }
        
        return params;
    }
    
    public Parameters buildCommonParameters(Tracker tracker, Session session, Visitor visitor) {
        final Parameters params = new Parameters();
        
        // Defaults
        params.set("utmwv", "5.3.0");
        params.set("utmcs", "-");
        params.set("utmr", "-");
        params.set("utmfl", "-");
        params.set("utmje", "-");
        
        // Required common params
        params.set("utmac", tracker.getAccountId());
        params.set("utmhn", tracker.getDomainName());
        params.set("utmn", Utils.getRandomInt());
        
        if (config.isAnonymizeIpAddress()) {
            params.set("aip", "1");
            params.set("utmip", Utils.anonymizeIp(visitor.getIpAddress()));
        } else {
            params.set("utmip", visitor.getIpAddress());
        }
        
        params.set("utmhid", session.getSessionId());
        params.set("utms", session.getTrackCount());
        
        // Optional visitor params
        params.setIfNotEmpty("utmul", visitor.getLocale());
        params.setIfNotEmpty("utmfl", visitor.getFlashVersion());
        params.setIfNotEmpty("utje", visitor.getJavaEnabled());
        params.setIfNotEmpty("utmsc", visitor.getScreenColorDepth() + "-bit");
        params.setIfNotEmpty("utmsr", visitor.getScreenResolution());
        
        // Custom variables
        X10 x10 = new X10();
        for (CustomVariable cvar : tracker.getCustomVariables()) {
            if (cvar == null)
                continue;
            String name = Utils.encodeUriComponent(cvar.getName());
            String value = Utils.encodeUriComponent(cvar.getValue());
            x10.putKey(X10.CUSTOMVAR_NAME_PROJECT_ID, cvar.getIndex(), name);
            x10.putKey(X10.CUSTOMVAR_VALUE_PROJECT_ID, cvar.getIndex(), value);
            
            if (cvar.getScope() != CustomVariable.Scope.PAGE) {
                x10.putKey(
                        X10.CUSTOMVAR_SCOPE_PROJECT_ID, 
                        cvar.getIndex(), 
                        Integer.toString(cvar.getScope().getId()));
            }
            
        }
        params.set("utme", x10.toUrlString());
        
                
        // Build cookies
        Cookies cookies = Cookies.build(params, tracker, visitor, session);
        params.set("utmcc", cookies.getUtmcc());
        
        return params;
    }

}

