
package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class HomeActivityResponse {

	private String objTypeId;
	private Integer objId;
	private String objCaption;
	private Integer seqNr;
	private OffsetDateTime timestamp;
	private String user;
	private String changes;

}
