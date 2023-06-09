package com.geselaapi.model;

import java.util.UUID;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID uuid;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
}
