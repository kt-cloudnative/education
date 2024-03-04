
package com.kt.cqrs.query.event.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "before",
    "after",
    "source",
    "op",
    "ts_ms"
})
@Data
public class Payload {

    @JsonProperty("before")
    public Before before;
    @JsonProperty("after")
    public After after;
    @JsonProperty("source")
    public Source source;
    @JsonProperty("op")
    public String op;
    @JsonProperty("ts_ms")
    public Long tsMs;

}
