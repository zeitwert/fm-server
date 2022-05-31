package io.zeitwert.fm.dms.adapter.api.rest;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeContentTypeEnum;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("documentContentController")
@RequestMapping("/rest/dms/documents")
public class DocumentContentController {

	private final SessionInfo sessionInfo;
	private final ObjDocumentRepository documentRepository;
	private final CodeContentTypeEnum contentTypeEnum;

	@Autowired
	public DocumentContentController(SessionInfo sessionInfo, ObjDocumentRepository documentRepository,
			CodeContentTypeEnum contentTypeEnum) {
		this.sessionInfo = sessionInfo;
		this.documentRepository = documentRepository;
		this.contentTypeEnum = contentTypeEnum;
	}

	@RequestMapping(value = "/{documentId}/content", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getContent(@PathVariable Integer documentId) {
		ObjDocument document = this.documentRepository.get(this.sessionInfo, documentId);
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
			ObjDocument document = this.documentRepository.get(this.sessionInfo, documentId);
			CodeContentType contentType = this.contentTypeEnum.getContentType(file.getContentType(),
					file.getOriginalFilename());
			if (contentType == null) {
				return ResponseEntity.badRequest().body(null);
			}
			this.documentRepository.storeContent(sessionInfo, document, contentType, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(null);
		}
		return ResponseEntity.ok().body(null);
	}

}
