
package com.kt.cqrs.query.event.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "fields",
    "optional",
    "name"
})
@Data
public class Schema {

    @JsonProperty("type")
    public String type;
    @JsonProperty("fields")
    public List<Field> fields = null;
    @JsonProperty("optional")
    public Boolean optional;
    @JsonProperty("name")
    public String name;

}
