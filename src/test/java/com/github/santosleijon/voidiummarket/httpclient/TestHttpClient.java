package com.github.santosleijon.voidiummarket.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class TestHttpClient {

    private static final String apiUrlBase = "http://localhost:8081";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(1))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public <T> T get(String url, TypeReference<T> responseType) throws HttpErrorResponse {
        try {
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(apiUrlBase + url))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            throwErrorIfBadStatusCode(response);

            if (responseType == null) {
                return null;
            }

            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void post(String url, Object body) throws HttpErrorResponse {
        try {
            var bodyString = objectMapper.writeValueAsString(body);
            var bodyPublisher = HttpRequest.BodyPublishers.ofString(bodyString);

            var request = HttpRequest.newBuilder()
                    .headers("Content-Type", "application/json")
                    .POST(bodyPublisher)
                    .uri(URI.create(apiUrlBase + url))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            throwErrorIfBadStatusCode(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String url) throws HttpErrorResponse {
        try {
            var request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create(apiUrlBase + url))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            throwErrorIfBadStatusCode(response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwErrorIfBadStatusCode(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() > 204) {
            throw new HttpErrorResponse(response.statusCode(), response.uri().toString(), response.body());
        }
    }
}
