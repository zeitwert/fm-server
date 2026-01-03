# JSON API Migration Cookbook

This cookbook describes how to migrate a JSON API repository from the legacy Java-based pattern to the new generic
Kotlin pattern.

## Reference Implementation

The reference implementation is `ObjAccount`:

- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/impl/ObjAccountApiRepositoryImpl.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/impl/ObjAccountDtoAdapter.kt`
- `fm-domain/src/main/java/io/zeitwert/fm/account/adapter/api/jsonapi/dto/ObjAccountDto.kt`

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
- Use `init {}` block to configure custom relationships if needed

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

Search for usages of the DTO in other files. Common issues:

**dto.getId() returns String now:**
If other code expects `getId()` to return `Integer`, you need to update it:

```java
// Old code (will fail):
contacts.stream().map(ct -> ct.getId()).collect(...)

// New code:
contacts.stream().map(ct -> DtoUtils.idFromString(ct.getId())).collect(...)
```

### 6. Verify the Migration

```bash
# Build
mvn clean compile test-compile -DskipTests -pl !fm-ui -nsu
```

You do not need to run tests, since UI is not covered. User will do that manually.

## Quick Checklist

- [ ] Create `ObjXxxApiRepositoryImpl.kt` extending `GenericAggregateApiRepositoryBase`
- [ ] Create `ObjXxxDtoAdapter.kt` extending `GenericObjDtoAdapterBase`
- [ ] Create `ObjXxxDto.kt` extending `GenericObjDtoBase`
- [ ] Ensure relation ID fields use `String?` type
- [ ] Delete `ObjXxxApiRepositoryImpl.java`
- [ ] Delete `ObjXxxDtoAdapter.java`
- [ ] Delete `ObjXxxDto.java`
- [ ] Delete `ObjXxxApiRepository.java` (interface)
- [ ] Delete any Part DTO files (e.g., `ObjXxxPartYyyDto.java`)
- [ ] Search for and fix downstream consumers
- [ ] Build and test

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
