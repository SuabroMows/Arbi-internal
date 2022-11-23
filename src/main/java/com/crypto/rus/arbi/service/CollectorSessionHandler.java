package com.crypto.rus.arbi.service;

import com.crypto.rus.arbi.model.collector.CollectorCode;
import com.crypto.rus.arbi.model.event.SessionOnCloseEvent;
import com.crypto.rus.arbi.model.order.OrderBook;
import com.crypto.rus.arbi.model.request.OrderBookRequest;
import com.crypto.rus.arbi.model.pair.ArbiPair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.WebSocket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class CollectorSessionHandler implements WebSocket.Listener {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Getter
    private final List<ArbiPair> arbiPairs;
    @Getter
    public Map<String, List<BigDecimal>> pairPrice = new HashMap<>();

    public Map<ArbiPair, Long> lifetimeArbiPairs;
    private final CollectorCode collectorCode;

    public CollectorSessionHandler(@NotNull ApplicationEventPublisher applicationEventPublisher,
                                   @NotNull List<ArbiPair> arbiPairs,
                                   @NotNull CollectorCode collectorCode) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.arbiPairs = arbiPairs;
        this.collectorCode = collectorCode;
        this.lifetimeArbiPairs = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println(collectorCode.name()+ " IS STARTING... "+ LocalTime.now().toString()+" ");
        WebSocket.Listener.super.onOpen(webSocket);
        webSocket.sendText(OrderBookRequest.fromPairs(this.arbiPairs),true);
    }

    public void findArbiDeal() {
        if(pairPrice.size() == OrderBookRequest.countCurrencyInPair(arbiPairs)) {
            for (ArbiPair arbiPair : arbiPairs) {
                BigDecimal priceBuy = pairPrice.get(arbiPair.usdtKey1()).get(1);
                BigDecimal priceSell1 = pairPrice.get(arbiPair.key1Key2()).get(0);
                BigDecimal priceSell2 = pairPrice.get(arbiPair.key2Usdt()).get(0);

                BigDecimal value = BigDecimal.TEN
                        .divide(priceBuy, RoundingMode.DOWN)
                        .multiply(priceSell1)
                        .multiply(priceSell2)
                        .subtract(BigDecimal.TEN)
                        .divide(BigDecimal.TEN, RoundingMode.DOWN)
                        .multiply(BigDecimal.TEN)
                        .multiply(BigDecimal.TEN);
                if(value.compareTo(BigDecimal.valueOf(0.2)) > 0) {
                    if(!lifetimeArbiPairs.containsKey(arbiPair)) {
                        lifetimeArbiPairs.put(arbiPair, System.currentTimeMillis());
                    }
                } else {
                    if(lifetimeArbiPairs.containsKey(arbiPair)) {
                        System.out.println(arbiPair.usdtKey1()+"->"+arbiPair.key1Key2()+"->"+arbiPair.key2Usdt());
                        System.out.println(System.currentTimeMillis() - lifetimeArbiPairs.get(arbiPair) + " ms");
                    }
                    lifetimeArbiPairs.remove(arbiPair);
                }
            }
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            OrderBook orderBook = mapper.readValue(data.toString(), OrderBook.class);
            List<BigDecimal> prices = new ArrayList<>();
            prices.add(orderBook.data().b().get(0).get(0));
            prices.add(orderBook.data().a().get(0).get(0));
            pairPrice.put(orderBook.data().s(), prices);

            findArbiDeal();
        } catch (JsonProcessingException ex) {
            System.out.println("not parse" + data);
        }

        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        applicationEventPublisher.publishEvent(new SessionOnCloseEvent(this, collectorCode));
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        applicationEventPublisher.publishEvent(new SessionOnCloseEvent(this, collectorCode));
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }
}
