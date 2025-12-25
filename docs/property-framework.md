# DDDrive Property Framework

This document describes the property framework used in the DDDrive application development library. The framework manages typed properties on domain entities (Aggregates and Parts) with support for change tracking, validation, and persistence.

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

## Old Model: Reflection-Based Properties

### How It Works

1. **Property Registration in `doInit()`**

   Base classes register properties imperatively in the `doInit()` lifecycle method:

   ```kotlin
   // In AggregateBase.kt
   override fun doInit() {
       addBaseProperty("id", Any::class.java)
       addBaseProperty("version", Int::class.java)
       addReferenceProperty("tenant", ObjTenant::class.java)
       addReferenceProperty("owner", ObjUser::class.java)
       addBaseProperty("caption", String::class.java)
       // ... more properties
       doInitSeqNr += 1
   }
   ```

2. **Javassist Proxy Creation**

   The repository creates aggregate instances using Javassist proxies:

   ```kotlin
   // In AggregateRepositoryBase.kt
   private fun createAggregate(isNew: Boolean): A {
       return aggregateProxyFactory.create(
           aggregateProxyFactoryParamTypeList,
           arrayOf(this, isNew),
           PropertyHandler.INSTANCE,  // <-- intercepts method calls
       ) as A
   }
   ```

3. **PropertyHandler Intercepts Calls**

   The `PropertyHandler` implements `MethodHandler` and routes getter/setter calls to the underlying property objects:

   ```kotlin
   // In PropertyHandler.kt
   override fun invoke(self: Any, m: Method, proceed: Method?, args: Array<Any?>): Any? {
       val methodName = m.name
       val fieldName = getFieldName(methodName)
       val property = getProperty(self, fieldName)
       
       if (isGetter(methodName, args)) {
           return when (property) {
               is BaseProperty<*> -> property.value
               is AggregateReferenceProperty<*> -> 
                   if (m.name.endsWith("Id")) property.id else property.value
               // ... other property types
           }
       } else if (isSetter(methodName, args)) {
           when (property) {
               is BaseProperty<*> -> (property as BaseProperty<Any?>).value = args[0]
               // ... other property types
           }
       }
       return null
   }
   ```

4. **Path-Based Access**

   For internal code, properties can be accessed by path strings:

   ```kotlin
   // Setting a value by path
   setValueByPath("tenantId", tenantId)
   setValueByPath("createdByUserId", userId)
   
   // Getting a value by path
   val version = getValueByPath<Int>("version")
   ```

### Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `EntityWithPropertiesBase` | `property/model/base/` | Base class with `addBaseProperty()`, `addReferenceProperty()`, etc. |
| `PropertyHandler` | `property/model/impl/` | Javassist `MethodHandler` that intercepts method calls |
| `PropertyFilter` | `property/model/impl/` | Filters which methods to intercept |
| `AggregateRepositoryBase` | `ddd/model/base/` | Creates proxied aggregate instances |
| `PathAccess.kt` | `path/` | `getValueByPath()` and `setValueByPath()` functions |

### Property Types

| Type | Registration Method | Value Type |
|------|-------------------|------------|
| `BaseProperty<T>` | `addBaseProperty(name, type)` | Simple types: String, Int, BigDecimal, OffsetDateTime, etc. |
| `EnumProperty<E>` | `addEnumProperty(name, enumType)` | Enumerated values |
| `AggregateReferenceProperty<A>` | `addReferenceProperty(name, aggregateType)` | Reference to another aggregate |
| `PartReferenceProperty<P>` | `addPartReferenceProperty(name, partType)` | Reference to a part within same aggregate |
| `EnumSetProperty<E>` | `addEnumSetProperty(name, enumType)` | Set of enum values |
| `ReferenceSetProperty<A>` | `addReferenceSetProperty(name, aggregateType)` | Set of aggregate references (by ID) |
| `PartListProperty<P>` | `addPartListProperty(name, partType)` | Ordered list of parts |

### Limitations

- **No compile-time type safety**: Errors only surface at runtime
- **Hidden complexity**: Method interception is non-obvious
- **Interface satisfaction is implicit**: The `Aggregate` interface declares `val id: Any`, but `AggregateBase` doesn't explicitly override it—the proxy handles it
- **Debugging difficulty**: Stack traces go through proxy machinery
- **Reflection overhead**: Minor performance cost

---

## New Model: Kotlin Delegation-Based Properties

### How It Works

1. **Property Delegates**

   Properties are defined using Kotlin's `by` delegation syntax:

   ```kotlin
   // In ObjHouseholdBase.kt
   class ObjHouseholdBase(...) : ObjBase(...), ObjHousehold {
       
       // Simple base property
       override var name: String? by baseProperty()
       
       // Enum property
       override var salutation: CodeSalutation? by enumProperty()
       
       // Aggregate reference (value and ID accessors)
       override var responsibleUser: ObjUser? by referenceProperty()
       override var responsibleUserId: Any? by referenceIdProperty<ObjUser>()
       
       // Part reference
       override var mainMember: ObjHouseholdPartMember? by partReferenceProperty()
       override var mainMemberId: Int? by partReferenceIdProperty<ObjHouseholdPartMember>()
       
       // Collection properties (read-only, return the property object itself)
       override val labelSet: EnumSetProperty<CodeLabel> by enumSetProperty()
       override val userSet: ReferenceSetProperty<ObjUser> by referenceSetProperty()
       override val memberList: PartListProperty<ObjHouseholdPartMember> by partListProperty()
   }
   ```

2. **Delegate Implementation**

   Each delegate lazily creates and caches the underlying property:

   ```kotlin
   // In PropertyDelegates.kt
   class BasePropertyDelegate<T : Any>(
       private val type: Class<T>,
   ) : ReadWriteProperty<EntityWithProperties, T?> {
   
       @Volatile
       private var property: BaseProperty<T>? = null
   
       override fun getValue(thisRef: EntityWithProperties, property: KProperty<*>): T? =
           getOrCreateProperty(thisRef, property).value
   
       override fun setValue(thisRef: EntityWithProperties, property: KProperty<*>, value: T?) {
           getOrCreateProperty(thisRef, property).value = value
       }
   
       private fun getOrCreateProperty(entity: EntityWithProperties, prop: KProperty<*>): BaseProperty<T> {
           var p = property
           if (p == null) {
               synchronized(this) {
                   p = property
                   if (p == null) {
                       // Uses the Kotlin property name to register
                       p = (entity as EntityWithPropertiesBase).getOrAddBaseProperty(prop.name, type)
                       property = p
                   }
               }
           }
           return p!!
       }
   }
   ```

3. **Reified Factory Functions**

   Cleaner syntax using reified type parameters:

   ```kotlin
   // Factory functions in PropertyDelegates.kt
   inline fun <reified T : Any> baseProperty(): BasePropertyDelegate<T> = 
       BasePropertyDelegate(T::class.java)
   
   inline fun <reified E : Enumerated> enumProperty(): EnumPropertyDelegate<E> = 
       EnumPropertyDelegate(E::class.java)
   
   inline fun <reified A : Aggregate> referenceProperty(): ReferencePropertyDelegate<A> = 
       ReferencePropertyDelegate(A::class.java)
   
   // ... etc.
   ```

### Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `PropertyDelegates.kt` | `property/delegate/` | `ReadWriteProperty` delegates for value properties |
| `PropertyHolders.kt` | `property/delegate/` | `ReadOnlyProperty` delegates for collection properties |
| `EntityWithPropertiesBase` | `property/model/base/` | `getOrAdd*Property()` methods for lazy registration |

### Delegate Types

| Delegate | Factory Function | Returns | Use Case |
|----------|-----------------|---------|----------|
| `BasePropertyDelegate<T>` | `baseProperty<T>()` | `T?` | Simple values |
| `EnumPropertyDelegate<E>` | `enumProperty<E>()` | `E?` | Enum values |
| `ReferencePropertyDelegate<A>` | `referenceProperty<A>()` | `A?` | Aggregate reference (full object) |
| `ReferenceIdPropertyDelegate<A>` | `referenceIdProperty<A>()` | `Any?` | Aggregate reference (ID only) |
| `PartReferencePropertyDelegate<P>` | `partReferenceProperty<P>()` | `P?` | Part reference (full object) |
| `PartReferenceIdPropertyDelegate<P>` | `partReferenceIdProperty<P>()` | `Int?` | Part reference (ID only) |
| `EnumSetPropertyHolder<E>` | `enumSetProperty<E>()` | `EnumSetProperty<E>` | Set of enums |
| `ReferenceSetPropertyHolder<A>` | `referenceSetProperty<A>()` | `ReferenceSetProperty<A>` | Set of references |
| `PartListPropertyHolder<P>` | `partListProperty<P>()` | `PartListProperty<P>` | List of parts |

### Benefits

- **Compile-time type safety**: Errors caught during compilation
- **Explicit interface satisfaction**: `override` keyword makes it clear
- **Standard Kotlin idioms**: No proxy magic, easier to understand
- **Better IDE support**: Autocomplete, navigation, refactoring work properly
- **Cleaner debugging**: Standard call stacks

---

## Migration Guide: Old to New Model

### Step 1: Identify Properties in `doInit()`

Find all `add*Property()` calls in the class hierarchy:

```kotlin
// Old: in doInit()
addBaseProperty("name", String::class.java)
addEnumProperty("status", CodeStatus::class.java)
addReferenceProperty("owner", ObjUser::class.java)
```

### Step 2: Add Delegated Properties

Replace with delegation, adding `override` if satisfying an interface:

```kotlin
// New: as class properties
override var name: String? by baseProperty()
override var status: CodeStatus? by enumProperty()
override var owner: ObjUser? by referenceProperty()
```

### Step 3: Handle Reference ID Properties

For references, you often need both value and ID access:

```kotlin
// Old: PropertyHandler handles both getOwner() and getOwnerId()
addReferenceProperty("owner", ObjUser::class.java)

// New: explicit properties for both
override var owner: ObjUser? by referenceProperty()
override var ownerId: Any? by referenceIdProperty<ObjUser>()  // Note: strips "Id" suffix to find "owner" property
```

### Step 4: Update `doInit()`

Remove the property registration calls but keep the `doInitSeqNr` increment:

```kotlin
override fun doInit() {
    super.doInit()
    // Property registrations removed - now handled by delegates
    doInitSeqNr += 1
}
```

### Step 5: Update `setValueByPath()` Calls

Internal code using path access still works, but direct property access is preferred:

```kotlin
// Old
setValueByPath("name", "New Name")

// New (preferred)
name = "New Name"
```

---

## Class Hierarchy

```
EntityWithPropertiesBase
├── AggregateBase (implements Aggregate, AggregateMeta)
│   ├── ObjBase (implements Obj, ObjMeta)
│   │   ├── ObjUserBase
│   │   ├── ObjTenantBase
│   │   └── ObjHouseholdBase (test)
│   └── DocBase (implements Doc, DocMeta)
│       └── DocTaskBase (test)
└── PartBase (implements Part, PartMeta)
    ├── ObjPartBase (implements ObjPart)
    │   ├── ObjPartTransitionBase
    │   └── ObjHouseholdPartMemberBase (test)
    └── DocPartBase (implements DocPart)
        └── DocPartTransitionBase
```

---

## Common Patterns

### Defining a New Aggregate

```kotlin
// Interface
interface ObjCustomer : Obj {
    var name: String?
    var email: String?
    var accountManager: ObjUser?
    val orderList: PartListProperty<ObjCustomerPartOrder>
}

// Base class (new model)
open class ObjCustomerBase(
    repository: ObjCustomerRepository,
    isNew: Boolean,
) : ObjBase(repository, isNew), ObjCustomer {

    override var name: String? by baseProperty()
    override var email: String? by baseProperty()
    override var accountManager: ObjUser? by referenceProperty()
    override val orderList: PartListProperty<ObjCustomerPartOrder> by partListProperty()

    override fun doAddPart(property: Property<*>, partId: Int?): Part<*> =
        if (property === orderList) {
            directory.getPartRepository(ObjCustomerPartOrder::class.java)
                .create(this, property, partId) as Part<*>
        } else {
            super.doAddPart(property, partId)
        }
}
```

### Defining a New Part

```kotlin
// Interface
interface ObjCustomerPartOrder : ObjPart<ObjCustomer> {
    val customer: ObjCustomer
    var orderNumber: String?
    var status: CodeOrderStatus?
}

// Base class (new model)
open class ObjCustomerPartOrderBase(
    obj: ObjCustomer,
    repository: PartRepository<ObjCustomer, ObjCustomerPartOrder>,
    property: Property<*>,
    id: Int,
) : ObjPartBase<ObjCustomer>(obj, repository, property, id), ObjCustomerPartOrder {

    override val customer: ObjCustomer = aggregate
    override var orderNumber: String? by baseProperty()
    override var status: CodeOrderStatus? by enumProperty()

    override fun delete() {
        // Custom delete logic if needed
    }
}
```

---

## Files Reference

### Core Framework (main)

| File | Description |
|------|-------------|
| `fm-dddrive/src/main/java/io/dddrive/property/delegate/PropertyDelegates.kt` | Value property delegates |
| `fm-dddrive/src/main/java/io/dddrive/property/delegate/PropertyHolders.kt` | Collection property holders |
| `fm-dddrive/src/main/java/io/dddrive/property/model/base/EntityWithPropertiesBase.kt` | Base class with property management |
| `fm-dddrive/src/main/java/io/dddrive/property/model/impl/PropertyHandler.kt` | Reflection-based method handler |
| `fm-dddrive/src/main/java/io/dddrive/path/PathAccess.kt` | Path-based property access |
| `fm-dddrive/src/main/java/io/dddrive/ddd/model/base/AggregateBase.kt` | Aggregate base class |
| `fm-dddrive/src/main/java/io/dddrive/ddd/model/base/PartBase.kt` | Part base class |

### Test Examples

| File | Description |
|------|-------------|
| `fm-dddrive/src/test/java/io/dddrive/domain/household/model/ObjHousehold.kt` | Household aggregate interface |
| `fm-dddrive/src/test/java/io/dddrive/domain/household/model/base/ObjHouseholdBase.kt` | Household using new delegation model |
| `fm-dddrive/src/test/java/io/dddrive/test/HouseholdMemTest.kt` | Integration test |

