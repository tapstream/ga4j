package com.tapstream.ga4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;

import com.tapstream.ga4j.client.GAClient;
import com.tapstream.ga4j.client.GAConfig;
import com.tapstream.ga4j.domain.Campaign;
import com.tapstream.ga4j.domain.Page;
import com.tapstream.ga4j.domain.Parameters;
import com.tapstream.ga4j.domain.Session;
import com.tapstream.ga4j.domain.Tracker;
import com.tapstream.ga4j.domain.Visitor;

public class TestClient {
    
    @Test
    public void testPageViewParameters() throws Exception {
        HttpClient mockHttpClient = mock(HttpClient.class);
        
        GAClient client = new GAClient(new GAConfig(), mockHttpClient);
        Parameters params = new Parameters();
        String utmac = "UA-12345678-1";
        String utmhn = "tapstream.com";
        
        String utma = "107780231.99850204.1358975187.1358975187.1358975187.1";
        String utmb = "107780231.0.10.1342742534";
        String utmz = "107780231.1358975187.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)";
        
        Page page = new Page("/TEST/LONG-WAY");
        Visitor visitor = new Visitor();
        Session session = new Session();
        session.updateFromUtmb(utmb);
        Tracker tracker = new Tracker(utmac, utmhn);
        try {
            Campaign campaign = Campaign.createFromReferrer("http://tapfolio.com/TESTPLEASEIGNORE/LONG-WAY");
            campaign.updateFromUtmz(utmz);
            tracker.setCampaign(campaign);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        visitor.updateFromServerMeta("127.0.0.1", "", "GA4J/Mozilla 4.0", "en-us");
        visitor.updateFromSession(session);
        visitor.updateFromUtma(utma);
        
        params = client.buildTrackPageViewParameters(tracker, page, session, visitor);
        
        String param_keys[] = new String[]{"utmwv", "utms", "utmn", "utmhn", "utmcs", "utmsr", "utmvp", "utmsc", "utmul",
                            "utmje", "utmfl", "utmdt", "utmhid", "utmr", "utmp", "utmac", "utmcc", "utmu"};

        for(String k: param_keys){
            System.out.println(k + ": " + params.get(k));
        }
        
        assertEquals(params.get("utmwv"), "5.3.0");
        assertEquals(params.get("utms"), "0");
        assertEquals(params.get("utmhn"), utmhn);
        assertEquals(params.get("utmul"), "en-us");
        assertEquals(params.get("utmac"), utmac);

        HttpRequestBase request = client.buildRequest(params, visitor);
        client.close();
    }

}
