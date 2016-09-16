package com.wolper.formmasterhelper;


import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class SendRest {

    private String url;
    private String password;
    private final String USERNAME="FMH";
    private RestTemplate restTemplate;
    private HttpEntity<?> requestEntity;
    private String errorSend;


    private SendRest(String url) {
        this.url=url;
    }

    public static SendRest initWithServerName(String url) {
        SendRest sendRest = new SendRest(url);
        return sendRest;
    }

    public SendRest setPassword(String password) {
        this.password = password;
        return this;
    }

    public SendRest prepareForSend(){
        HttpAuthentication authHeader = new HttpBasicAuthentication(USERNAME, password);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(new MediaType("application","json"));
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        return this;
    }

    public boolean senmMe(String playload){
        boolean status=true;
        try {
            ResponseEntity<String> ok=restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (!ok.getBody().equals("ok")) status=false; errorSend="Just has not sent!";
        }
        catch (RestClientException e) {errorSend = e.getMessage(); status=false;}
        return status;
    }


    public String getError(){
        return errorSend;
    }

}
