package io.zeitwert.fm.oe.adapter.api.rest;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("tenantDocumentController")
@RequestMapping("/rest/oe/tenants")
public class TenantDocumentController {

	@Autowired
	RequestContext requestCtx;
	@Autowired
	private ObjTenantFMRepository tenantRepository;
	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/logo", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		ObjTenantFM tenant = this.tenantRepository.get(id);
		Integer documentId = (Integer) tenant.getLogoImageId();
		if (documentId == null) {
			return ResponseEntity.noContent().build();
		}
		return this.documentController.getContent(documentId);
	}

}
