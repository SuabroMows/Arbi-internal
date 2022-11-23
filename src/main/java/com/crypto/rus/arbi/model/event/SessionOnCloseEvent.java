package com.crypto.rus.arbi.model.event;

import com.crypto.rus.arbi.model.collector.CollectorCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

public class SessionOnCloseEvent extends ApplicationEvent {
    @Getter
    private final CollectorCode collectorCode;
    public SessionOnCloseEvent(@NotNull Object source,
                               @NotNull CollectorCode collectorCode) {
        super(source);
        this.collectorCode = collectorCode;
    }
}
