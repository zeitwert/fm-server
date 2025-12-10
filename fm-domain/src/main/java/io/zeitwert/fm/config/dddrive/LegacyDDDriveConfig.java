package io.zeitwert.fm.config.dddrive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.dddrive.app.service.api.AppContextSPI;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.ddd.model.enums.CodePartListTypeEnum;
import io.dddrive.doc.model.enums.CodeCaseDefEnum;
import io.dddrive.doc.model.enums.CodeCaseStageEnum;
import io.dddrive.validation.model.enums.CodeValidationLevelEnum;

@Configuration
public class LegacyDDDriveConfig {

	@Autowired
	private final AppContextSPI appContext = null;

	@Bean("codeAggregateTypeEnum")
	public CodeAggregateTypeEnum aggregateTypeEnum() {
		return new CodeAggregateTypeEnum(appContext);
	}

	@Bean("codePartListTypeEnum")
	public CodePartListTypeEnum partListTypeEnum() {
		return new CodePartListTypeEnum(appContext);
	}

	@Bean("codeCaseDefEnum")
	public CodeCaseDefEnum caseDefEnum() {
		return new CodeCaseDefEnum(appContext);
	}

	@Bean("codeCaseStageEnum")
	public CodeCaseStageEnum caseStageEnum() {
		return new CodeCaseStageEnum(appContext);
	}

	@Bean("codeValidationLevelEnum")
	public CodeValidationLevelEnum validationLevelEnum() {
		return new CodeValidationLevelEnum(appContext);
	}

}

