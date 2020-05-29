package com.tekgator.simplehttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Patrick Weiss <info@tekgator.com>
 */
public class HttpResponse {

    private int code = HttpsURLConnection.HTTP_SEE_OTHER;
    private String message = "";
    private String data = "";
    private boolean successful;

    public HttpResponse(HttpsURLConnection httpClient) {
        try {
            code = httpClient.getResponseCode();
            message = httpClient.getResponseMessage();

            if (code >= HttpsURLConnection.HTTP_OK && code < HttpsURLConnection.HTTP_BAD_REQUEST) {
                successful = true;
                data = readResponse(httpClient.getInputStream());
            } else {
                successful = false;
                data = readResponse(httpClient.getErrorStream());
            }
        } catch (IOException e) {

        }
    }

    private String readResponse(InputStream inputStream) {
        StringBuilder response = new StringBuilder();
        String line;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            response = new StringBuilder();
        }

        return response.toString();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return getData();
    }    

}