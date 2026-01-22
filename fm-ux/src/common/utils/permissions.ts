import {
	ADVISOR_TENANT,
	KERNEL_TENANT,
	ROLE_ADMIN,
	ROLE_APP_ADMIN,
	ROLE_SUPER_USER,
} from "../../session/model/types";

/**
 * Check if a user can modify an entity based on entity type and role.
 * This provides a central place for entity-specific permission logic.
 *
 * @param entityType - The type of entity (e.g., "account", "contact")
 * @param role - The user's role ID
 * @returns boolean indicating if the user can modify the entity
 */
export function canModifyEntity(entityType: string, role: string): boolean {
	switch (entityType) {
		case "account":
			return isAdmin(role);
		case "contact":
			return true;
		default:
			return true;
	}
}

/**
 * Check if a user can create an entity based on entity type, role, and tenant type.
 *
 * @param entityType - The type of entity (e.g., "account", "contact")
 * @param role - The user's role ID
 * @param tenantType - The tenant type ID (e.g., "kernel", "advisor", "community")
 * @returns boolean indicating if the user can create the entity
 */
export function canCreateEntity(entityType: string, role: string, tenantType: string): boolean {
	switch (entityType) {
		case "tenant":
		case "user": {
			// OE entities can only be created in kernel tenant
			return tenantType === KERNEL_TENANT;
		}
		case "account": {
			// Accounts can only be created in kernel or advisory tenants
			const canCreateAccount = tenantType === KERNEL_TENANT || tenantType === ADVISOR_TENANT;
			return canCreateAccount && isAdmin(role);
		}
		default: {
			// All other entities can only be created in non-kernel tenants
			const isNonKernelTenant = tenantType !== KERNEL_TENANT;
			return isNonKernelTenant;
		}
	}
}

function isAdmin(role: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}
