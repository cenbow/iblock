package com.iblock.web.filter;

import com.iblock.common.utils.JsonUtils;
import com.iblock.web.constant.CommonProperties;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.UserInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qihong on 16/1/27.
 */
@Log4j
public class ValidateFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws
            IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        UserInfo currentUser = null;
        if (session != null) {
            currentUser = (UserInfo) session.getAttribute(CommonProperties.USER_INFO);
        }
        if (currentUser == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", ResponseStatus.NO_AUTH.getCode());
            map.put("msg", ResponseStatus.NO_AUTH.getValue());
            response.getWriter().print(JsonUtils.toStr(map));
            return;
        }
        chain.doFilter(request, response);
    }

    public void destroy() {

    }
}
