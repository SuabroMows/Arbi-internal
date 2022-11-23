package com.crypto.rus.arbi.model.order;

public record OrderBook(
        OrderBookData data,
        String type,
        String topic,
        Long ts
) {
}
