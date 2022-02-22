package fm.comunas.ddd.app.event;

import fm.comunas.ddd.aggregate.model.Aggregate;

import org.springframework.context.ApplicationEvent;

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
