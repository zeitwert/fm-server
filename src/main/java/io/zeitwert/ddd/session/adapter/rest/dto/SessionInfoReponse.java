
package io.zeitwert.ddd.session.adapter.rest.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.session.model.SessionInfo;

@Data()
@Builder
public class SessionInfoReponse {

	private static final List<String> apps = Arrays.asList("fm", "admin");

	private ObjTenantDto tenant;

	private ObjUserDto user;

	private String locale;

	private String applicationId;

	private List<String> availableApplications;

	private Map<String, Object> customValues;

	public static SessionInfoReponse fromSession(SessionInfo sessionInfo) {
		if (sessionInfo == null) {
			return null;
		}
		// @formatter:off
		return SessionInfoReponse.builder()
			.tenant(ObjTenantDto.fromObj(sessionInfo.getTenant()))
			.user(ObjUserDto.fromObj(sessionInfo.getUser()))
			.locale("en-US")
			.applicationId("fm")
			.availableApplications(apps)
			.customValues(sessionInfo.getCustomValues())
			.build();
		// @formatter:on
	}

}
