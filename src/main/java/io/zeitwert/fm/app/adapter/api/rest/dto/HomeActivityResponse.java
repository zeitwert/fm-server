
package io.zeitwert.fm.app.adapter.api.rest.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeActivityResponse {

	private EnumeratedDto item; // rating, task
	private EnumeratedDto relatedTo; // rating: building, task: obj
	private EnumeratedDto owner;

	private EnumeratedDto user;
	private String dueAt;

	private String subject;
	private String content;
	private EnumeratedDto priority;

}
