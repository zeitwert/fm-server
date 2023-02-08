package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelationId;

@JsonSerialize(using = ToStringSerializer.class)
public class ObjPartId implements Serializable {

	@JsonApiId
	public Integer id;

	@JsonApiRelationId
	public Integer objId;

	public ObjPartId(String id) {
		String[] elements = id.split(":");
		this.objId = Integer.valueOf(elements[0]);
		this.id = Integer.valueOf(elements[1]);
	}

	public ObjPartId(Integer objId, Integer partId) {
		this.objId = objId;
		this.id = partId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getObjId() {
		return this.objId;
	}

	public void setObjId(Integer objId) {
		this.objId = objId;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof ObjPartId && object.toString().equals(this.toString());
	}

	@Override
	public String toString() {
		return this.objId + ":" + this.id;
	}

}
