# Phase 5: Code Modernization (Optional)

**Status:** Pending  
**Dependencies:** [Phase 4 - Update UI](06-phase-4-update-ui.md)  
**Next Phase:** None (final phase)

## Objective

Gradual migration from Java to Kotlin and modernization of code patterns.

## Scope

This phase is **optional** and can be done incrementally over time. The approach is to convert files to Kotlin when they need to be modified for other reasons.

## Migration Strategy

### When to Convert

Convert Java files to Kotlin when:
- Making significant changes to the file
- Adding new features to a class
- Fixing bugs that require refactoring
- The file is small and isolated

### When NOT to Convert

Keep Java files as-is when:
- Making minor bug fixes
- The file is complex and working fine
- The file has many dependencies that would need updating
- Time pressure doesn't allow for careful conversion

## Tasks

### 1. Configure Kotlin in Modules

Each module needs Kotlin support:

```xml
<!-- In module pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

- [ ] Add Kotlin to `fm-common`
- [ ] Add Kotlin to `fm-domain`
- [ ] Add Kotlin to `fm-server`
- [ ] Add Kotlin to `fm-jooq-adapter`

### 2. Lombok to Kotlin Data Classes

**Before (Java + Lombok):**
```java
@Data
@Builder
public class ObjBuildingDto {
    private String id;
    private String name;
    private String description;
    private Integer buildingYear;
}
```

**After (Kotlin):**
```kotlin
data class ObjBuildingDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val buildingYear: Int? = null
)
```

### 3. Domain Model Conversion

**Before (Java):**
```java
public abstract class ObjBuildingBase extends FMObjBase
        implements ObjBuilding, AggregateWithNotesMixin {

    protected final SimpleProperty<String> name = 
        this.addSimpleProperty("name", String.class);
    
    @Override
    public String getName() {
        return this.name.getValue();
    }
    
    @Override
    public void setName(String value) {
        this.name.setValue(value);
    }
}
```

**After (Kotlin):**
```kotlin
abstract class ObjBuildingBase(
    repository: ObjBuildingRepository
) : FMObjBase(repository), ObjBuilding, AggregateWithNotesMixin {

    private val _name = addBaseProperty(ObjBuilding::name.name, String::class.java)
    
    override var name: String?
        get() = _name.value
        set(value) { _name.value = value }
}
```

### 4. Service Layer Conversion

**Before (Java):**
```java
@Service
public class BuildingServiceImpl implements BuildingService {
    
    @Autowired
    private ObjBuildingRepository repository;
    
    @Override
    public ObjBuilding findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Building not found"));
    }
}
```

**After (Kotlin):**
```kotlin
@Service
class BuildingServiceImpl(
    private val repository: ObjBuildingRepository
) : BuildingService {
    
    override fun findById(id: String): ObjBuilding =
        repository.findById(id) 
            ?: throw NotFoundException("Building not found")
}
```

### 5. Controller Conversion

Controllers are already being created in Kotlin (Phase 3), so this is mostly done.

### 6. Test Conversion

**Before (Java + JUnit):**
```java
@Test
public void testCreateBuilding() {
    ObjBuilding building = repository.create(tenantId);
    building.setName("Test Building");
    repository.store(building);
    
    assertNotNull(building.getId());
    assertEquals("Test Building", building.getName());
}
```

**After (Kotlin):**
```kotlin
@Test
fun `create building`() {
    val building = repository.create(tenantId).apply {
        name = "Test Building"
    }
    repository.store(building)
    
    assertNotNull(building.id)
    assertEquals("Test Building", building.name)
}
```

## Conversion Checklist per File

For each file being converted:

- [ ] Create `.kt` file with same name
- [ ] Convert class structure
- [ ] Convert properties/fields
- [ ] Convert methods
- [ ] Update null handling (use `?` types)
- [ ] Replace getters/setters with properties
- [ ] Use Kotlin idioms (apply, let, etc.)
- [ ] Update tests if needed
- [ ] Delete old `.java` file
- [ ] Verify compilation
- [ ] Run related tests

## Priority Files for Conversion

### High Priority (New/Frequently Changed)
- REST Controllers (already Kotlin from Phase 3)
- New DTO classes
- New services

### Medium Priority
- Existing services with active development
- Repository implementations
- Configuration classes

### Low Priority
- Stable domain models
- Generated code (jOOQ)
- Rarely changed utilities

## Files to Reference

| File | Purpose |
|------|---------|
| [ObjHouseholdBase.kt](../dfp-app-server/dfp-finplan/src/main/java/ch/dfp/finplan/household/model/base/ObjHouseholdBase.kt) | Kotlin domain model pattern |
| [HouseholdController.kt](../dfp-app-server/dfp-finplan/src/main/java/ch/dfp/finplan/household/api/rest/HouseholdController.kt) | Kotlin controller pattern |
| [ObjBuildingBase.java](src/main/java/io/zeitwert/fm/building/model/base/ObjBuildingBase.java) | Current Java model to convert |

## IDE Support

IntelliJ IDEA can auto-convert Java to Kotlin:
1. Open Java file
2. Code → Convert Java File to Kotlin File
3. Review and fix any issues
4. Update imports
5. Run tests

**Note:** Auto-conversion is a starting point; manual refinement is usually needed.

## Kotlin Style Guidelines

Follow these patterns from dfp-app-server:

1. **Use data classes** for DTOs
2. **Use constructor injection** instead of `@Autowired` fields
3. **Use expression bodies** for simple functions
4. **Use `?.let { }` and `?: `** for null handling
5. **Use `apply { }` and `also { }`** for initialization
6. **Avoid `!!`** (non-null assertion) - use proper null handling

## Completion Criteria

This phase is ongoing and doesn't have a fixed completion point. Consider it complete when:

- [ ] All new code is written in Kotlin
- [ ] Critical/frequently-changed files are converted
- [ ] Team is comfortable with Kotlin
- [ ] No significant Java-Kotlin interop issues

