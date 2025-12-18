package io.dddrive.dddrive.config

import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.ddd.model.enums.CodeAggregateType
import io.dddrive.core.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.core.ddd.model.enums.CodePartListTypeEnum
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.enums.model.base.EnumConfigBase
import io.dddrive.core.validation.model.enums.CodeValidationLevelEnum
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Base DDDrive configuration providing core beans for the DDDrive framework.
 *
 * This configuration provides fundamental beans that are needed across all modules:
 * - Core enumeration types (CodeAggregateTypeEnum, CodeCaseDefEnum, etc.)
 * - Repository directory
 *
 * Domain-specific configurations should import this base configuration and add their own
 * domain-specific initializations.
 */
@Configuration("dddriveConfig")
open class DDDriveConfig : EnumConfigBase() {

	@Bean("directory")
	open fun directory(): RepositoryDirectory = RepositoryDirectory.instance

	@Bean("aggregateTypeEnum")
	open fun aggregateTypeEnum(): CodeAggregateTypeEnum {
		try {
			startConfig()
			val enum = CodeAggregateTypeEnum()
			// Register core OE aggregate types
			enum.addItem(CodeAggregateType("objTenant", "Tenant"))
			enum.addItem(CodeAggregateType("objUser", "User"))
			return enum
		} finally {
			endConfig()
		}
	}

	@Bean("partListTypeEnum")
	open fun partListTypeEnum(): CodePartListTypeEnum {
		try {
			startConfig()
			val enum = CodePartListTypeEnum()
			return enum
		} finally {
			endConfig()
		}
	}

	@Bean("caseDefEnum")
	open fun caseDefEnum(): CodeCaseDefEnum {
		try {
			startConfig()
			return CodeCaseDefEnum()
		} finally {
			endConfig()
		}
	}

	@Bean("caseStageEnum")
	open fun caseStageEnum(): CodeCaseStageEnum {
		try {
			startConfig()
			return CodeCaseStageEnum()
		} finally {
			endConfig()
		}
	}

	@Bean("validationLevelEnum")
	open fun validationLevelEnum(): CodeValidationLevelEnum {
		try {
			startConfig()
			return CodeValidationLevelEnum()
		} finally {
			endConfig()
		}
	}
}
