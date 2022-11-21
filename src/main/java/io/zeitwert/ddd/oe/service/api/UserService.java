package io.zeitwert.ddd.oe.service.api;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjUser;

import java.util.Map;
import java.util.Optional;

public interface UserService {

	ObjUser getUser(Integer userId);

	Optional<ObjUser> getByEmail(String email);

	EnumeratedDto getUserEnumerated(Integer userId);

	Map<String, Integer> getStatistics();

}
