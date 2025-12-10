import { EntityGender, EntityTypeInfo } from "@zeitwert/ui-model";

export function getNewEntityText(entityType: EntityTypeInfo): string {
	switch (entityType.gender) {
		case EntityGender.MALE:
			return "Neuer " + entityType.labelSingular;
		case EntityGender.FEMALE:
			return "Neue " + entityType.labelSingular;
		case EntityGender.NEUTER:
			return "Neues " + entityType.labelSingular;
		default:
			return "Neu: " + entityType.labelSingular;
	}
}

export function getEditEntityText(entityType: EntityTypeInfo): string {
	return "Modifikation " + entityType.labelSingular;
}

export function getImportEntityText(entityType: EntityTypeInfo): string {
	return entityType.labelSingular + " Importieren";
}
