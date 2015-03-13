GA4J - Google Analytics for Java
=============

[![Build Status](https://travis-ci.org/tapstream/ga4j.svg?branch=master)](https://travis-ci.org/tapstream/ga4j)

This is an implementation of the Google Analytics page tracking API for Java. It's draws heavily from 
kra3's [py-ga-mob](https://github.com/kra3/py-ga-mob).


Basic Usage
------------------
    
    try (GAClient client = new GAClient()){
        client.trackPageView(
            new Tracker( "UA-12345678-1", "www.example.com"), 
            new Page("/example"), 
            new Session(), 
            new Visitor());
    }
    
See com.tapstream.ga4j.TestClient.testPageViewParameters() for an example of more advanced usage.
    