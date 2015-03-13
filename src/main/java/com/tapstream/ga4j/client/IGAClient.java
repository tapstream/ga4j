package com.tapstream.ga4j.client;

import java.io.Closeable;

import com.tapstream.ga4j.domain.Page;
import com.tapstream.ga4j.domain.Session;
import com.tapstream.ga4j.domain.Tracker;
import com.tapstream.ga4j.domain.Visitor;
import com.tapstream.ga4j.exceptions.RequestError;

public interface IGAClient extends Closeable{
    public void trackPageView(Tracker tracker, Page page, Session session, Visitor visitor) throws RequestError;
}
