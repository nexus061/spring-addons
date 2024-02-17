package com.c4_soft.springaddons.security.oidc.starter.rest;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.Data;

/**
 * A {@link ClientHttpRequestInterceptor} adding a Bearer Authorization header (if the {@link BearerProvider} provides one).
 *
 * @author Jerome Wacongne ch4mp&#64;c4-soft.com
 */
@Data
public class BearerTokenAuthenticationInterceptor implements ClientHttpRequestInterceptor {
    private final BearerProvider bearerProvider;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        bearerProvider.getBearer().ifPresent(bearer -> {
            request.getHeaders().setBearerAuth(bearer);
        });
        return execution.execute(request, body);
    }
}