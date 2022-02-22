
package io.zeitwert.fm.contact.model;

import io.zeitwert.ddd.obj.model.ObjPart;

import java.time.LocalDate;

public interface ObjContactPartAnniversary extends ObjPart<ObjContact> {

	String getAnniversaryTypeId();

	void setAnniversaryTypeId(String anniversaryTypeId);

	LocalDate getStartDate();

	void setStartDate(LocalDate startDate);

	String getAnniversaryNotificationId();

	void setAnniversaryNotificationId(String anniversaryNotificationId);

	String getAnniversaryTemplateId();

	void setAnniversaryTemplateId(String anniversaryTemplateId);

	void store();

	void delete();

}
