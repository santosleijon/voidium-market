package com.github.santosleijon.voidiummarket.httpclient;

public class HttpErrorResponse extends RuntimeException {

    public int statusCode;
    public String uri;
    public String body;

    public HttpErrorResponse(int statusCode, String uri, String body) {
        super("Status " + statusCode + ": " + uri);
        this.statusCode = statusCode;
        this.uri = uri;
        this.body = body;
    }
}
