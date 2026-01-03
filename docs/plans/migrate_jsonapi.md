# JSON API Migration Cookbook

This cookbook describes how to migrate a JSON API repository from the legacy Java-based pattern to the new generic
Kotlin pattern.

## Reference Implementations

### ObjAccount (basic relationships)

- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/impl/ObjAccountApiRepositoryImpl.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/impl/ObjAccountDtoAdapter.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/dto/ObjAccountDto.kt`

### ObjUser (field mapping with ReferenceSetProperty)

- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/impl/ObjUserApiRepositoryImpl.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/impl/ObjUserDtoAdapter.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/dto/ObjUserDto.kt`

### ObjTenant (simple with asEnumerated)

- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/impl/ObjTenantApiRepositoryImpl.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/impl/ObjTenantDtoAdapter.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/oe/adapter/api/jsonapi/dto/ObjTenantDto.kt`

## Migration Steps

### 1. Create the new Repository (Kotlin)

**Old pattern (Java):**

```java
@Controller("objXxxApiRepository")
public class ObjXxxApiRepositoryImpl
    extends AggregateApiRepositoryBase<ObjXxx, ObjXxxDto>
    implements ObjXxxApiRepository {

    public ObjXxxApiRepositoryImpl(
            ObjXxxRepository repository,
            SessionContext requestCtx,
            ObjUserRepository userRepository,
            ObjXxxDtoAdapter dtoAdapter) {
        super(ObjXxxDto.class, requestCtx, userRepository, repository, dtoAdapter);
    }
}
```

**New pattern (Kotlin):**

```kotlin
@Controller("objXxxApiRepository")
open class ObjXxxApiRepositoryImpl(
    directory: RepositoryDirectory,
    repository: ObjXxxRepository,
    adapter: ObjXxxDtoAdapter,
    sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjXxx, ObjXxxDto>(
    resourceClass = ObjXxxDto::class.java,
    directory = directory,
    repository = repository,
    adapter = adapter,
    sessionCtx = sessionCtx,
)
```

**Required imports:**

```kotlin
import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.dddrive.app.model.SessionContext
```

### 2. Create the new Adapter (Kotlin)

**Old pattern (Java):**

```java
@Component("objXxxDtoAdapter")
public class ObjXxxDtoAdapter extends ObjDtoAdapterBase<ObjXxx, ObjXxxDto> {
    // Manual fromAggregate() and toAggregate() implementations
    // Manual relationship handling
}
```

**New pattern (Kotlin):**

```kotlin
@Component("objXxxDtoAdapter")
class ObjXxxDtoAdapter(
    directory: RepositoryDirectory,
) : GenericObjDtoAdapterBase<ObjXxx, ObjXxxDto>(directory, { ObjXxxDto() }) {
    init {
        // Optional: Configure custom relationships
        // relationship("relationId", "resourceType", "sourcePropertyName")
    }
}
```

**Required imports:**

```kotlin
import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoAdapterBase
```

**Notes:**

- The base class handles `fromAggregate()` and `toAggregate()` automatically using metadata
- Parts are handled generically - no manual part mapping needed
- Use `init {}` block to configure custom relationships and field mappings

#### Adapter Configuration Options

The adapter supports several configuration methods in the `init {}` block:

**Relationship Configuration** (for JSON API relationships with `@JsonApiRelationId`):

```kotlin
init {
    // Single relationship: maps DTO field "logoId" to entity property "logoImage"
    relationship("logoId", "document", "logoImage")

    // Relationship with custom data source:
    relationship("mainContactId", "contact") { entity, dto ->
        // Custom logic to compute the related ID
        DtoUtils.idToString(someValue)
    }

    // Collection relationship:
    relationshipSet("contactIds", "contact", "contacts")
}
```

**Field Configuration** (for regular fields with name mapping or type conversion):

```kotlin
init {
    // Exclude a property from automatic handling
    exclude("tenantSet")

    // Simple field mapping: maps DTO field "tenants" from entity property "tenantSet"
    // Intelligent type detection:
    //   - ReferenceSetProperty<T> → List<EnumeratedDto> (loads entities)
    //   - AggregateReferenceProperty<T> → EnumeratedDto (loads entity)
    field("tenants", "tenantSet")

    // Custom field with outgoing/incoming functions:
    field("customField",
        outgoing = { entity, dto -> /* compute value */ },
        incoming = { dto, entity -> /* apply value */ }
    )
}
```

**Keep `asEnumerated()` Method** if used by other components:

```kotlin
fun asEnumerated(obj: ObjXxx?): EnumeratedDto? {
    return if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)
}
```

Check usages with: `grep -r "xxxDtoAdapter.asEnumerated"` - if found, keep this method.

### 3. Create the new DTO (Kotlin)

**Old pattern (Java with Lombok):**

```java
@Data()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonApiResource(type = "xxx", resourcePath = "xxx/xxxs")
public class ObjXxxDto extends ObjDtoBase<ObjXxx> {
    @JsonApiRelationId
    private String accountId;
    // ... more fields
}
```

**New pattern (Kotlin):**

```kotlin
@JsonApiResource(type = "xxx", resourcePath = "xxx/xxxs")
class ObjXxxDto : GenericObjDtoBase<ObjXxx>() {

    @JsonApiRelationId
    var accountId: String? = null
        get() = getRelation("accountId") as String?
        set(value) {
            setRelation("accountId", value)
            field = value
        }

    @JsonApiRelation(serialize = SerializeType.LAZY)
    var account: ObjAccountDto? = null
}
```

**Required imports:**

```kotlin
import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoBase
```

**Important:**

- Relation ID fields for already migrated DTO **MUST be `String?`**, not `Int?` or `Object?`. Crnk uses reflection to
	set values and passes String IDs.
- Only declare explicit properties for relationships that need `@JsonApiRelation`/`@JsonApiRelationId` annotations
- Regular attributes and parts, part lists are handled automatically by the generic base class

### 4. Delete Obsolete Files

The following files are no longer needed and should be deleted:

1. **The old Java Repository** - e.g., `ObjXxxApiRepositoryImpl.java`
2. **The old Java Adapter** - e.g., `ObjXxxDtoAdapter.java`
3. **The old Java DTO** - e.g., `ObjXxxDto.java`
4. **Part DTOs** - e.g., `ObjXxxPartYyyDto.java` (parts are handled generically)
5. **API Repository Interface** - e.g., `ObjXxxApiRepository.java` (no longer needed)

### 5. Check for Downstream Consumers

Search for usages of the DTO and adapter in other files:

```bash
# Search for DTO usages
grep -r "ObjXxxDto" --include="*.java" --include="*.kt"

# Search for adapter usages (especially asEnumerated)
grep -r "xxxDtoAdapter" --include="*.java" --include="*.kt"
```

**Common issues to fix:**

**1. `dto.getId()` returns String now:**

```java
// Old code (will fail):
contacts.stream().map(ct -> ct.getId()).collect(...)

// New code:
contacts.stream().map(ct -> DtoUtils.idFromString(ct.getId())).collect(...)
```

**2. `adapter.fromAggregate()` requires non-null parameter:**

The new Kotlin adapter's `fromAggregate(A)` requires a non-null aggregate. If callers pass nullable:

```kotlin
// Old code (will fail):
userDtoAdapter.fromAggregate(this.sessionContext.user as ObjUser?)

// New code - use safe call:
val user = this.sessionContext.user as? ObjUser
user?.let { userDtoAdapter.fromAggregate(it) }
```

**3. Adapter's `asEnumerated()` method:**

If the old adapter had `asEnumerated()` used by other components (e.g., `ObjBuildingPartRatingDto`),
keep it in the new adapter:

```kotlin
fun asEnumerated(obj: ObjXxx?): EnumeratedDto? {
    return if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)
}
```

### 6. Verify the Migration

```bash
# Build
mvn clean compile test-compile -DskipTests -pl !fm-ui -nsu
```

You do not need to run tests, since UI is not covered. User will do that manually.

## Quick Checklist

### Before Starting

- [ ] Check for field name differences between old DTO and entity (e.g., `logoId` vs `logoImageId`)
- [ ] Check for `ReferenceSetProperty` that need `List<EnumeratedDto>` conversion
- [ ] Check if adapter has `asEnumerated()` method used elsewhere

### Create New Files

- [ ] Create `ObjXxxApiRepositoryImpl.kt` extending `GenericAggregateApiRepositoryBase`
- [ ] Create `ObjXxxDtoAdapter.kt` extending `GenericObjDtoAdapterBase`
  - [ ] Configure relationships with `relationship()` for fields needing name mapping
  - [ ] Configure field mappings with `field()` for ReferenceSet → EnumeratedDto conversion
  - [ ] Keep `asEnumerated()` method if used by other components
- [ ] Create `ObjXxxDto.kt` extending `GenericObjDtoBase`
  - [ ] Only declare `@JsonApiRelationId` fields explicitly
  - [ ] Use `String?` type for all relation IDs

### Delete Old Files

- [ ] Delete `ObjXxxApiRepositoryImpl.java`
- [ ] Delete `ObjXxxDtoAdapter.java`
- [ ] Delete `ObjXxxDto.java`
- [ ] Delete `ObjXxxApiRepository.java` (interface)
- [ ] Delete any Part DTO files (e.g., `ObjXxxPartYyyDto.java`)

### Fix Downstream Consumers

- [ ] Search: `grep -r "ObjXxxDto" --include="*.kt" --include="*.java"`
- [ ] Search: `grep -r "xxxDtoAdapter" --include="*.kt" --include="*.java"`
- [ ] Fix nullable `fromAggregate()` calls with `?.let { }`
- [ ] Fix `getId()` calls expecting Integer with `DtoUtils.idFromString()`

### Verify

- [ ] Build: `mvn compile test-compile -DskipTests -pl !fm-ui -nsu`

## Common Issues

### "argument type mismatch" on relation ID

**Cause:** Relation ID field declared as `Any?` or `Int?` instead of `String?`

**Solution:** Change the field type to `String?`:

```kotlin
@JsonApiRelationId
var accountId: String? = null
    get() = getRelation("accountId") as String?
    set(value) {
        setRelation("accountId", value)
        field = value
    }
```

### Compilation error in consuming code

**Cause:** `getId()` now returns `String` instead of `Integer` or `Object`

**Solution:** Add `DtoUtils.idFromString()` where needed:

```java
DtoUtils.idFromString(dto.getId())
```

### Field name mismatch between DTO and entity

**Cause:** The old Java DTO had a field like `logoId` but the entity property is `logoImageId`

**Solution:** Use relationship mapping with explicit source property:

```kotlin
init {
    // Maps DTO "logoId" to entity "logoImage" (which has logoImageId)
    relationship("logoId", "document", "logoImage")
}
```

Common mappings to watch for:
- `logoId` → `logoImage` (entity stores as `logoImageId`)
- `avatarId` → `avatarImage` (entity stores as `avatarImageId`)
- `coverFotoId` → `coverFoto` (entity stores as `coverFotoId`)

### ReferenceSetProperty needs List<EnumeratedDto>

**Cause:** Entity has `tenantSet: ReferenceSetProperty<ObjTenant>` but DTO needs `tenants: List<EnumeratedDto>`

**Solution:** Use field mapping with intelligent type detection:

```kotlin
init {
    exclude("tenantSet")  // Exclude from automatic handling
    field("tenants", "tenantSet")  // Maps with auto-conversion to List<EnumeratedDto>
}
```

The adapter automatically detects `ReferenceSetProperty` and converts to `List<EnumeratedDto>` by loading
each referenced entity and calling `EnumeratedDto.of(entity)`.

## Framework Files Reference

These are the key framework files involved in the generic adapter pattern:

| File | Purpose |
|------|---------|
| `GenericAggregateDtoAdapterBase.kt` | Base adapter with relationship/field configuration |
| `GenericObjDtoAdapterBase.kt` | Obj-specific adapter (extends above, adds meta/owner) |
| `GenericAggregateApiRepositoryBase.kt` | Base API repository |
| `GenericObjDtoBase.kt` | Base DTO class |
| `ReferenceSetProperty.kt` | Interface for reference sets (has `targetClass`) |
| `AggregateReferenceProperty.kt` | Interface for single references (has `targetClass`) |
| `EnumeratedDto.kt` | DTO for id+name pairs (has `of(Aggregate?)` method) |
