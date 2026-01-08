package io.zeitwert.config.dsl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.config.DelegatingSessionContext
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.jooq.DSLContext

/**
 * DSL for creating tenants with nested users using Spring repositories.
 *
 * This DSL uses the repository layer to create tenants and users, which ensures proper domain logic
 * is executed (e.g., creating logo images, avatar images, etc.).
 *
 * Usage:
 * ```
 * Tenant.init(tenantRepository, userRepository)
 * Tenant("demo", "Demo", "advisor") {
 *     adminUser("admin@zeitwert.io", "Admin", "admin", "demo") {
 *         user("user@zeitwert.io", "User", "user", "demo")
 *         account("ACC", "Account Name", "client") {
 *             contact(...)
 *         }
 *     }
 * }
 * ```
 */
object Tenant {

	lateinit var dslContext: DSLContext
	lateinit var directory: RepositoryDirectory

	val tenantRepository: ObjTenantRepository
		get() = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository

	val userRepository: ObjUserRepository
		get() = directory.getRepository(ObjUser::class.java) as ObjUserRepository

	fun init(
		dslContext: DSLContext,
		directory: RepositoryDirectory,
	) {
		this.dslContext = dslContext
		this.directory = directory
	}

	operator fun invoke(
		key: String,
		name: String,
		type: String,
		init: TenantContext.() -> Unit,
	): Pair<Int, Int> {
		val context = TenantContext(key, name, type)

		// Create or get tenant first
		val tenantId = getOrCreateTenant(context)

		// Set tenant ID in session context for nested operations
		DelegatingSessionContext.setTenantId(tenantId)
		context.tenantId = tenantId

		// Now execute the DSL block with tenant context set
		context.init()

		// Process any simple users (non-admin)
		context.users.forEach { userCtx ->
			val tenant = tenantRepository.get(tenantId)
			getOrCreateUser(tenant, userCtx)
		}

		return Pair(tenantId, context.adminUserId!!)
	}

	private fun getOrCreateTenant(ctx: TenantContext): Int {
		// Check if tenant already exists
		val tenant = tenantRepository.getByKey(ctx.key)

		if (tenant.isPresent) {
			val tenant = tenant.get()
			val tenantId = tenant.id as Int
			println("    Tenant ${ctx.key} already exists (id=$tenantId)")
			return tenantId
		}

		// Create new tenant via repository
		val newTenant = tenantRepository.create()
		newTenant.key = ctx.key
		newTenant.name = ctx.name
		newTenant.tenantType = CodeTenantType.getTenantType(ctx.type)
		dslContext.transaction { _ ->
			tenantRepository.store(newTenant)
		}

		val tenantId = newTenant.id as Int
		println("    Created tenant ${ctx.key} - ${ctx.name} (id=$tenantId)")

		return tenantId
	}

	internal fun getOrCreateUser(
		tenant: ObjTenant,
		ctx: UserContext,
	): Int {
		// Check if user already exists
		val user = userRepository.getByEmail(ctx.email)

		if (user.isPresent) {
			val userId = user.get().id as Int
			println("      User ${ctx.email} already exists (id=$userId)")
			return userId
		}

		// Create new user via repository
		val newUser = userRepository.create()
		newUser.email = ctx.email
		newUser.name = ctx.name
		newUser.role = CodeUserRole.getUserRole(ctx.role)
		newUser.password = userRepository.passwordEncoder.encode(ctx.password)

		// Associate user with tenant
		newUser.tenantSet.add(tenant.id)

		dslContext.transaction { _ ->
			userRepository.store(newUser)
		}

		val userId = newUser.id as Int
		println("      Created user ${ctx.email} - ${ctx.name} (id=$userId)")

		return userId
	}
}

@DslMarker
annotation class TenantDslMarker

@TenantDslMarker
class TenantContext(
	val key: String,
	val name: String,
	val type: String,
	var tenantId: Int? = null,
	var adminUserId: Int? = null,
) {

	internal val users = mutableListOf<UserContext>()

	/**
	 * Create an admin user with a nested context for creating additional users and accounts.
	 * The admin user's ID will be set in the session context for all nested operations.
	 */
	fun adminUser(
		email: String,
		name: String,
		role: String,
		password: String,
		init: AdminUserContext.() -> Unit,
	) {
		val tenant = Tenant.tenantRepository.get(tenantId!!)

		// Create the admin user
		val userId = Tenant.getOrCreateUser(tenant, UserContext(email, name, role, password))

		// Set user ID in session context for nested operations
		DelegatingSessionContext.setUserId(userId)
		adminUserId = userId

		// Execute nested block with user context set
		AdminUserContext(tenant, userId).init()
	}

	/**
	 * Create a simple user without nested context.
	 * Use this for users that don't need to create accounts or other objects.
	 */
	fun user(
		email: String,
		name: String,
		role: String,
		password: String,
	) {
		users += UserContext(email, name, role, password)
	}

}

@TenantDslMarker
class UserContext(
	val email: String,
	val name: String,
	val role: String,
	val password: String,
)

/**
 * Context for admin user that allows creating additional users and accounts.
 */
@TenantDslMarker
class AdminUserContext(
	val tenant: ObjTenant,
	val userId: Int,
) {

	/**
	 * Create a simple user within this admin context.
	 */
	fun user(
		email: String,
		name: String,
		role: String,
		password: String,
	) {
		Tenant.getOrCreateUser(tenant, UserContext(email, name, role, password))
	}

	/**
	 * Create an account with optional nested contacts.
	 */
	fun account(
		key: String,
		name: String,
		accountType: String,
		init: AccountContext.() -> Unit = {},
	): Int = Account(userId, key, name, accountType, init)

}
