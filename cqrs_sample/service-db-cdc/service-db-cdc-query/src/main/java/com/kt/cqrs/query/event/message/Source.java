
package com.kt.cqrs.query.event.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "name",
    "server_id",
    "ts_sec",
    "gtid",
    "file",
    "pos",
    "row",
    "snapshot",
    "thread",
    "db",
    "table"
})
@Data
public class Source {

    @JsonProperty("version")
    public String version;
    @JsonProperty("name")
    public String name;
    @JsonProperty("server_id")
    public Integer serverId;
    @JsonProperty("ts_sec")
    public Integer tsSec;
    @JsonProperty("gtid")
    public Object gtid;
    @JsonProperty("file")
    public String file;
    @JsonProperty("pos")
    public Integer pos;
    @JsonProperty("row")
    public Integer row;
    @JsonProperty("snapshot")
    public Boolean snapshot;
    @JsonProperty("thread")
    public Integer thread;
    @JsonProperty("db")
    public String db;
    @JsonProperty("table")
    public String table;

}
