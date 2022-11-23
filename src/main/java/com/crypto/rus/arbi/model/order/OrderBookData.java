package com.crypto.rus.arbi.model.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderBookData(
        Long t,
        String s,
        List<List<BigDecimal>> a,
        List<List<BigDecimal>> b
) {
}
