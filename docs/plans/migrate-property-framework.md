# DDDrive Property Framework - Migration Cookbook

This document describes the property framework used in the DDDrive application development library and provides a complete cookbook for migrating entities from the old reflection-based model to the new Kotlin delegation-based model.

## Overview

The property framework provides a way to define typed properties on domain entities that:
- Support getter/setter access with type safety
- Track changes for audit logging
- Enable path-based access for dynamic property resolution
- Integrate with persistence providers for loading/storing

There are **two models** for defining properties:

| Aspect | Old Model (Reflection) | New Model (Delegation) |
|--------|----------------------|----------------------|
| Property registration | `doInit()` method | Kotlin property delegates |
| Interface satisfaction | Javassist proxy + `PropertyHandler` | Direct Kotlin `override` |
| Type safety | Runtime (reflection) | Compile-time |
| Boilerplate | Low (but hidden complexity) | Explicit (but clear) |
| Debugging | Harder (proxy interception) | Easier (standard Kotlin) |

---

## Quick Reference: Property Mapping

| Old Pattern | New Pattern |
|-------------|-------------|
| `addBaseProperty("name", T::class.java)` | `override var name: T? by baseProperty()` |
| `addEnumProperty("type", E::class.java)` | `override var type: E? by enumProperty()` |
| `addReferenceProperty("ref", A::class.java)` | `override var ref: A? by referenceProperty()` + `override var refId: Any? by referenceIdProperty<A>()` |
| `addEnumSetProperty("set", E::class.java)` | `override val set: EnumSetProperty<E> by enumSetProperty()` |
| `addPartListProperty("list", P::class.java)` | `override val list: PartListProperty<P> by partListProperty()` |

**Required imports for delegation:**
```kotlin
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.enumSetProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
```

---

## Migration Cookbook

### Prerequisites

Before migrating, ensure you understand:
1. The entity you're migrating (Aggregate or Part)
2. All properties defined in its `doInit()` method
3. The interface it implements
4. Any consuming code (tests, persistence providers)

### Step-by-Step Migration Process

#### Step 1: Update the Interface

**For collection properties (EnumSet, ReferenceSet, PartList)**, the interface must expose the property object type directly instead of helper methods:

```kotlin
// OLD Interface
interface ObjTest : Obj {
    // Helper methods for EnumSet
    fun hasTestType(testType: CodeTestType): Boolean
    val testTypeSet: Set<CodeTestType>
    fun clearTestTypeSet()
    fun addTestType(testType: CodeTestType)
    fun removeTestType(testType: CodeTestType)
    
    // Helper methods for PartList
    val nodeCount: Int
    fun getNode(seqNr: Int): ObjTestPartNode
    val nodeList: List<ObjTestPartNode>
    fun getNodeById(nodeId: Int): ObjTestPartNode
    fun clearNodeList()
    fun addNode(): ObjTestPartNode
    fun removeNode(nodeId: Int)
}

// NEW Interface - expose the property objects directly
interface ObjTest : Obj {
    // EnumSet - just the property
    val testTypeSet: EnumSetProperty<CodeTestType>
    
    // PartList - just the property
    val nodeList: PartListProperty<ObjTestPartNode>
}
```

**For reference ID properties**, ensure the type is `Any?`:
```kotlin
// Interface should declare:
var refObjId: Any?   // NOT Int? - use Any? for flexibility
val refObj: ObjTest?
```

#### Step 2: Create the New Implementation Class

1. **Naming Convention**: Change `*Base` to `*Impl` and move from `model/base/` to `model/impl/`
2. **Class Modifier**: Use `open class` (NOT just `class`) - Javassist proxies require non-final classes!
3. **Remove `abstract`**: The class should be instantiable

```kotlin
// File: model/impl/ObjTestImpl.kt
package io.zeitwert.fm.test.model.impl

import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.enumSetProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.model.EnumSetProperty
import io.dddrive.property.model.PartListProperty
// ... other imports

open class ObjTestImpl(  // MUST be 'open' for Javassist proxy!
    repository: ObjTestRepository,
    isNew: Boolean,
) : FMObjBase(repository, isNew),
    ObjTest,
    AggregateWithNotesMixin {

    // Simple base properties
    override var shortText: String? by baseProperty()
    override var longText: String? by baseProperty()
    override var date: LocalDate? by baseProperty()
    override var int: Int? by baseProperty()
    override var isDone: Boolean? by baseProperty()
    override var nr: BigDecimal? by baseProperty()

    // JSON property (special handling for jOOQ JSON type)
    private var _json: JSON? by baseProperty()
    override var json: String?
        get() = _json?.data()
        set(value) {
            _json = if (value != null) JSON.json(value) else null
        }

    // Enum property
    override var testType: CodeTestType? by enumProperty()

    // Reference properties (BOTH id and value)
    override var refObjId: Any? by referenceIdProperty<ObjTest>()
    override var refObj: ObjTest? by referenceProperty()

    // Enum set property
    override val testTypeSet: EnumSetProperty<CodeTestType> by enumSetProperty()

    // Part list property
    override val nodeList: PartListProperty<ObjTestPartNode> by partListProperty()

    // ... rest of the class
}
```

#### Step 3: Create Part Implementation (if applicable)

Same pattern for Parts:

```kotlin
// File: model/impl/ObjTestPartNodeImpl.kt
open class ObjTestPartNodeImpl(  // MUST be 'open'!
    obj: ObjTest,
    repository: PartRepository<ObjTest, ObjTestPartNode>,
    property: Property<*>,
    id: Int,
) : ObjPartBase<ObjTest>(obj, repository, property, id),
    ObjTestPartNode {

    override var shortText: String? by baseProperty()
    override var longText: String? by baseProperty()
    // ... other properties with delegation
    
    override var testType: CodeTestType? by enumProperty()
    
    override var refObjId: Any? by referenceIdProperty<ObjTest>()
    override var refObj: ObjTest? by referenceProperty()
}
```

#### Step 4: Update the Repository

Change the class references from `*Base` to `*Impl`:

```kotlin
// File: model/impl/ObjTestRepositoryImpl.kt
@Component("objTestRepository")
class ObjTestRepositoryImpl :
    FMObjRepositoryBase<ObjTest>(
        ObjTestRepository::class.java,
        ObjTest::class.java,
        ObjTestImpl::class.java,  // Changed from ObjTestBase
        AGGREGATE_TYPE_ID,
    ),
    ObjTestRepository {

    override fun registerParts() {
        this.addPart(
            ObjTest::class.java, 
            ObjTestPartNode::class.java, 
            ObjTestPartNodeImpl::class.java  // Changed from ObjTestPartNodeBase
        )
    }
}
```

#### Step 5: Update Consuming Code

**Tests and other code using the old helper methods must be updated:**

```kotlin
// OLD - helper methods
testA1.hasTestType(CodeTestType.TYPE_A)
testA1.addTestType(CodeTestType.TYPE_A)
testA1.removeTestType(CodeTestType.TYPE_A)
testA1.addNode().apply { shortText = "A" }
testA1.removeNode(nodeB1.id)
testA1.nodeCount

// NEW - use property object directly
testA1.testTypeSet.contains(CodeTestType.TYPE_A)
testA1.testTypeSet.add(CodeTestType.TYPE_A)
testA1.testTypeSet.remove(CodeTestType.TYPE_A)
testA1.nodeList.add(null).apply { shortText = "A" }  // null = auto-generate ID
testA1.nodeList.remove(nodeB1.id)
testA1.nodeList.size
```

**For Java code, be explicit with null casts to avoid ambiguous method references:**
```java
// Java - cast null to avoid ambiguity between add(E) and add(Integer)
ObjTestPartNode node = testA1.getNodeList().add((Integer) null);
```

**Persistence providers:**
```kotlin
// OLD
aggregate.addTestType(CodeTestType.getTestType(it)!!)

// NEW
aggregate.testTypeSet.add(CodeTestType.getTestType(it)!!)
```

#### Step 6: Delete Old Base Classes

After migration is complete and tests pass, delete:
- `model/base/ObjTestBase.kt`
- `model/base/ObjTestPartNodeBase.kt`
- etc.

#### Step 7: Verify

Run the build to ensure everything compiles and tests pass:
```bash
mvn clean compile test-compile -DskipTests -pl !fm-ui -nsu
mvn test -Dtest=YourTestClass -pl your-module -nsu -am -Dsurefire.failIfNoSpecifiedTests=false
```

---

## Common Gotchas

### 1. Class Must Be `open`

**Problem**: `java.lang.RuntimeException: io.example.ObjTestImpl is final`

**Solution**: In Kotlin, classes are `final` by default. Add `open`:
```kotlin
open class ObjTestImpl(...)  // NOT: class ObjTestImpl(...)
```

### 2. Missing Delegate Import

**Problem**: `Unresolved reference 'baseProperty'`

**Solution**: Add the import:
```kotlin
import io.dddrive.property.delegate.baseProperty
```

### 3. Reference ID Type Mismatch

**Problem**: `Assignment type mismatch: actual type is 'Any?', but 'Int!' was expected`

**Solution**: In the interface, declare `refObjId: Any?` (not `Int?`). In persistence code, cast:
```kotlin
record.refObjId = aggregate.refObjId as? Int
```

### 4. Java Ambiguous Method Reference

**Problem**: In Java, `add(null)` is ambiguous between `add(E)` and `add(Integer)`

**Solution**: Cast null explicitly:
```java
testA1.getNodeList().add((Integer) null);
```

### 5. Collection Methods Not Found

**Problem**: `Unresolved reference 'hasTestType'`, `Unresolved reference 'addNode'`

**Solution**: The old helper methods are removed. Use the property object directly:
```kotlin
// OLD: aggregate.hasTestType(type)
// NEW: aggregate.testTypeSet.contains(type)

// OLD: aggregate.addNode()
// NEW: aggregate.nodeList.add(null)
```

---

## Property Types Reference

### Value Properties (ReadWriteProperty)

| Delegate | Factory | Type | Notes |
|----------|---------|------|-------|
| `BasePropertyDelegate<T>` | `baseProperty<T>()` | `T?` | For String, Int, BigDecimal, LocalDate, OffsetDateTime, etc. |
| `EnumPropertyDelegate<E>` | `enumProperty<E>()` | `E?` | For Enumerated types |
| `ReferencePropertyDelegate<A>` | `referenceProperty<A>()` | `A?` | Gets/sets the full aggregate object |
| `ReferenceIdPropertyDelegate<A>` | `referenceIdProperty<A>()` | `Any?` | Gets/sets only the ID |
| `PartReferencePropertyDelegate<P>` | `partReferenceProperty<P>()` | `P?` | Gets/sets a part within the same aggregate |
| `PartReferenceIdPropertyDelegate<P>` | `partReferenceIdProperty<P>()` | `Int?` | Gets/sets only the part ID |

### Collection Properties (ReadOnlyProperty)

| Holder | Factory | Type | Methods Available |
|--------|---------|------|-------------------|
| `EnumSetPropertyHolder<E>` | `enumSetProperty<E>()` | `EnumSetProperty<E>` | `add()`, `remove()`, `contains()`, `clear()`, `size`, iteration |
| `ReferenceSetPropertyHolder<A>` | `referenceSetProperty<A>()` | `ReferenceSetProperty<A>` | `add()`, `remove()`, `contains()`, `clear()`, `size`, iteration |
| `PartListPropertyHolder<P>` | `partListProperty<P>()` | `PartListProperty<P>` | `add(id)`, `remove(id)`, `get(index)`, `getById(id)`, `clear()`, `size`, iteration |

### EnumSetProperty API

```kotlin
val set: EnumSetProperty<CodeTestType> = aggregate.testTypeSet

set.add(CodeTestType.TYPE_A)       // Add element
set.remove(CodeTestType.TYPE_A)    // Remove element
set.contains(CodeTestType.TYPE_A)  // Check membership
set.clear()                        // Remove all
set.size                           // Count
set.forEach { ... }                // Iterate
set.map { it.id }                  // Transform
```

### PartListProperty API

```kotlin
val list: PartListProperty<ObjTestPartNode> = aggregate.nodeList

val node = list.add(null)          // Add new part (auto-generate ID)
val node = list.add(123)           // Add with specific ID
list.remove(nodeId)                // Remove by ID
list.get(index)                    // Get by index
list.getById(id)                   // Get by ID
list.clear()                       // Remove all
list.size                          // Count
list.forEach { ... }               // Iterate
list.any { it.shortText == "A" }   // Find
```

---

## Complete Migration Example

### Before Migration (Old Model)

**Interface (`model/ObjTest.kt`):**
```kotlin
interface ObjTest : Obj, ItemWithNotes {
    var shortText: String?
    var testType: CodeTestType?
    var refObjId: Any?
    val refObj: ObjTest?
    
    // Helper methods (to be removed)
    fun hasTestType(testType: CodeTestType): Boolean
    val testTypeSet: Set<CodeTestType>
    fun addTestType(testType: CodeTestType)
    fun removeTestType(testType: CodeTestType)
    val nodeList: List<ObjTestPartNode>
    fun addNode(): ObjTestPartNode
    fun removeNode(nodeId: Int)
}
```

**Base class (`model/base/ObjTestBase.kt`):**
```kotlin
abstract class ObjTestBase(
    repository: ObjTestRepository,
    isNew: Boolean,
) : FMObjBase(repository, isNew), ObjTest {

    private lateinit var _nodeList: PartListProperty<ObjTestPartNode>

    override fun doInit() {
        super.doInit()
        addBaseProperty("shortText", String::class.java)
        addEnumProperty("testType", CodeTestType::class.java)
        addReferenceProperty("refObj", ObjTest::class.java)
        addEnumSetProperty("testTypeSet", CodeTestType::class.java)
        _nodeList = addPartListProperty("nodeList", ObjTestPartNode::class.java)
    }
    // ... helper method implementations
}
```

### After Migration (New Model)

**Interface (`model/ObjTest.kt`):**
```kotlin
interface ObjTest : Obj, ItemWithNotes {
    var shortText: String?
    var testType: CodeTestType?
    var refObjId: Any?
    val refObj: ObjTest?
    
    // Collection properties exposed directly
    val testTypeSet: EnumSetProperty<CodeTestType>
    val nodeList: PartListProperty<ObjTestPartNode>
}
```

**Impl class (`model/impl/ObjTestImpl.kt`):**
```kotlin
open class ObjTestImpl(
    repository: ObjTestRepository,
    isNew: Boolean,
) : FMObjBase(repository, isNew), ObjTest, AggregateWithNotesMixin {

    override var shortText: String? by baseProperty()
    override var testType: CodeTestType? by enumProperty()
    override var refObjId: Any? by referenceIdProperty<ObjTest>()
    override var refObj: ObjTest? by referenceProperty()
    override val testTypeSet: EnumSetProperty<CodeTestType> by enumSetProperty()
    override val nodeList: PartListProperty<ObjTestPartNode> by partListProperty()
    
    // No doInit() override needed for property registration!
    // The delegates handle registration automatically.
}
```

---

## Files Reference

### Core Framework (fm-dddrive/src/main)

| File | Description |
|------|-------------|
| `io/dddrive/property/delegate/PropertyDelegates.kt` | Value property delegates (`baseProperty`, `enumProperty`, etc.) |
| `io/dddrive/property/delegate/PropertyHolders.kt` | Collection property holders (`enumSetProperty`, `partListProperty`) |
| `io/dddrive/property/model/base/EntityWithPropertiesBase.kt` | Base class with property management |
| `io/dddrive/ddd/model/base/AggregateBase.kt` | Aggregate base class |
| `io/dddrive/obj/model/base/ObjBase.kt` | Obj aggregate base (already migrated) |
| `io/dddrive/doc/model/base/DocBase.kt` | Doc aggregate base (already migrated) |
| `io/dddrive/ddd/model/base/PartBase.kt` | Part base class |

### FM Domain Examples (fm-domain/src/test)

| File | Description |
|------|-------------|
| `io/zeitwert/fm/test/model/ObjTest.kt` | Test aggregate interface |
| `io/zeitwert/fm/test/model/impl/ObjTestImpl.kt` | Migrated test aggregate |
| `io/zeitwert/fm/test/model/impl/ObjTestPartNodeImpl.kt` | Migrated test part |
| `io/zeitwert/fm/test/model/impl/DocTestImpl.kt` | Migrated doc test aggregate |
| `io/zeitwert/fm/test/model/impl/ObjTestRepositoryImpl.kt` | Repository using Impl class |

### DDDrive Examples (fm-dddrive/src/test)

| File | Description |
|------|-------------|
| `io/dddrive/domain/household/model/ObjHousehold.kt` | Example aggregate interface |
| `io/dddrive/domain/household/model/impl/ObjHouseholdImpl.kt` | Example migrated aggregate |
| `io/dddrive/domain/household/model/impl/ObjHouseholdPartMemberImpl.kt` | Example migrated part |
