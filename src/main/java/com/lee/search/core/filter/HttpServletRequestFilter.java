package com.lee.search.core.filter;

import com.lee.search.core.interceptor.MyHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Order(1)
@WebFilter(filterName = "httpServletRequestFilter", displayName = "httpServletRequestFilter", urlPatterns = "/*")
public class HttpServletRequestFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Register a new filter {}", filterConfig.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ServletRequest requestWrapper = null;
		if (request instanceof HttpServletRequest) {
			requestWrapper = new MyHttpServletRequestWrapper((HttpServletRequest) request);
		}

		if (requestWrapper == null) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(requestWrapper, response);
		}
	}

	@Override
	public void destroy() {
		log.info("Destroy filter {}", getClass().getName());
	}

}
