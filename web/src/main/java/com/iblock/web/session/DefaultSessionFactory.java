package com.iblock.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DefaultSessionFactory implements SessionFactory<HttpSession> {

    public HttpSession getSession(HttpServletRequest request, HttpServletResponse response, boolean create) {
        return request.getSession(create);
    }

    public void releaseSession(HttpSession session, HttpServletRequest request) {
    }

    public void close() throws IOException {
    }
}
