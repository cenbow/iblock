package com.iblock.web.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

@SuppressWarnings("deprecation")
public class HttpSessionWrapper<SessionT extends HttpSession> implements HttpSession {

    private final SessionT original;

    public HttpSessionWrapper(SessionT original) {
        if (original == null) {
            throw new NullPointerException();
        }
        this.original = original;
    }

    public long getCreationTime() {
        return original.getCreationTime();
    }

    public String getId() {
        return original.getId();
    }

    public long getLastAccessedTime() {
        return original.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return original.getServletContext();
    }

    public void setMaxInactiveInterval(int interval) {
        original.setMaxInactiveInterval(interval);
    }

    public int getMaxInactiveInterval() {
        return original.getMaxInactiveInterval();
    }

    public HttpSessionContext getSessionContext() {
        return original.getSessionContext();
    }

    public Object getAttribute(String name) {
        return original.getAttribute(name);
    }

    public Object getValue(String name) {
        return original.getValue(name);
    }

    public Enumeration getAttributeNames() {
        return original.getAttributeNames();
    }

    public String[] getValueNames() {
        return original.getValueNames();
    }

    public void setAttribute(String name, Object value) {
        original.setAttribute(name, value);
    }

    public void putValue(String name, Object value) {
        original.putValue(name, value);
    }

    public void removeAttribute(String name) {
        original.removeAttribute(name);
    }

    public void removeValue(String name) {
        original.removeValue(name);
    }

    public void invalidate() {
        original.invalidate();
    }

    public boolean isNew() {
        return original.isNew();
    }

    public SessionT getSession() {
        return this.original;
    }
}
