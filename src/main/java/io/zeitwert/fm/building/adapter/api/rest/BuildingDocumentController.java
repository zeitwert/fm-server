
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.dms.adapter.api.rest.DocumentContentController;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

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

	@RequestMapping(value = "/{id}/coverFoto", method = RequestMethod.POST)
	public ResponseEntity<Void> setCoverFoto(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
		try {
			ObjBuilding building = this.repo.get(id);
			ObjDocument document = building.getCoverFoto();
			CodeContentType contentType = CodeContentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			document.storeContent(contentType, file.getBytes());
			building.calcAll();
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
