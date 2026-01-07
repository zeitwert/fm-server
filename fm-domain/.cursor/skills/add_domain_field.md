# Skill: Add Domain Field

This document describes how to add a new field to a domain object in this ERP application.

## Overview

Adding a new field requires changes across multiple layers:
1. Database schema (migration)
2. Domain interface
3. Domain implementation
4. Persistence mapper
5. Tests

## Step-by-Step Process

### 1. Database Migration

Create a versioned migration script in `fm-domain/src/main/resources/db/V1.0/2-upgrade/V1.0.1/`:

```sql
-- V1.0.1.XXXX__add_fieldname_to_tablename.sql

alter table obj_tablename
add column field_name varchar(40);  -- adjust type as needed

-- Optional: Add index if needed for lookups
create [unique] index obj_tablename_field_name_idx ...
```

**Key considerations:**
- Use appropriate data type (varchar, integer, decimal, text, etc.)
- For nullable unique indexes, use `where field_name is not null` to allow multiple nulls
- Views using `t.*` or `a.*` will automatically include the new column

### 2. Domain Interface

Add the property to the domain interface (e.g., `ObjAccount.kt`):
Typically all domain fields are nullable.

```kotlin
interface ObjAccount : Obj {
    // ... existing properties ...
    
    var newField: String?  // nullable if optional
}
```

### 3. Domain Implementation

Add the property implementation using the corresponding property delegate (e.g., `ObjAccountImpl.kt`):

```kotlin
class ObjAccountImpl(...) : FMObjBase(...), ObjAccount {
    // ... existing properties ...
    
    override var newField by baseProperty<String>("newField")
}
```

**Property delegate types:**
- `baseProperty<T>` - for simple types (String, Int, BigDecimal, etc.)
- `enumProperty<E>` - for enum types extending `Enumerated`
- `referenceIdProperty<T>` - for foreign key IDs
- `referenceProperty<T>` - for lazy-loaded references

### 4. Persistence Mapper

Update the SQL persistence provider (e.g., `ObjAccountSqlPersistenceProviderImpl.kt`):

**In `mapFromRecord()`:**
```kotlin
private fun mapFromRecord(aggregate: ObjAccount, record: ObjAccountRecord) {
    // ... existing mappings ...
    aggregate.newField = record.newField
}
```

**In `mapToRecord()`:**
```kotlin
private fun mapToRecord(aggregate: ObjAccount): ObjAccountRecord {
    val record = dslContext.newRecord(Tables.OBJ_ACCOUNT)
    // ... existing mappings ...
    record.newField = aggregate.newField
    return record
}
```

### 6. Tests

Embed / extend tests to verify the new field:

```kotlin
@Test
fun testNewFieldPersistence() {
    val obj = repository.create()
    obj.newField = "test-value"
    repository.store(obj)
    
    val loaded = repository.load(obj.id)
    assertEquals("test-value", loaded.newField)
}

## Build and Verify

1. **Apply migration:**
   ```bash
   cd fm-domain
   mvn flyway:migrate -nsu "-Dflyway.ignoreMigrationPatterns=*:missing"
   ```

2. **Regenerate jOOQ classes:**
   ```bash
   mvn generate-sources -pl fm-domain -nsu "-Dskip.jooq.generation=false"
   ```

3. **Compile:**
   ```bash
   mvn clean compile -DskipTests -pl !fm-ui -nsu
   ```

4. **Run tests:**
   ```bash
   mvn test -Dtest="YourTestClass" -pl fm-domain -nsu
   ```

5. **Run all tests before completing:**
   ```bash
   mvn test -pl !fm-ui -nsu
   ```

## Common Patterns

### Field naming conventions
- Database: `snake_case` (e.g., `field_name`)
- Kotlin/Java: `camelCase` (e.g., `fieldName`)
- jOOQ Table fields: `UPPER_SNAKE_CASE` (e.g., `FIELD_NAME`)

### jOOQ property access
- Most fields: `record.fieldName` (Kotlin property syntax)
- Reserved words (like `key`): May need `record.getKey()` / `record.setKey()` if there's a method conflict
- Table fields: `Tables.OBJ_TABLE.FIELD_NAME`

### Nullable vs Non-nullable
- Use nullable types (`String?`) for optional fields
- Use non-null types with defaults for required fields
- Database constraints should match domain model

## File Locations

| Component | Location Pattern |
|-----------|------------------|
| Migration | `fm-domain/src/main/resources/db/V1.0/2-upgrade/V1.0.1/` |
| Domain Interface | `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/Obj{Entity}.kt` |
| Domain Impl | `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/impl/Obj{Entity}Impl.kt` |
| Repository | `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/Obj{Entity}Repository.kt` |
| Repository Impl | `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/impl/Obj{Entity}RepositoryImpl.kt` |
| Persistence | `fm-domain/src/main/java/io/zeitwert/fm/{module}/persist/Obj{Entity}SqlPersistenceProviderImpl.kt` |
| Tests | `fm-domain/src/test/java/io/zeitwert/fm/{Entity}Test.kt` |
| jOOQ Generated | `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/db/` |

## Checklist

- [ ] Create database migration script
- [ ] Add property to domain interface
- [ ] Add property implementation with delegate
- [ ] Update `mapFromRecord()` in persistence provider
- [ ] Update `mapToRecord()` in persistence provider
- [ ] Add lookup method if needed (persistence provider + repository)
- [ ] Apply migration to database
- [ ] Regenerate jOOQ classes
- [ ] Create/extend tests
- [ ] Run all tests to verify no regressions

