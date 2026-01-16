package io.zeitwert.data.dsl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.data.DelegatingSessionContext
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import java.time.OffsetDateTime

/**
 * DSL for creating tenants with nested users using Spring repositories.
 *
 * This DSL uses the repository layer to create tenants and users, which ensures proper domain logic
 * is executed (e.g., creating logo images, avatar images, etc.).
 *
 * Usage:
 * ```
 * Tenant.init(tenantRepository, userRepository)
 * Tenant("demo", "Demo", "advisor", "demo/tenant/logo-demo.png") {
 *     adminUser("admin@zeitwert.io", "Admin", "admin", "demo") {
 *         user("user@zeitwert.io", "User", "user", "demo")
 *         account("ACC", "Account Name", "client", "demo/account/logo-ACC.jpg") {
 *             contact(...)
 *         }
 *     }
 * }
 * ```
 */
object Tenant {

	lateinit var directory: RepositoryDirectory

	val tenantRepository: ObjTenantRepository
		get() = directory.getRepository(ObjTenant::class.java) as ObjTenantRepository

	val userRepository: ObjUserRepository
		get() = directory.getRepository(ObjUser::class.java) as ObjUserRepository

	val documentRepository: ObjDocumentRepository
		get() = directory.getRepository(ObjDocument::class.java) as ObjDocumentRepository

	/**
	 * Upload logo content from classpath resource to a document.
	 * Loads the document for writing by ID.
	 *
	 * @param documentId The ID of the logo document to upload content to
	 * @param resourcePath The resource path relative to resources (e.g., "demo/tenant/logo-demo.png")
	 * @param userId The user ID for the upload
	 */
	fun uploadLogoFromResource(
		documentId: Any,
		resourcePath: String,
		userId: Any,
	) {
		val fullPath = "/$resourcePath"
		val inputStream = Tenant::class.java.getResourceAsStream(fullPath)
		if (inputStream == null) {
			println("      Warning: No logo resource found at $resourcePath")
			return
		}

		val extension = resourcePath.substringAfterLast(".", "")
		val contentType = CodeContentType.getItemByExtension(extension)
		if (contentType == null) {
			println("      Warning: Unknown content type for extension '$extension' in $resourcePath")
			return
		}

		val bytes = inputStream.use { it.readBytes() }
		// Load document for writing
		val document = documentRepository.load(documentId)
		document.storeContent(contentType, bytes, userId, OffsetDateTime.now())
		println("      Uploaded logo from $resourcePath")
	}

	fun init(
		directory: RepositoryDirectory,
	) {
		this.directory = directory
	}

	operator fun invoke(
		key: String,
		name: String,
		type: String,
		logoPath: String? = null,
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

		// Upload tenant logo if path is provided
		if (logoPath != null) {
			val tenant = tenantRepository.get(tenantId)
			uploadLogoFromResource(tenant.logoImageId!!, logoPath, context.adminUserId!!)
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
		tenantRepository.transaction {
			tenantRepository.store(newTenant)
		}
		check(newTenant.logoImageId != null) { "Tenant logoImageId is created in domain logic" }

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

		userRepository.transaction {
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
	 *
	 * @param key Account key
	 * @param name Account name
	 * @param accountType Account type
	 * @param logoPath Optional path to logo resource relative to resources (e.g., "demo/account/logo-3032.jpg")
	 * @param init Lambda to configure the account
	 */
	fun account(
		key: String,
		name: String,
		accountType: String,
		logoPath: String? = null,
		init: AccountContext.() -> Unit = {},
	): Int = Account(userId, key, name, accountType, logoPath, init)

}
