package com.tekgator.simplehttpclient;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {

    private final Builder builder;

    public HttpClient(Builder builder) {
        this.builder = builder;
    }

    public HttpResponse request() throws IOException {
        return request(buildParameterString(builder.urlParameters));
    }

    private HttpResponse request(String urlParameters) throws IOException {
        URL url;

        if (builder.requestMethod == RequestMethod.GET && urlParameters.length() > 0) {
            // in case of GET append parameters to the URL
            url = new URL(String.format("%s?%s", builder.url, urlParameters));
        } else {
            url = new URL(builder.url);
        }

        // get HTTP client for URL
        HttpsURLConnection httpClient = (HttpsURLConnection) url.openConnection();

        // set connect timeout, if provided
        if (builder.connectTimeout != null) {
            httpClient.setConnectTimeout(builder.connectTimeout);
        }

        // set read timeout, if provided
        if (builder.readTimeout != null) {
            httpClient.setReadTimeout(builder.readTimeout);
        }

        // set caching mode
        httpClient.setUseCaches(builder.useCaches);

        // set request method e.g. GET, POST, etc.
        httpClient.setRequestMethod(builder.requestMethod.toString());

        // set request properties if any
        for (Entry<String, String> entry : builder.requestProperties.entrySet()) {
            httpClient.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // if not GET, set output to true
        if (builder.requestMethod != RequestMethod.GET) {
            httpClient.setDoOutput(true);
        }
        
        // if not GET, send parameters now
        if (builder.requestMethod != RequestMethod.GET && urlParameters.length() > 0) {
            httpClient.setRequestProperty( "Content-Length", String.valueOf(urlParameters.length()) );    
            httpClient.getOutputStream().write(urlParameters.getBytes(StandardCharsets.UTF_8));
        }

        HttpResponse httpResponse = new HttpResponse(httpClient);

        // disconnect from server
        httpClient.disconnect();

        return httpResponse;
    }


    public HttpClient setUrlParameter(String key, String value) {
        builder.urlParameters.put(key, value);
        return this;
    }

    public HttpClient setUrlParameter(Hashtable<String, String> urlParameters) {
        builder.urlParameters.putAll(urlParameters);
        return this;
    }

    public HttpClient clearUrlParameter() {
        builder.urlParameters.clear();
        return this;
    }

    private String buildParameterString(Hashtable<String, String> urlParameters) {
        StringBuilder sb = new StringBuilder();

        for(Entry<String, String> entry : urlParameters.entrySet()) {
            try {
                if (sb.length() > 0)
                    sb.append('&');

                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
                sb.append('=');
                sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
            }
        }

        return sb.toString();
    }

    /**
     * @author Patrick Weiss <info@tekgator.com>
     */
    public static final class Builder {

        private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";

        private final String url;
        
        private Integer connectTimeout;
        private Integer readTimeout;
        private boolean useCaches;
        private RequestMethod requestMethod;
        private Hashtable<String, String> requestProperties = new Hashtable<>();
        private Hashtable<String, String> urlParameters = new Hashtable<>();


        public Builder(String url) {
            this.url = url;
            setRequestMethod(RequestMethod.GET);
            setUserAgent(DEFAULT_USER_AGENT);
            setUseCaches(false);
        }

        public Builder setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setRequestMethod(RequestMethod requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder setRequestProperty(String key, String value) {
            requestProperties.put(key, value);
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            setRequestProperty("User-Agent", userAgent);
            return this;
        }

        public Builder setContentType(String contentType) {
            setRequestProperty("Content-Type", contentType);
            return this;
        }

        public Builder setUrlParameter(String key, String value) {
            urlParameters.put(key, value);
            return this;
        }

        public Builder setUrlParameter(Hashtable<String, String> urlParameters) {
            this.urlParameters.putAll(urlParameters);
            return this;
        }

        public void setUseCaches(boolean useCaches) {
            this.useCaches = useCaches;
        }

        public HttpClient create() {
            return new HttpClient(this);
        }

    }

    /**
     * @author Patrick Weiss <info@tekgator.com>
     */
    public static enum RequestMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        HEAD("HEAD"),
        OPTIONS("OPTIONS"),
        DELETE("DELETE"),
        TRACE("TRACE")
        ;
    
        private final String stringValue;
        
        RequestMethod(String s) { 
            stringValue = s; 
        }
        
        @Override
        public String toString() { 
            return stringValue; 
        }    
    
    }
    
}