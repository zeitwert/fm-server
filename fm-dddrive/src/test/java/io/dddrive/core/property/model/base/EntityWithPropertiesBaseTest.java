package io.dddrive.core.property.model.base;

import io.dddrive.core.ddd.model.*;
import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.AggregateReferenceProperty;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.impl.PartListPropertyImpl;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class EntityWithPropertiesBaseTest {

	@Mock
	private RepositoryDirectory directory;
	@Mock
	private AggregateRepository<TestAggregate> aggregateRepository;
	@Mock
	private AggregateRepository<TestEntity> testEntityRepository;
	@Mock
	private Enumeration<TestEnumerated> enumeration;

	private TestEntity rootEntity;

	@BeforeEach
	void setUp() {
		rootEntity = new TestEntity("", "root", directory);
		lenient().when(directory.getRepository(TestAggregate.class)).thenReturn(aggregateRepository);
		lenient().when(directory.getRepository(TestEntity.class)).thenReturn(testEntityRepository);
		lenient().when(directory.getEnumeration(any(Class.class))).thenReturn(enumeration);
	}

	interface TestAggregate extends Aggregate {

	}

	interface TestEnumerated extends io.dddrive.core.enums.model.Enumerated {

	}

	// Helper classes for testing
	static class TestEntityBase extends EntityWithPropertiesBase {

		protected final String relativePath;
		protected final String path;
		protected final RepositoryDirectory directory;
		private final Map<Integer, Part<?>> partsToAdd = new HashMap<>();
		@Setter
		private boolean frozen = false;

		TestEntityBase(String relativePath, String path, RepositoryDirectory directory) {
			this.relativePath = relativePath;
			this.path = path;
			this.directory = directory;
		}

		public void expectAddPart(Part<?> part) {
			partsToAdd.put(part.getId(), part);
		}

		@Override
		public RepositoryDirectory getDirectory() {
			return directory;
		}

		@Override
		public String getRelativePath() {
			return path;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public boolean isFrozen() {
			return frozen;
		}

		@Override
		protected boolean doLogChange(String propertyName) {
			return false;
		}

		@Override
		public void fireFieldChange(String type, String path, String value, String oldValue, boolean isCalc) {
			// No-op for these tests
		}

		@Override
		public boolean isInLoad() {
			return false;
		}

		@Override
		public boolean isInCalc() {
			return false;
		}

		@Override
		public Part<?> doAddPart(Property<?> property, Integer partId) {
			return partsToAdd.get(partId);
		}

		@Override
		public void doBeforeSet(Property<?> property, @Nullable Object value, @Nullable Object oldValue) {
		}

		@Override
		public void doAfterSet(Property<?> property) {
		}

		@Override
		public void doAfterClear(Property<?> property) {
		}

		@Override
		public void doAfterAdd(Property<?> property, Part<?> part) {
		}

		@Override
		public void doAfterRemove(Property<?> property) {
		}

	}

	// Helper classes for testing
	static class TestEntity extends TestEntityBase implements Aggregate {

		TestEntity(String relativePath, String path, RepositoryDirectory directory) {
			super(relativePath, path, directory);
		}

		@Override
		public AggregateMeta getMeta() {
			return null;
		}

		@Override
		public Object getId() {
			return path;
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
			return null;
		}

	}

	static class TestPart extends TestEntityBase implements Part<TestAggregate> {

		private final int id;

		TestPart(int id, String relativePath, String path, RepositoryDirectory directory) {
			super(relativePath, path, directory);
			this.id = id;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public TestAggregate getAggregate() {
			return null;
		}

		@Override
		public PartMeta<TestAggregate> getMeta() {
			return null;
		}

	}

	@Nested
	class SetValueByPathSuccessTests {

		@Test
		void shouldSetSimpleBaseProperty() {
			BaseProperty<String> prop = rootEntity.addBaseProperty("myProp", String.class);
			prop.setValue("initial");

			rootEntity.setValueByPath("myProp", "updated");

			assertEquals("updated", prop.getValue());
		}

		@Test
		void shouldSetNestedPropertyInReference() {
			TestEntity referencedEntity = new TestEntity("ref", "root.ref", directory);
			BaseProperty<Integer> nestedProp = referencedEntity.addBaseProperty("nestedProp", Integer.class);
			nestedProp.setValue(100);

			AggregateReferenceProperty<TestEntity> refProp = rootEntity.addReferenceProperty("ref", TestEntity.class);
			lenient().when(testEntityRepository.get(any())).thenReturn(referencedEntity);
			refProp.setValue(referencedEntity);


			rootEntity.setValueByPath("ref.nestedProp", 200);

			assertEquals(200, nestedProp.getValue());
		}

		@Test
		void shouldSetNestedPropertyInPartList() {
			PartListProperty<TestPart> partListProp = rootEntity.addPartListProperty("parts", TestPart.class);
			TestPart part0 = new TestPart(0, "parts[0]", "root.parts[0]", directory);
			BaseProperty<String> partProp = part0.addBaseProperty("partProp", String.class);
			partProp.setValue("partValue");
			rootEntity.expectAddPart(part0);
			partListProp.addPart(part0.getId());

			rootEntity.setValueByPath("parts[0].partProp", "newPartValue");

			assertEquals("newPartValue", partProp.getValue());
		}

		@Test
		void shouldSetReferencePropertyDirectly() {
			AggregateReferenceProperty<TestEntity> refProp = rootEntity.addReferenceProperty("ref", TestEntity.class);
			TestEntity referencedEntity = new TestEntity("", "e", directory);

			rootEntity.setValueByPath("ref", referencedEntity);

			// The setValue on ReferenceProperty calls setId.
			// So we check if the ID was set correctly.
			assertEquals("e", refProp.getId());
		}

		@Test
		void shouldSetReferenceIdProperty() {
			AggregateReferenceProperty<TestEntity> refProp = rootEntity.addReferenceProperty("ref", TestEntity.class);

			rootEntity.setValueByPath("ref.id", "some-id");

			assertEquals("some-id", refProp.getId());
		}

	}

	@Nested
	class SetValueByPathFailureTests {

		@Test
		void shouldThrowWhenEntityIsFrozen() {
			rootEntity.addBaseProperty("myProp", String.class);
			rootEntity.setFrozen(true);

			assertThrows(IllegalStateException.class, () -> rootEntity.setValueByPath("myProp", "value"));
		}

		@Test
		void shouldThrowOnNullPath() {
			assertThrows(NullPointerException.class, () -> rootEntity.setValueByPath(null, "value"));
		}

		@Test
		void shouldThrowOnEmptyPath() {
			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("", "value"));
		}

		@Test
		void shouldThrowWhenPropertyNotFound() {
			assertThrows(NullPointerException.class, () -> rootEntity.setValueByPath("nonExistentProp", "value"));
		}

		@Test
		void shouldThrowOnInvalidSegmentFormat() {
			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("prop[a]", "v"));
		}

		@Test
		void shouldThrowWhenIndexingNonListProperty() {
			rootEntity.addBaseProperty("myProp", String.class);
			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("myProp[0]", "value"));
		}

		@Test
		void shouldThrowWhenIndexIsOutOfBounds() {
			PartListProperty<TestPart> partListProp = rootEntity.addPartListProperty("parts", TestPart.class);
			TestPart part0 = new TestPart(0, "parts[0]", "root.parts[0]", directory);
			part0.addBaseProperty("partProp", String.class);
			rootEntity.expectAddPart(part0);
			partListProp.addPart(part0.getId());

			assertThrows(IndexOutOfBoundsException.class, () -> rootEntity.setValueByPath("parts[1].partProp", "value"));
		}

		@Test
		void shouldThrowWhenPathEndsWithListAccess() {
			PartListProperty<TestPart> partListProp = rootEntity.addPartListProperty("parts", TestPart.class);
			TestPart part0 = new TestPart(0, "parts[0]", "root.parts[0]", directory);
			rootEntity.expectAddPart(part0);
			partListProp.addPart(part0.getId());

			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("parts[0]", "value"));
		}

		@Test
		void shouldThrowWhenSettingCollectionProperty() {
			rootEntity.addPartListProperty("parts", TestPart.class);
			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("parts", new ArrayList<>()));
		}

		@Test
		void shouldThrowWhenAccessingPartListWithDotNotation() {
			PartListProperty<TestPart> partListProp = rootEntity.addPartListProperty("parts", TestPart.class);
			TestPart part0 = new TestPart(0, "parts[0]", "root.parts[0]", directory);
			part0.addBaseProperty("partProp", String.class);
			rootEntity.expectAddPart(part0);
			partListProp.addPart(part0.getId());

			assertThrows(IllegalArgumentException.class, () -> rootEntity.setValueByPath("parts.0.partProp", "value"));
		}

		@Test
		void shouldThrowWhenReferenceIsNull() {
			AggregateReferenceProperty<TestEntity> refProp = rootEntity.addReferenceProperty("ref", TestEntity.class);
			refProp.setValue(null); // Explicitly set to null

			assertThrows(NullPointerException.class, () -> rootEntity.setValueByPath("ref.nestedProp", "value"));
		}

		@Test
		void shouldThrowWhenPartInListIsNull() {
			// To simulate this, we can mock the getPart method on the property
			PartListProperty<TestPart> mockedPartList = new PartListPropertyImpl<>(rootEntity, "parts", TestPart.class) {
				@Override
				public TestPart getPart(int index) {
					if (index == 0) return null;
					throw new IndexOutOfBoundsException();
				}

				@Override
				public int getPartCount() {
					return 1;
				}
			};
			rootEntity.addProperty(mockedPartList);


			assertThrows(IllegalStateException.class, () -> rootEntity.setValueByPath("parts[0].prop", "value"));
		}

		@Test
		void shouldThrowOnDeeperNavigationFailure() {
			TestEntity referencedEntity = new TestEntity("ref", "root.ref", directory);
			AggregateReferenceProperty<TestEntity> refProp = rootEntity.addReferenceProperty("ref", TestEntity.class);
			lenient().when(testEntityRepository.get(any())).thenReturn(referencedEntity);
			refProp.setValue(referencedEntity);

			// 'anotherRef' does not exist on referencedEntity
			assertThrows(NullPointerException.class, () -> rootEntity.setValueByPath("ref.anotherRef.prop", "value"));
		}

	}

}
