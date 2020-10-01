package com.logs.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody = new byte[0];
    private boolean bufferFilled = false;

    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public byte[] getRequestBody() throws IOException {
        if (bufferFilled) {
            return Arrays.copyOf(requestBody, requestBody.length);
        }
        InputStream inputStream = super.getInputStream();
    	ByteArrayOutputStream result = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int length;
    	while ((length = inputStream.read(buffer)) != -1) {
    		result.write(buffer, 0, length);
    	}
    	requestBody = result.toByteArray();
    	bufferFilled = true;
    	return requestBody;
    	
    }

    public ServletInputStream getInputStream() throws IOException {
        return new CustomServletInputStream(getRequestBody());
    }

    
    private static class CustomServletInputStream extends ServletInputStream {

        private ByteArrayInputStream buffer;

        
        public CustomServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new RuntimeException("Not implemented");
        }
    }
    
}
