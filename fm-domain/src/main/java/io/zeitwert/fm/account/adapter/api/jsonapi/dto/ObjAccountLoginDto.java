package io.zeitwert.fm.account.adapter.api.jsonapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.fm.account.adapter.api.jsonapi.impl.ObjAccountLoginDtoAdapter;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjAccountLoginDto extends ObjDtoBase<ObjAccount> {

	private String name;
	private String description;
	private EnumeratedDto accountType;
	private EnumeratedDto clientSegment;
	private EnumeratedDto referenceCurrency;
	@JsonApiRelationId
	private Integer logoId;
	@JsonIgnore
	private ObjDocumentDto logoDto;

	@Override
	public ObjAccountLoginDtoAdapter getAdapter() {
		return (ObjAccountLoginDtoAdapter) super.getAdapter();
	}

	public void setLogoId(Integer logoId) {
		// assertThis(false, "logoId is read-only");
	}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getLogo() {
		if (this.logoDto == null) {
			this.logoDto = this.getAdapter().getDocumentDto(this.logoId);
		}
		return this.logoDto;
	}

	public void setLogo(ObjDocumentDto logo) {
		// assertThis(false, "logo is read-only");
	}

}
