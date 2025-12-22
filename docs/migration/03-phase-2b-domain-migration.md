# Phase 2b: Domain-by-Domain Migration

**Status:** In Progress  
**Dependencies:** [Phase 2a - DDDrive Setup](02-phase-2a-dddrive-setup.md)  
**Next Phase:** [Phase 2c - Cleanup](04-phase-2c-cleanup.md)

---

## Migration Recipe

### Per-Domain Steps

For each domain, migrate as a vertical slice:

1. **Domain Config** → create `{Domain}Config.kt` to register code tables (see below)
2. **Enums** → Kotlin `enum class` implementing `Enumerated` with companion `Enumeration` (see below)
3. **Interface** → extend `io.dddrive.obj.model.Obj` (or `.doc.model.Doc`)
4. **Repository Interface** → extend `io.dddrive.obj.model.ObjRepository` (or `DocRepository`)
5. **Base Class** → Kotlin, extend `FMObjCoreBase` (or `FMDocCoreBase`)
6. **Repository Impl** → Kotlin, extend `FMObjCoreRepositoryBase` (or `FMDocCoreRepositoryBase`)
7. **Persistence Provider** → create in `persist/jooq/`
8. **Delete** cache classes (`Obj*Cache.java`, `Obj*CacheImpl.java`) — use `repository.get()` instead
9. **Delete** old enum files (`Code*.java` + `Code*Enum.kt` pairs) — replaced by single Kotlin enum class

### Domain Config Classes

Each domain needs a config class that:
- Registers **CodeAggregateType** for the domain's aggregate(s)
- Triggers initialization of **domain-specific enums** by accessing their `.entries`

The NEW framework (`io.dddrive.*`) does not load code tables from the database — values are hardcoded in Kotlin enum classes. The DML scripts remain for database reference data (intentional duplication).

**Pattern:** Implement `InitializingBean` and register items in `afterPropertiesSet()`:

```kotlin
package io.zeitwert.fm.{domain}.config

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.dddrive.enums.model.base.EnumConfigBase
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component("{domain}Config")
class {Domain}Config : EnumConfigBase(), InitializingBean {

    @Autowired
    @Qualifier("coreCodeAggregateTypeEnum")
    lateinit var aggregateTypeEnum: CodeAggregateTypeEnum

    override fun afterPropertiesSet() {
        try {
            startConfig()
            // Register aggregate type
            aggregateTypeEnum.addItem(CodeAggregateType(aggregateTypeEnum, "obj_{domain}", "{Domain}"))
            
            // Initialize domain-specific enums (triggers companion object initialization)
            Code{X}.entries
            Code{Y}.entries
        } finally {
            endConfig()
        }
    }
}
```

**Example:** See `fm-domain/src/test/java/io/zeitwert/fm/test/config/TestConfig.kt` for a working example.

### Code Table (Enum) Pattern

Each code table is a Kotlin `enum class` that:
- Implements `Enumerated`
- Has hardcoded enum values (derived from Flyway migration scripts in `3-config/`)
- Has a companion object `Enumeration` extending `EnumerationBase` that registers entries
- Is initialized by accessing its `.entries` in a corresponding config class `{Domain}Config.kt`

**Example:**
See `fm-domain/src/main/java/io/zeitwert/fm/oe/model/enums/CodeUserRole.kt`
See `fm-domain/src/main/java/io/zeitwert/fm/oe/config/OEConfig.kt`

**Usage:**
- Direct access (always use this if not dynamically accessed): `Code{X}.ITEM_A`
- Lookup by ID: `Code{X}.get{X}("item_a")`

### API Changes

| Old | New |
|-----|-----|
| `Code{X}Enum.get{X}("id")` | `Code{X}.Enumeration.getItem("id")` or `Code{X}.{ENTRY}` |
| `getValue()` / `setValue()` | `.value` (Kotlin property) |
| `contains()`, `valueSet`, `add()`, `remove()` | `hasItem()`, `getItems()`, `addItem()`, `removeItem()` |
| `size()`, `value`, `getBySeqNr()`, `add()` | `getPartCount()`, `getParts()`, `getPart()`, `addPart(null)` |
| `create(tenantId)` | `create(tenantId, userId, timestamp)` |
| `store(aggregate)` | `store(aggregate, userId, timestamp)` |
| `delete(obj)` | `delete(obj, userId, timestamp)` |
| `cache.get(id)` | `repository.get(id)` (frozen) |
| `repository.load(id)` | `repository.load(id)` (writeable) |
| `getId()` returns `Integer` | `getId()` returns `Object` — cast when needed |
| `getByForeignKey()` returns view records | Returns aggregates |

### Import Mapping

| Old | New |
|-----|-----|
| `io.dddrive.obj.model.Obj` | `io.dddrive.obj.model.Obj` |
| `io.dddrive.obj.model.ObjRepository` | `io.dddrive.obj.model.ObjRepository` |
| `io.dddrive.obj.model.base.ObjExtnBase` | `io.dddrive.obj.model.base.ObjBase` |
| `io.dddrive.obj.model.ObjPart` | `io.dddrive.obj.model.ObjPart` |
| `io.dddrive.obj.model.base.ObjPartBase` | `io.dddrive.obj.model.base.ObjPartBase` |
| `io.dddrive.obj.model.ObjPartRepository` | `io.dddrive.ddd.model.PartRepository` |
| `io.dddrive.doc.model.Doc` | `io.dddrive.doc.model.Doc` |
| `io.dddrive.doc.model.DocRepository` | `io.dddrive.doc.model.DocRepository` |
| `io.dddrive.doc.model.base.DocExtnBase` | `io.dddrive.doc.model.base.DocBase` |
| `io.dddrive.enums.model.base.EnumeratedBase` | Kotlin enum implementing `io.dddrive.enums.model.Enumerated` |
| `io.dddrive.jooq.enums.JooqEnumerationBase` | Companion object extending `io.dddrive.enums.model.base.EnumerationBase` |
| `io.dddrive.property.model.*` | `io.dddrive.property.model.*` |
| `io.dddrive.ddd.model.*` | `io.dddrive.ddd.model.*` |
| `io.zeitwert.fm.obj.model.base.FMObjBase` | `io.zeitwert.fm.obj.model.base.FMObjCoreBase` |
| `io.zeitwert.fm.obj.model.base.FMObjRepositoryBase` | `io.zeitwert.fm.obj.model.base.FMObjCoreRepositoryBase` |
| `io.zeitwert.fm.doc.model.base.FMDocBase` | `io.zeitwert.fm.doc.model.base.FMDocCoreBase` |
| `io.zeitwert.fm.doc.model.base.FMDocRepositoryBase` | `io.zeitwert.fm.doc.model.base.FMDocCoreRepositoryBase` |

### Cross-Domain References

Comment out with marker, uncomment after target domain migrated:
```java
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// private final ReferenceProperty<ObjDocument> logoImage = ...
```

Find remaining: `grep -r "TODO-MIGRATION:" fm-domain/src/`

### What Gets Commented Out

- **Adapter layer** (`adapter/api/jsonapi/`) — restored in Phase 3
- **Cross-domain mixins** (`AggregateWithNotesMixin`, etc.) — until all consumers migrated
- **Shared interfaces** (`ItemWithNotes`, etc.) — make empty temporarily
- **Base class cross-domain methods** (`noteRepository()`, etc.)

---

## Domain To-Do List

| # | Domain | Package | Type | Status | Dependencies | Notes |
|---|--------|---------|------|--------|--------------|-------|
| 1 | Collaboration | `collaboration` | Obj | ✅ Done | — | |
| — | Test | `test.model` | Obj+Doc | ✅ Done | — | Test classes only |
| 2 | Contact | `contact` | Obj | ⬜ | — | ~12 enums, 1 part |
| 3 | Task | `task` | Doc | ⬜ | — | Uses `FMDocCoreRepositoryBase` |
| 4 | Portfolio | `portfolio` | Obj | ⬜ | — | |
| 5 | DMS | `dms` | Obj | ✅ Done | Self-ref | `templateDocument` → DMS |
| 6 | OE | `oe` | Obj | ✅ Done | DMS | `avatarImage`, `logoImage` restored |
| 7 | Account | `account` | Obj | ✅ Done | DMS, Contact | `logoImage`, `mainContact` restored |
| 8 | Building | `building` | Obj | ⬜ | All | Most refs, migrate last |

### Completion Criteria

- All domains migrated
- No `TODO-MIGRATION:` markers remain
- All tests pass
- No `io.dddrive.*` (non-core) imports in domain code
