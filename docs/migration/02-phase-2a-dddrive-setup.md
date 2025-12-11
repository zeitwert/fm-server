# Phase 2a: DDDrive Setup

**Status:** Pending  
**Dependencies:** [Phase 1 - Modular Build](01-phase-1-modular-build.md)  
**Next Phase:** [Phase 2b - Domain Migration](03-phase-2b-domain-migration.md)

## Objective

Embed the new DDDrive framework and create PostgreSQL/jOOQ persistence adapters. Both old and new dddrive will coexist temporarily.

## Key Architectural Difference

The new dddrive has **pluggable persistence** via providers (not baked into base classes). We need to create PostgreSQL/jOOQ providers.

| Aspect | Old dddrive | New dddrive |
|--------|-------------|-------------|
| Package | `io.dddrive.*` | `io.dddrive.core.*` |
| Persistence | Built into base classes | Pluggable via `AggregatePersistenceProvider` |
| Source | External dependency | Embedded module |

## Package Coexistence

Since packages differ, both can coexist on classpath during migration:

```
Old: io.dddrive.property.model.Property
New: io.dddrive.core.property.model.Property
```

This enables gradual, domain-by-domain migration.

## Tasks

### 1. Copy dfp-dddrive to fm-dddrive

- [ ] Copy `dfp-app-server/dfp-dddrive/src` to `fm-server/fm-dddrive/src`
- [ ] Update `fm-dddrive/pom.xml`:
  - Set parent to fm-server parent
  - Update artifact coordinates to `io.zeitwert:fm-dddrive`
  - Add required dependencies (Kotlin, Jackson, etc.)
- [ ] Verify `fm-dddrive` compiles

**Key source directories:**
```
fm-dddrive/src/main/java/io/dddrive/core/
├── config/
├── ddd/model/          # Aggregate, Part, Repository bases
├── doc/model/          # Document patterns
├── enums/model/        # Enumeration support
├── obj/model/          # Object patterns
├── oe/model/           # Tenant/User patterns
├── property/model/     # Property system
└── validation/model/   # Validation framework
```

### 3. Implement AggregatePersistenceProvider for jOOQ

Create jOOQ-based persistence provider implementing the interface from new dddrive.

**Interface to implement:**
```kotlin
// From io.dddrive.core.ddd.model
interface AggregatePersistenceProvider<A : Aggregate> {
    fun findById(id: String): A?
    fun findAll(): List<A>
    fun save(aggregate: A): A
    fun delete(id: String)
}
```

**Tasks:**
- [ ] Create `JooqPersistenceProvider<A>` base class
- [ ] Implement CRUD operations using jOOQ DSL
- [ ] Handle entity-to-record mapping
- [ ] Handle record-to-entity mapping
- [ ] Support optimistic locking (version field)

**Reference:** Look at MongoDB provider in dfp-app-server for patterns:
- `dfp-finplan/src/main/java/ch/dfp/finplan/household/persist/mongodb/`

### 4. Create Base Classes for jOOQ Entities

- [ ] Create `JooqObjBase` extending new `ObjBase`
- [ ] Create `JooqDocBase` extending new `DocBase`
- [ ] Wire up jOOQ-specific persistence logic
- [ ] Handle property serialization to/from jOOQ records

### 5. Update Parent POM

- [ ] Add `fm-dddrive` to modules list
- [ ] Keep old `io.zeitwert:dddrive` dependency (temporary)
- [ ] Add new internal `fm-dddrive` dependency

### 6. Verify Setup

- [ ] All modules compile
- [ ] New dddrive classes accessible via `io.dddrive.core.*`
- [ ] Old dddrive classes still accessible via `io.dddrive.*`
- [ ] No runtime conflicts between old and new

## Files to Reference

| File | Purpose |
|------|---------|
| [dfp-dddrive/](../dfp-app-server/dfp-dddrive/) | New DDDrive source to copy |
| [dfp-dddrive/CLAUDE.md](../dfp-app-server/dfp-dddrive/CLAUDE.md) | DDDrive architecture docs |
| [AggregatePersistenceProvider.kt](../dfp-app-server/dfp-dddrive/src/main/java/io/dddrive/core/ddd/model/AggregatePersistenceProvider.kt) | Persistence interface |
| [dfp-finplan/persist/mongodb/](../dfp-app-server/dfp-finplan/src/main/java/ch/dfp/finplan/household/persist/mongodb/) | MongoDB provider example |

## fm-domain Persistence Structure

```
fm-domain/
├── pom.xml
└── src/main/java/io/zeitwert/fm/persist/jooq/
    ├── JooqPersistenceProvider.kt      # Base provider implementation
    ├── JooqObjPersistenceProvider.kt   # Obj-specific provider
    ├── JooqDocPersistenceProvider.kt   # Doc-specific provider
    └── config/
        └── JooqAdapterConfig.kt        # Spring configuration
```

## Completion Criteria

- [ ] `fm-dddrive` module compiles with new dddrive code
- [ ] Both old (`io.dddrive.*`) and new (`io.dddrive.core.*`) packages accessible
- [ ] Basic persistence provider tests pass
- [ ] No changes to existing domain code yet

