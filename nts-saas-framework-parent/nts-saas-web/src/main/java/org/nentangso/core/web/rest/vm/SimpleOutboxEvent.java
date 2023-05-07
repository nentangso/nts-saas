package org.nentangso.core.web.rest.vm;

import java.io.Serializable;

public class SimpleOutboxEvent implements Serializable {
    private String payload;
    private String eventType;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "SimpleOutboxEvent{" +
            "payload='" + payload + '\'' +
            ", eventType='" + eventType + '\'' +
            '}';
    }
}
