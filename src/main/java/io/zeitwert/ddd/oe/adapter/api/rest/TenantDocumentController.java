
package io.zeitwert.ddd.oe.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.service.api.TenantService;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;

import java.util.Map;

@RestController("tenantDocumentController")
@RequestMapping("/rest/oe/tenants")
public class TenantDocumentController {

	@Autowired
	private TenantService tenantService;

	@Autowired
	RequestContext requestCtx;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/{img}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getImage(@PathVariable Integer id, @PathVariable String img) {
		ObjTenant tenant = this.tenantService.getTenant(id);
		Integer documentId = null;
		if ("logo".equals(img)) {
			documentId = tenant.getLogoImageId();
		} else if ("banner".equals(img)) {
			documentId = tenant.getBannerImageId();
		} else {
			return ResponseEntity.notFound().build();
		}
		return this.documentController.getContent(documentId);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Integer>> getStatistics() {
		return ResponseEntity.ok().body(tenantService.getStatistics());
	}

}
