
package com.kt.cqrs.query.event.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "optional",
    "field",
    "name",
    "version",
    "parameters",
    "default"
})
@Data
public class Field__1 {

    @JsonProperty("type")
    public String type;
    @JsonProperty("optional")
    public Boolean optional;
    @JsonProperty("field")
    public String field;
    @JsonProperty("name")
    public String name;
    @JsonProperty("version")
    public Integer version;
    @JsonProperty("parameters")
    public Parameters parameters;
    @JsonProperty("default")
    public Boolean _default;

}
