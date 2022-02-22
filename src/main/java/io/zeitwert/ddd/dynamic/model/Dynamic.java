
package io.zeitwert.ddd.dynamic.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;

@JsonApiResource(type = "dynamic", resourcePath = "dynamic/dynamics")
public class Dynamic {

	private final Integer id = Long.valueOf(Math.round(1000.0 * Math.random())).intValue();

	@JsonApiId
	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return "dynamic " + this.getId();
	}

	@JsonAnyGetter
	@JsonIgnore // otherwise a "properties" field is created :-/
	public Map<String, String> getProperties() {
		Map<String, String> fields = new HashMap<>();
		fields.put("description", "bla bla bla");
		fields.put("age", "28");
		return fields;
	}

	@JsonAnySetter
	public void setProperties(String propertyName, String propertyValue) {
		throw new BadRequestException("Operation not supported");
	}

}
