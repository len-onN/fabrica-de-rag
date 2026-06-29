package local.fabricarag.core.domain.analytics;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActorType {
    USER("user"),
    AGENT("agent"),
    SERVICE_ACCOUNT("service_account"),
    SYSTEM("system");

    private final String value;

    ActorType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
