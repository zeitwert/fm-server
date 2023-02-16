
package io.zeitwert.fm.oe.model.base;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.TableRecord;

import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.model.base.ObjTenantBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.enums.CodeTenantType;

public abstract class ObjTenantFMBase extends ObjTenantBase implements ObjTenantFM {

	//@formatter:off
	protected final EnumProperty<CodeTenantType> tenantType = this.addEnumProperty("tenantType", CodeTenantType.class);
	protected final SimpleProperty<BigDecimal> inflationRate = this.addSimpleProperty("inflationRate", BigDecimal.class);
	protected final ReferenceProperty<ObjDocument> logoImage = this.addReferenceProperty("logoImage", ObjDocument.class);
	//@formatter:on

	public ObjTenantFMBase(ObjTenantFMRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjTenantFMRepository getRepository() {
		return (ObjTenantFMRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addLogoImage();
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getLogoImageId() == null) {
			this.addLogoImage();
		}
	}

	@Override
	public List<ObjUserFM> getUsers() {
		ObjUserFMRepository userRepo = (ObjUserFMRepository) this.getAppContext().getRepository(ObjUserFM.class);
		ObjUserCache userCache = (ObjUserCache) this.getAppContext().getCache(ObjUser.class);
		return userRepo.getByForeignKey("tenantId", this.getId())
				.stream()
				.map(c -> (TableRecord<?>) c)
				.map(c -> (ObjUserFM) userCache.get((Integer) c.get("id")))
				.toList();
	}

	@Override
	public List<ObjAccount> getAccounts() {
		ObjAccountRepository accountRepo = (ObjAccountRepository) this.getAppContext().getRepository(ObjAccount.class);
		List<ObjAccountVRecord> accountIds = accountRepo.getByForeignKey("tenantId", this.getId());
		ObjAccountCache accountCache = (ObjAccountCache) this.getAppContext().getCache(ObjAccount.class);
		return accountIds.stream().map(c -> accountCache.get(c.getId())).toList();
	}

	private void addLogoImage() {
		ObjDocumentRepository documentRepo = (ObjDocumentRepository) this.getAppContext().getRepository(ObjDocument.class);
		ObjDocument image = documentRepo.create(this.getTenantId());
		image.setName("Logo");
		image.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		image.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		image.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("logo"));
		documentRepo.store(image);
		this.logoImage.setId(image.getId());
	}

}
