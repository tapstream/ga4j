package com.tapstream.ga4j;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tapstream.ga4j.Utils;

public class TestUtils {

    @Test
    public void testGeneratehash() {
        assertEquals(242063040, Utils.generateHash("stringToHash")); 
    }
    
    @Test
    
    public void testGaTimestamp(){
        String timestamps[] = new String[]{"99850204", "1358975187", "1358975187"};
        for(String t: timestamps){
            assertEquals(t, Utils.dumpGaTimestamp(Utils.parseGaTimestamp(t)));
        }
    }
    @Test
    public void testAnonymizeIp() {
        assertEquals("192.168.1.0", Utils.anonymizeIp("192.168.1.5"));
        assertEquals("10.2.1.0", Utils.anonymizeIp("10.2.1.50"));

    }

}
