package io.zeitwert.fm.dms.adapter.api.rest;

import io.zeitwert.fm.app.model.SessionContextFM;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("documentContentController")
@RequestMapping("/rest/dms/documents")
public class DocumentContentController {

	private final ObjDocumentRepository documentRepository;
	private final SessionContextFM sessionContext;

	public DocumentContentController(ObjDocumentRepository documentRepository, SessionContextFM sessionContext) {
		this.documentRepository = documentRepository;
		this.sessionContext = sessionContext;
	}

	@RequestMapping(value = "/{documentId}/content", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getContent(@PathVariable Integer documentId) {
		if (documentId == null) {
			return ResponseEntity.notFound().build();
		}
		ObjDocument document = this.documentRepository.get(documentId);
		CodeContentType contentType = document.getContentType();
		if (contentType == null) {
			return ResponseEntity.noContent().build();
		}
		ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename(document.getName()).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		return ResponseEntity.ok().contentType(contentType.getMediaType()).headers(headers).body(document.getContent());
	}

	@RequestMapping(value = "/{documentId}/content", method = RequestMethod.POST)
	public ResponseEntity<Void> setContent(@PathVariable Integer documentId,
																				 @RequestParam("file") MultipartFile file) {
		try {
			CodeContentType contentType = CodeContentType.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			ObjDocument document = this.documentRepository.load(documentId);
			document.storeContent(contentType, file.getBytes(), sessionContext.getUser().getId(), sessionContext.getCurrentTime());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
