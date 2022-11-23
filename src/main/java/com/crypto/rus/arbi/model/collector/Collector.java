package com.crypto.rus.arbi.model.collector;

import com.crypto.rus.arbi.model.pair.ArbiPair;
import com.crypto.rus.arbi.service.CollectorSessionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

public class Collector {

    private final CollectorSessionHandler sessionHandler;
    private final String wssUrl;

    public Collector(@NotNull CollectorCode code,
                     @NotNull ApplicationEventPublisher applicationEventPublisher,
                     @NotNull List<ArbiPair> pairs,
                     @NotNull String wssUrl) {
        this.sessionHandler = new CollectorSessionHandler(applicationEventPublisher, pairs, code);
        this.wssUrl = wssUrl;
    }

    public void collectData() {
        HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(wssUrl), sessionHandler)
                .join();
    }
}
