
package com.kt.cqrs.query.event.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "initial_limit",
    "used_limit"
})
@Data
public class After {

    @JsonProperty("id")
    public String id;
    @JsonProperty("initial_limit")
    public Integer initialLimit;
    @JsonProperty("used_limit")
    public Integer usedLimit;

}
