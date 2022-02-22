package fm.comunas.ddd.session.adapter.rest.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SessionContextRequest {

	private Map<String, Object> customValues;

}
