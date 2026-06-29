package local.fabricarag.core.domain.analytics;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventOrigin {
    UI("ui"),
    API("api"),
    WORKER("worker"),
    MCP("mcp"),
    SYSTEM("system");

    private final String value;

    EventOrigin(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
