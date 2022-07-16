
package io.zeitwert.fm.lead.model.base;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.enums.CodeArea;
import io.zeitwert.fm.account.model.enums.CodeAreaEnum;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.doc.model.base.FMDocBase;
import io.zeitwert.fm.lead.model.DocLead;
import io.zeitwert.fm.lead.model.DocLeadRepository;
import io.zeitwert.fm.lead.model.enums.CodeLeadRating;
import io.zeitwert.fm.lead.model.enums.CodeLeadRatingEnum;
import io.zeitwert.fm.lead.model.enums.CodeLeadSource;
import io.zeitwert.fm.lead.model.enums.CodeLeadSourceEnum;
import io.zeitwert.ddd.oe.model.enums.CodeCountry;
import io.zeitwert.ddd.oe.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.SessionInfo;

import org.jooq.UpdatableRecord;

public abstract class DocLeadBase extends FMDocBase implements DocLead {

	private final UpdatableRecord<?> leadRecord;

	protected final ReferenceProperty<ObjAccount> account;
	protected final SimpleProperty<String> subject;
	protected final SimpleProperty<String> description;
	protected final EnumProperty<CodeLeadSource> leadSource;
	protected final EnumProperty<CodeLeadRating> leadRating;
	protected final ReferenceProperty<ObjContact> contact;
	protected final EnumProperty<CodeSalutation> salutation;
	protected final EnumProperty<CodeTitle> title;
	protected final SimpleProperty<String> firstName;
	protected final SimpleProperty<String> lastName;
	protected final SimpleProperty<String> phone;
	protected final SimpleProperty<String> mobile;
	protected final SimpleProperty<String> email;
	protected final SimpleProperty<String> street;
	protected final SimpleProperty<String> zip;
	protected final SimpleProperty<String> city;
	protected final SimpleProperty<String> state;
	protected final EnumProperty<CodeCountry> country;
	protected final EnumSetProperty<CodeArea> areaSet;

	protected DocLeadBase(SessionInfo sessionInfo, DocLeadRepository repository, UpdatableRecord<?> docRecord,
			UpdatableRecord<?> leadRecord) {
		super(sessionInfo, repository, docRecord);
		this.leadRecord = leadRecord;
		this.account = this.addReferenceProperty(leadRecord, DocLeadFields.ACCOUNT_ID, ObjAccount.class);
		this.subject = this.addSimpleProperty(leadRecord, DocLeadFields.SUBJECT);
		this.description = this.addSimpleProperty(leadRecord, DocLeadFields.DESCRIPTION);
		this.leadSource = this.addEnumProperty(leadRecord, DocLeadFields.LEAD_SOURCE_ID, CodeLeadSourceEnum.class);
		this.leadRating = this.addEnumProperty(leadRecord, DocLeadFields.LEAD_RATING_ID, CodeLeadRatingEnum.class);
		this.contact = this.addReferenceProperty(leadRecord, DocLeadFields.CONTACT_ID, ObjContact.class);
		this.salutation = this.addEnumProperty(leadRecord, DocLeadFields.SALUTATION_ID, CodeSalutationEnum.class);
		this.title = this.addEnumProperty(leadRecord, DocLeadFields.TITLE_ID, CodeTitleEnum.class);
		this.firstName = this.addSimpleProperty(leadRecord, DocLeadFields.FIRST_NAME);
		this.lastName = this.addSimpleProperty(leadRecord, DocLeadFields.LAST_NAME);
		this.phone = this.addSimpleProperty(leadRecord, DocLeadFields.PHONE);
		this.mobile = this.addSimpleProperty(leadRecord, DocLeadFields.MOBILE);
		this.email = this.addSimpleProperty(leadRecord, DocLeadFields.EMAIL);
		this.street = this.addSimpleProperty(leadRecord, DocLeadFields.STREET);
		this.zip = this.addSimpleProperty(leadRecord, DocLeadFields.ZIP);
		this.city = this.addSimpleProperty(leadRecord, DocLeadFields.CITY);
		this.state = this.addSimpleProperty(leadRecord, DocLeadFields.STATE);
		this.country = this.addEnumProperty(leadRecord, DocLeadFields.COUNTRY_ID, CodeCountryEnum.class);
		this.areaSet = this.addEnumSetProperty(this.getRepository().getAreaSetType(), CodeAreaEnum.class);
	}

	@Override
	public DocLeadRepository getRepository() {
		return (DocLeadRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer docId, Integer tenantId) {
		super.doInit(docId, tenantId);
		this.leadRecord.setValue(DocLeadFields.DOC_ID, docId);
		this.leadRecord.setValue(DocLeadFields.TENANT_ID, tenantId);
	}

	@Override
	public void doStore() {
		super.doStore();
		this.leadRecord.store();
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.leadRecord.setValue(DocLeadFields.ACCOUNT_ID, id);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		ObjContact contact = this.getContact();
		ObjAccount account = this.getAccount();
		if (contact != null) {
			this.caption.setValue((contact.getCaption() + " " + this.getSubject()).trim());
		} else if (account != null) {
			this.caption.setValue((account.getCaption() + " " + this.getSubject()).trim());
		} else {
			this.caption.setValue((this.getFirstName() + " " + this.getLastName() + " " + this.getSubject()).trim());
		}
		this.caption
				.setValue(this.getCaption() + (this.getCaseStage() != null ? ", " + this.getCaseStage().getName() : ""));
	}

}
