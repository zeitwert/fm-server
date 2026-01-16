package io.zeitwert.app.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.SimpleTransactionStatus

@Configuration
@ConditionalOnProperty(
	name = ["zeitwert.persistence.type"],
	havingValue = "mem",
	matchIfMissing = false,
)
open class MemTransactionConfiguration {

	@Bean(name = ["transactionManager"])
	@Primary
	open fun transactionManager(): PlatformTransactionManager = object : PlatformTransactionManager {
		override fun getTransaction(definition: TransactionDefinition): TransactionStatus {
			return SimpleTransactionStatus()
		}

		override fun commit(status: TransactionStatus) {
			// no-op for in-memory persistence
		}

		override fun rollback(status: TransactionStatus) {
			// no-op for in-memory persistence
		}
	}
}
