package com.kt.cqrs.query.event;


import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class CardWithdrawn {

    private UUID cardNo;
    private long amount;
    private Date timestamp;
    private String type;

}
