package com.crypto.rus.arbi.model.request;

import com.crypto.rus.arbi.model.pair.ArbiPair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderBookRequest extends SubscribeRequest {
    @NotNull
    public static String fromPairs(List<ArbiPair> args) {
        ArrayNode argsNode = JsonNodeFactory.instance.arrayNode();
        Set<String> argsSet = new HashSet<>();
        for(ArbiPair pair : args) {
            argsSet.add("orderbook.5" + "." +pair.usdtKey1());
            argsSet.add("orderbook.5" + "." +pair.key1Key2());
            argsSet.add("orderbook.5" + "." +pair.key2Usdt());
        }
        argsSet.forEach(argsNode::add);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.set("op", new TextNode(op));
        node.set("args", argsNode);

        try {
            return new ObjectMapper().writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @NotNull
    public static Integer countCurrencyInPair(List<ArbiPair> args) {
        Set<String> currency = new HashSet<>();
        args.forEach(p->{
            currency.add(p.usdtKey1());
            currency.add(p.key1Key2());
            currency.add(p.key2Usdt());
        });
        return currency.size();
    }
}
