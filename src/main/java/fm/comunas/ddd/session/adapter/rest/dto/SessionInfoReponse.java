
package fm.comunas.ddd.session.adapter.rest.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import fm.comunas.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import fm.comunas.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import fm.comunas.ddd.session.model.SessionInfo;

@Data()
@Builder
public class SessionInfoReponse {

	private static final List<String> apps = Arrays.asList("advise", "client", "meeting", "config");

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
			.applicationId(sessionInfo.getUser().getEmail().endsWith("comunas.fm") ? "fm" : "advise")
			.availableApplications(apps)
			.customValues(sessionInfo.getCustomValues())
			.build();
		// @formatter:on
	}

}
