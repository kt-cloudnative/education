package com.kt.cqrs.command.event;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.h2.api.Trigger;

public class EventHandler implements Trigger {

    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {

    }

    @Override
    public void fire(Connection connection, Object[] before, Object[] after) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO WITHDRAWAL (ID, CARD_ID, AMOUNT) " + "VALUES (?, ?, ?)")) {
            stmt.setObject(1, UUID.randomUUID()); 
            stmt.setObject(2, cardId(after));
            stmt.setObject(3, getUsedLimitChange(before, after));

            stmt.executeUpdate();
        }
    }

    private Object cardId(Object[] cardRow) {
        return cardRow[0];
    }

    private BigDecimal getUsedLimitChange(Object[] oldCardRow, Object[] newCardRow) {
        return ((BigDecimal) newCardRow[2]).subtract((BigDecimal) oldCardRow[2]);
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {

    }
}
