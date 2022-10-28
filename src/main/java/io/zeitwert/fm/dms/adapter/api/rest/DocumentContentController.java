package io.zeitwert.fm.dms.adapter.api.rest;

import java.io.IOException;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

@RestController("documentContentController")
@RequestMapping("/rest/dms/documents")
public class DocumentContentController {

	private final ObjDocumentRepository documentRepository;

	public DocumentContentController(ObjDocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@RequestMapping(value = "/{documentId}/content", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getContent(@PathVariable Integer documentId) {
		ObjDocument document = this.documentRepository.get(documentId);
		CodeContentType contentType = document.getContentType();
		if (contentType == null) {
			return ResponseEntity.notFound().build();
		}
		return this.getContentResponse(document.getName(), contentType, document.getContent());
	}

	private ResponseEntity<byte[]> getContentResponse(String name, CodeContentType contentType, byte[] content) {
		ContentDisposition contentDisposition = ContentDisposition.builder("inline").filename(name).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		return ResponseEntity.ok().contentType(contentType.getMediaType()).headers(headers).body(content);
	}

	@RequestMapping(value = "/{documentId}/content", method = RequestMethod.POST)
	public ResponseEntity<Void> storeContent(@PathVariable Integer documentId,
			@RequestParam("file") MultipartFile file) {
		try {
			ObjDocument document = this.documentRepository.get(documentId);
			CodeContentType contentType = CodeContentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			document.storeContent(contentType, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
