package io.zeitwert.fm.doc.adapter.api.jsonapi.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelationId;

@JsonSerialize(using = ToStringSerializer.class)
public class DocPartId implements Serializable {

	@JsonApiId
	public Integer id;

	@JsonApiRelationId
	public Integer docId;

	public DocPartId(String id) {
		String[] elements = id.split(":");
		this.docId = Integer.valueOf(elements[0]);
		this.id = Integer.valueOf(elements[1]);
	}

	public DocPartId(Integer docId, Integer partId) {
		this.docId = docId;
		this.id = partId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocId() {
		return this.docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof DocPartId && object.toString().equals(this.toString());
	}

	@Override
	public String toString() {
		return this.docId + ":" + this.id;
	}

}
