package com.example.InteraccionesSociales2.webClient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.InteraccionesSociales2.webClient.dto.Foros;

import reactor.core.publisher.Mono;

@Component
public class ForosClient {

    private final WebClient webClient;

    public ForosClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8082/api/v1/foros")
                .build();
    }

    public Mono<Foros> obtenerPorId(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Foros.class);
    }
}
