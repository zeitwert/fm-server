package io.dddrive.core.ddd.model.base;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.dddrive.core.enums.model.base.EnumConfigBase;
import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.validation.model.AggregatePartValidation;
import io.dddrive.core.validation.model.enums.CodeValidationLevel;
import io.dddrive.core.validation.model.enums.CodeValidationLevelEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregateBaseValidationPathTest {

	@Mock
	private AggregateRepository<TestAggregate> mockRepository;
	@Mock
	private RepositoryDirectory mockRepositoryDirectory;
	@Mock
	private AggregateRepository<ObjTenant> mockTenantRepository;
	@Mock
	private AggregateRepository<ObjUser> mockUserRepository;
	private TestAggregate aggregate;

	@BeforeEach
	void setUp() {
		new EnumConfigBase() {
			{
				try {
					startConfig();
					new CodeValidationLevelEnum();
				} finally {
					endConfig();
				}
			}
		};

		when(mockRepository.getDirectory()).thenReturn(mockRepositoryDirectory);
		when(mockRepositoryDirectory.getRepository(ObjTenant.class)).thenReturn(mockTenantRepository);
		when(mockRepositoryDirectory.getRepository(ObjUser.class)).thenReturn(mockUserRepository);

		aggregate = new TestAggregate(mockRepository);
	}

	@Test
	void addValidation_withPath_shouldStorePathInValidationObject() {
		// Arrange
		String message = "Field is required";
		String path = "user.address.street";
		CodeValidationLevel level = CodeValidationLevelEnum.ERROR;

		// Act
		aggregate.addValidation(level, message, path);

		// Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertEquals(1, validations.size());
		AggregatePartValidation validation = validations.get(0);
		assertEquals(message, validation.getMessage());
		assertEquals(level, validation.getValidationLevel());
		assertEquals(path, validation.getPath());
		assertEquals(0, validation.getSeqNr());
	}

	@Test
	void addValidation_withoutPath_shouldStoreNullPath() {
		// Arrange
		String message = "A general error occurred";
		CodeValidationLevel level = CodeValidationLevelEnum.WARNING;

		// Act
		aggregate.addValidation(level, message, "");

		// Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertEquals(1, validations.size());
		AggregatePartValidation validation = validations.get(0);
		assertEquals(message, validation.getMessage());
		assertEquals(level, validation.getValidationLevel());
		assertEquals("", validation.getPath());
	}

	@Test
	void addValidation_withEntity_shouldStorePathFromEntity() {
		// Arrange
		String message = "Entity is invalid";
		String expectedValidationPath = "test.entity.path";
		CodeValidationLevel level = CodeValidationLevelEnum.ERROR;
		EntityWithPropertiesSPI mockEntity = org.mockito.Mockito.mock(EntityWithPropertiesSPI.class);
		when(mockEntity.getRelativePath()).thenReturn(expectedValidationPath);

		// Act
		aggregate.addValidation(level, message, mockEntity);

		// Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertEquals(1, validations.size());
		AggregatePartValidation validation = validations.get(0);
		assertEquals(message, validation.getMessage());
		assertEquals(level, validation.getValidationLevel());
		assertEquals(expectedValidationPath, validation.getPath());
	}

	@Test
	void addValidation_withProperty_shouldStorePathFromProperty() {
		// Arrange
		String message = "Property is invalid";
		String expectedValidationPath = "test.property";
		CodeValidationLevel level = CodeValidationLevelEnum.ERROR;
		Property<?> mockProperty = org.mockito.Mockito.mock(Property.class);
		when(mockProperty.getRelativePath()).thenReturn(expectedValidationPath);

		// Act
		aggregate.addValidation(level, message, mockProperty);

		// Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertEquals(1, validations.size());
		AggregatePartValidation validation = validations.get(0);
		assertEquals(message, validation.getMessage());
		assertEquals(level, validation.getValidationLevel());
		assertEquals(expectedValidationPath, validation.getPath());
	}

	@Test
	void addValidation_withIndexedPath_shouldStorePathCorrectly() {
		// Arrange
		String message = "Element at index 2 is invalid";
		String path = "items[2].name";
		CodeValidationLevel level = CodeValidationLevelEnum.ERROR;

		// Act
		aggregate.addValidation(level, message, path);

		// Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertEquals(1, validations.size());
		assertEquals(path, validations.get(0).getPath());
	}

	@Test
	void calcVolatile_shouldAddValidations() {
		// Arrange
		VolatileTestAggregate volatileAggregate = new VolatileTestAggregate(mockRepository);
		volatileAggregate.setInsuredValueMissing(true);
		volatileAggregate.setCoordinatesMissing(true);

		// Act
		volatileAggregate.calcVolatile();

		// Assert
		List<AggregatePartValidation> validations = volatileAggregate.getValidations();
		assertEquals(2, validations.size(), "Should contain two validations");

		assertTrue(validations.stream()
						.anyMatch(v -> "insuredValue".equals(v.getPath()) &&
								"Versicherungswert muss erfasst werden".equals(v.getMessage()) &&
								CodeValidationLevelEnum.ERROR.equals(v.getValidationLevel())),
				"Validation for insuredValue with path not found");

		assertTrue(validations.stream()
						.anyMatch(v -> Objects.equals(v.getPath(), "") &&
								"Koordinaten der Immobilie fehlen".equals(v.getMessage()) &&
								CodeValidationLevelEnum.WARNING.equals(v.getValidationLevel())),
				"Validation for coordinates without path not found");
	}

	@Test
	void getValidations_shouldReturnImmutableCopy() {
		// Arrange
		aggregate.addValidation(CodeValidationLevelEnum.ERROR, "test", "");

		// Act & Assert
		List<AggregatePartValidation> validations = aggregate.getValidations();
		assertThrows(UnsupportedOperationException.class, () -> {
			validations.add(null);
		});
	}

	@Test
	void clearValidationList_isCalledByCalcAll() {
		// Arrange
		aggregate.addValidation(CodeValidationLevelEnum.ERROR, "Initial validation", "");
		assertEquals(1, aggregate.getValidations().size());

		// Act
		aggregate.calcAll(); // calcAll clears the list at the beginning

		// Assert
		// Since doCalcAll is empty, no new validations are added.
		assertTrue(aggregate.getValidations().isEmpty());
	}

	private static class TestAggregate extends AggregateBase {

		protected TestAggregate(AggregateRepository<? extends Aggregate> repository) {
			super(repository);
		}

		// Dummy implementations for abstract methods not under test
		@Override
		public Object getId() {
			return "test-id";
		}

		@Override
		public Object getTenantId() {
			return null;
		}

		@Override
		public ObjTenant getTenant() {
			return null;
		}

		@Override
		public ObjUser getOwner() {
			return null;
		}

		@Override
		public void setOwner(ObjUser owner) {
		}

		@Override
		public String getCaption() {
			return "Test Aggregate";
		}

		@Override
		public <T> void setValueByPath(String relativePath, T value) {
		}

		@Override
		public int getVersion() {
			return 1;
		}

		@Override
		public OffsetDateTime getCreatedAt() {
			return null;
		}

		@Override
		public ObjUser getCreatedByUser() {
			return null;
		}

		@Override
		public OffsetDateTime getModifiedAt() {
			return null;
		}

		@Override
		public ObjUser getModifiedByUser() {
			return null;
		}

		@Override
		public boolean isNew() {
			return false;
		}

	}

	private static class VolatileTestAggregate extends TestAggregate {

		private boolean insuredValueMissing = false;
		private boolean coordinatesMissing = false;

		protected VolatileTestAggregate(AggregateRepository<? extends Aggregate> repository) {
			super(repository);
		}

		public void setInsuredValueMissing(boolean insuredValueMissing) {
			this.insuredValueMissing = insuredValueMissing;
		}

		public void setCoordinatesMissing(boolean coordinatesMissing) {
			this.coordinatesMissing = coordinatesMissing;
		}

		@Override
		protected void doCalcVolatile() {
			super.doCalcVolatile();
			this.validateElements();
		}

		private void validateElements() {
			if (this.insuredValueMissing) {
				this.addValidation(CodeValidationLevelEnum.ERROR, "Versicherungswert muss erfasst werden", "insuredValue");
			}
			if (this.coordinatesMissing) {
				this.addValidation(CodeValidationLevelEnum.WARNING, "Koordinaten der Immobilie fehlen", "");
			}
		}

	}

}
