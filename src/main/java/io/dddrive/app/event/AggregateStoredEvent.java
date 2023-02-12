package io.dddrive.app.event;

import org.springframework.context.ApplicationEvent;

import io.dddrive.ddd.model.Aggregate;

public class AggregateStoredEvent extends ApplicationEvent {

	private Aggregate aggregate;

	public AggregateStoredEvent(Object source, Aggregate aggregate) {
		super(source);
		this.aggregate = aggregate;
	}

	public Aggregate getAggregate() {
		return this.aggregate;
	}

}
