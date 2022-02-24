package io.zeitwert.ddd.item.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.ItemPart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class ItemPartDtoBase<A extends Aggregate, P extends ItemPart<A>> {

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private String id;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	public Integer getId() {
		try {
			return Integer.valueOf(this.id);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void toPart(P part) {
	}

	public static void fromPart(ItemPartDtoBaseBuilder<?, ?, ?, ?> dtoBuilder, ItemPart<?> part) {
		dtoBuilder.id(String.valueOf(part.getId()));
	}

}
