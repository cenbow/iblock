package com.iblock.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qihong on 16/1/27.
 */
public class ContentTypeFilter implements Filter {

    private String charset = "UTF-8";
    private FilterConfig config;
    private String encoding;

    public void destroy() {
        // System.out.println(config.getFilterName()+"被销毁");
        charset = null;
        config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 设置请求响应字符编码
        request.setCharacterEncoding(charset);
        response.setCharacterEncoding(charset);

        HttpServletRequest req = (HttpServletRequest) request;

        // 对GET方式提交的请求的处理
        if (req.getMethod().equalsIgnoreCase("get")) {
            req = new GetHttpServletRequestWrapper(req, charset);
        }
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "http://www.mi-zhu.com:8000");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Max-Age", "86400");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, *");
        //       httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
        chain.doFilter(req, httpServletResponse);

    }

    public void init(FilterConfig config) throws ServletException {
        this.encoding = config.getInitParameter("encoding");
        this.config = config;
        String charset = config.getServletContext().getInitParameter("charset");
        if (charset != null && charset.trim().length() != 0) {
            this.charset = charset;
        }
    }
}
