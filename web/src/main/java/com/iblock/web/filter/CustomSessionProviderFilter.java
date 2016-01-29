package com.iblock.web.filter;

import com.iblock.web.session.CustomSessionHttpServletRequest;
import com.iblock.web.session.DefaultSessionFactory;
import com.iblock.web.session.SessionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * 自定义servlet会话过滤器。经过此filter后，HttpSession的实现类皆由SessionFactory的实现提供。 filter的init-param：
 * springHosted：当为true时，表示SessionFactory的实例从spring容器中获取。
 * sessionFactory：指定SessionFactory实例的类名。当springHosted不为true时，SessionFactory的实例借助此参数构造，
 * sessionFactory类需要包含一个带FilterConfig参数的构造函数或一个无参的构造函数（优先级按序） </p>
 */
public class CustomSessionProviderFilter implements Filter {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SessionFactory sessionFactory;

    public void init(FilterConfig filterConfig) throws ServletException {
        boolean springHosted = Boolean.parseBoolean(filterConfig.getInitParameter("springHosted"));
        if (springHosted) {
            logger.info("using spring-hosted mode");
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            logger.info("SessionFactory: " + sessionFactory);
            return;
        }

        // non-spring-hosted
        String sessionFactoryVal = filterConfig.getInitParameter("sessionFactory");
        if (sessionFactoryVal == null) {
            logger.info("sessionFactory param is not set, and default session factory is used");
            sessionFactory = new DefaultSessionFactory();
            return;
        }
        try {
            Class<?> sessionFactoryClz = Class.forName(sessionFactoryVal);
            // sessionFactory类需要包含一个带FilterConfig参数的构造函数或一个无参的构造函数
            try {
                Constructor<?> constructor = sessionFactoryClz.getConstructor(FilterConfig.class);
                sessionFactory = (SessionFactory) constructor.newInstance(filterConfig);
            } catch (NoSuchMethodException constructorNotFound) {
                try {
                    Constructor<?> constructor = sessionFactoryClz.getConstructor();
                    sessionFactory = (SessionFactory) constructor.newInstance();
                } catch (NoSuchMethodException e) {
                    throw new ServletException(String.format("constructor %s() or %s(FilterConfig) is required",
                            sessionFactoryVal, sessionFactoryVal));
                }
            }

            logger.info("custom session using provider: " + sessionFactoryVal);
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        @SuppressWarnings("unchecked")
        CustomSessionHttpServletRequest customSessionReq = new CustomSessionHttpServletRequest(httpServletRequest,
                httpServletResponse,
                sessionFactory);
        chain.doFilter(customSessionReq, response);
    }

    public void destroy() {
        try {
            sessionFactory.close();
        } catch (Exception e) {
            logger.error("failed to close sessionFactory", e);
        }
    }

    /**
     * 非容器启动情况下，设置会话工厂。
     *
     * @param sessionFactory 会话工厂实例
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        if (this.sessionFactory != null) {
            throw new IllegalStateException("sessionFactory has already been set");
        }
        if (sessionFactory == null) {
            throw new NullPointerException();
        }
        this.sessionFactory = sessionFactory;
    }
}
