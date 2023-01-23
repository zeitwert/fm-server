package io.zeitwert.ddd.enums.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.obj.model.Obj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data()
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumeratedDto {

	@JsonApiId
	private String id;

	private String name;

	private EnumeratedDto itemType;

	public static EnumeratedDto fromEnum(Enumerated e) {
		if (e == null) {
			return null;
		}
		return EnumeratedDto.builder()
				.id(e.getId())
				.name(e.getName())
				.build();
	}

	public static EnumeratedDto fromAggregate(Aggregate a) {
		if (a == null) {
			return null;
		}
		return EnumeratedDto.builder()
				.id(a.getId().toString())
				.itemType(fromEnum(a.getMeta().getAggregateType()))
				.name(a.getCaption())
				.build();
	}

	public static EnumeratedDto fromObj(Obj obj) {
		return fromAggregate(obj);
	}

	public static EnumeratedDto fromDoc(Doc doc) {
		return fromAggregate(doc);
	}

}
