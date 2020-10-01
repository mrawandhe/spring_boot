package com.logs.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogFilter extends OncePerRequestFilter {
	
	@Value("${log.request.log-header:false}")
	private boolean isIncludeHeaders;
	
	@Value("${log.request.log-clientInfo:false}")
	private boolean isIncludeClientInfo;
	

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException,
    	IOException {
    	
    	HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        logRequest(requestWrapper);
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        logResponse(responseWrapper, startTime);
    }

    protected void logRequest(HttpServletRequest request) {
		StringBuilder msg = new StringBuilder();
		msg.append(request.getMethod()).append(" ");
		msg.append(request.getRequestURI());
		String queryString = request.getQueryString();
		if (!StringUtils.isEmpty(queryString)) {
			msg.append('?').append(queryString);
		}
		if (isIncludeClientInfo) {
			String client = request.getRemoteAddr();
			if (!StringUtils.isEmpty(client)) {
				msg.append(", client=").append(client);
			}
			HttpSession session = request.getSession(false);
			if (Objects.nonNull(session)) {
				msg.append(", session=").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (!StringUtils.isEmpty(user)) {
				msg.append(", user=").append(user);
			}
		}
		if (isIncludeHeaders) {
			HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
			msg.append(", headers=").append(HttpHeaders.formatHeaders(headers));
		}
		String payload = getBody(request);
		if (!StringUtils.isEmpty(payload)) {
			msg.append(",\npayload=").append(payload);
		}
		log.debug("Request [{}]", msg.toString());
	}
	
	protected String getBody(HttpServletRequest req) {
		String body = null;
		HttpRequestWrapper request = (HttpRequestWrapper)req;
		try {
			body = new String(request.getRequestBody());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return body;
	}

    private void logResponse(ContentCachingResponseWrapper responseWrapper, long startTime) {
    	String body = "None";
    	byte[] buf = responseWrapper.getContentAsByteArray();
    	if (buf.length > 0) {
    		int length = Math.min(buf.length, 2048);
    		try {
    			body = new String(buf, 0, length, responseWrapper.getCharacterEncoding());
    			responseWrapper.copyBodyToResponse();
    		} catch (IOException e) {
    			log.error(e.getMessage());
    		}
    	}
    	int statusCode = responseWrapper.getStatus();
    	long time = System.currentTimeMillis() - startTime;
    	log.debug("Response [statusCode={}, processing time={} ms, body={}]", statusCode, time, body);
    }

}
