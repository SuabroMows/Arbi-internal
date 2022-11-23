package com.crypto.rus.arbi.service;

import com.crypto.rus.arbi.model.collector.Collector;
import com.crypto.rus.arbi.model.collector.CollectorCode;
import com.crypto.rus.arbi.model.event.SessionOnCloseEvent;
import com.crypto.rus.arbi.model.pair.ArbiPair;
import com.crypto.rus.arbi.model.pair.ArbiPairList;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArbiService implements ApplicationListener<SessionOnCloseEvent> {
    @Value("${bybit.wss.url}")
    private String wssUrl;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<CollectorCode, Collector> collectors;

    @Autowired
    public ArbiService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.collectors = new HashMap<>();
    }

    @EventListener(value = ApplicationReadyEvent.class)
    public void start() {
        addCollectorAndStartCollect(Arrays.asList(
                ArbiPairList.T_ALGO_BTC_T,
                ArbiPairList.T_SOL_BTC_T,
                ArbiPairList.T_XRP_BTC_T,
                ArbiPairList.T_MATIC_BTC_T
        ), CollectorCode.C_1);

        addCollectorAndStartCollect(Arrays.asList(
                ArbiPairList.T_BIT_BTC_T,
                ArbiPairList.T_LTC_BTC_T,
                ArbiPairList.T_ETH_BTC_T,
                ArbiPairList.T_MANA_BTC_T
        ), CollectorCode.C_2);
    }

    public void addCollectorAndStartCollect(@NotNull List<ArbiPair> pairs,
                                            @NotNull CollectorCode code) {
        Collector collector = new Collector(
                code,
                applicationEventPublisher,
                pairs,
                wssUrl);
        collectors.put(code, collector);
        collector.collectData();
    }

    @Override
    public void onApplicationEvent(SessionOnCloseEvent event) {
       collectors.get(event.getCollectorCode()).collectData();
    }
}
