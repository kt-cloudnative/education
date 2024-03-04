package com.kt.cqrs.command.event;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("STORED_DOMAIN_EVENT")
class StoredDomainEvent implements Persistable<String> {

	@Transient
	private boolean isNew = false;

	@Id
	private String id;
	private String content;
	private boolean sent;
	private Date eventTimestamp;
	private String eventType;

	StoredDomainEvent(String content, String eventType) {
		this.isNew = true;
		this.content = content;
		this.id = UUID.randomUUID().toString();
		this.eventType = eventType;
		this.eventTimestamp = new Date();
	}

	public static StoredDomainEvent newStoredDomainEvent(String content, String eventType) {
		StoredDomainEvent storedDomainEvent = new StoredDomainEvent(content, eventType);
		return storedDomainEvent;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	void sent() {
		this.sent = true;
	}

}