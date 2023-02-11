
package io.zeitwert.fm.oe.adapter.api.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.oe.model.ObjTenantFM;

@RestController("tenantDocumentController")
@RequestMapping("/rest/oe/tenants")
public class TenantDocumentController {

	@Autowired
	private ObjTenantCache tenantCache;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/logo", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
		ObjTenantFM tenant = (ObjTenantFM) this.tenantCache.get(id);
		Integer documentId = tenant.getLogoImageId();
		if (documentId == null) {
			return ResponseEntity.noContent().build();
		}
		return this.documentController.getContent(documentId);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Integer>> getStatistics() {
		return ResponseEntity.ok().body(this.tenantCache.getStatistics());
	}

}
