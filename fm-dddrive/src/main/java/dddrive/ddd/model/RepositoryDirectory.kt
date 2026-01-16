package dddrive.ddd.model

import dddrive.ddd.model.impl.RepositoryDirectoryImpl

interface RepositoryDirectory {

	fun <A : Aggregate> getRepository(intfClass: Class<A>): AggregateRepository<A>

	fun <A : Aggregate, P : Part<A>> getPartRepository(intfClass: Class<P>): PartRepository<A, P>

	fun <A : Aggregate> getPersistenceProvider(intfClass: Class<A>): AggregatePersistenceProvider<A>

	fun <P : Part<*>> getPartPersistenceProvider(intfClass: Class<P>): PartPersistenceProvider<P>

	fun <E : Enumerated> getEnumeration(enumClass: Class<E>): Enumeration<E>

	fun getEnumeration(
		module: String,
		enumName: String,
	): Enumeration<out Enumerated>

	companion object {

		private var _instance: RepositoryDirectory = RepositoryDirectoryImpl()

		@JvmStatic
		val instance: RepositoryDirectory
			get() = _instance

		@JvmStatic
		fun setInstance(directory: RepositoryDirectory) {
			_instance = directory
		}

		@JvmStatic
		fun reset() {
			_instance = RepositoryDirectoryImpl()
		}

		/**
		 * Clear all providers and repositories but preserve enumerations.
		 * Use this when resetting for a new test context while keeping
		 * enum items that were registered during class loading.
		 */
		@JvmStatic
		fun resetProvidersAndRepositories() {
			(_instance as RepositoryDirectoryImpl).clearProvidersAndRepositories()
		}
	}

}
