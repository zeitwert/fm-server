package io.zeitwert.app.session.model

import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Provides access to kernel tenant and user IDs.
 *
 * This service is initialized during bootstrap and caches the kernel tenant/user IDs
 * for efficient access throughout the application. It replaces the hardcoded
 * KERNEL_TENANT_ID constant to support dynamic ID generation (e.g., memory persistence).
 *
 * Note: Uses @Lazy on dependencies to break the circular reference between
 * KernelContext -> Repositories -> PersistenceProviders -> KernelContext.
 */
@Component
class KernelContext(
	@Lazy private val tenantRepository: ObjTenantRepository,
	@Lazy private val userRepository: ObjUserRepository,
) {

	/**
	 * The kernel tenant ID, lazily loaded on first access.
	 * Finds the tenant by key "kernel".
	 */
	val kernelTenantId: Any by lazy {
		tenantRepository
			.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY)
			.orElseThrow { IllegalStateException("Kernel tenant not found with key '${ObjTenantRepository.KERNEL_TENANT_KEY}'") }
			.id!!
	}

	/**
	 * The kernel user ID, lazily loaded on first access.
	 * Returns the ID of the user with the kernel user email.
	 */
	val kernelUserId: Any by lazy {
		userRepository
			.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL)
			.orElseThrow { IllegalStateException("Kernel user not found with email '${ObjUserRepository.KERNEL_USER_EMAIL}'") }
			.id!!
	}

	/**
	 * Check if a tenant ID is the kernel tenant.
	 */
	fun isKernelTenant(tenantId: Any): Boolean = tenantId == kernelTenantId
}
