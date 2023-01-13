
package io.zeitwert.fm.account.adapter.api.jsonapi.dto;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiRelationId;
import io.crnk.core.resource.annotations.SerializeType;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto;
import io.zeitwert.fm.dms.adapter.api.jsonapi.impl.ObjDocumentDtoAdapter;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.adapter.api.jsonapi.dto.FMObjDtoBase;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public class ObjAccountLoginDto extends FMObjDtoBase<ObjAccount> {

	private String key;
	private String name;
	private String description;
	private EnumeratedDto accountType;
	private EnumeratedDto clientSegment;
	private EnumeratedDto referenceCurrency;
	private BigDecimal inflationRate;
	private Set<EnumeratedDto> areas;

	@JsonApiRelationId
	private Integer logoId;

	@JsonIgnore
	private ObjDocumentDto logoDto;

	@JsonApiRelation(serialize = SerializeType.LAZY)
	public ObjDocumentDto getLogo() {
		if (this.logoDto == null) {
			ObjDocument logo = null;
			if (this.getOriginal() != null) {
				logo = this.getOriginal().getLogoImage();
			} else if (this.logoId != null) {
				logo = getRepository(ObjDocument.class).get(this.logoId);
			}
			this.logoDto = ObjDocumentDtoAdapter.getInstance().fromAggregate(logo);
		}
		return this.logoDto;
	}

	public void setLogo(ObjDocumentDto logo) {
	}

}
