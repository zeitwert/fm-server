
package io.zeitwert.fm.server.session.adapter.rest.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountLoginDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;

@Data()
@Builder
public class SessionInfoReponse {

	private ObjUserDto user;
	private ObjTenantDto tenant;
	private ObjAccountLoginDto account;

	private String locale;
	private String applicationId;
	private String applicationName;
	private String applicationVersion;
	private List<String> availableApplications;

}
