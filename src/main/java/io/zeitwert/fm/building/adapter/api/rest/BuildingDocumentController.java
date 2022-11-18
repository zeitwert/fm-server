
package io.zeitwert.fm.building.adapter.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;

@RestController("buildingDocumentController")
@RequestMapping("/rest/building/buildings")
public class BuildingDocumentController {

	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	private DocumentContentController documentController;

	@RequestMapping(value = "/{id}/coverFoto", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getCoverFoto(@PathVariable Integer id) {
		Integer documentId = this.repo.get(id).getCoverFotoId();
		return this.documentController.getContent(documentId);
	}

}
