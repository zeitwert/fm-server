

# Aggregate Lifecycle

## create

```mermaid
sequenceDiagram
	participant C as Consumer
	participant ER as XyzRepositoryImpl
	participant OR as ObjRepositoryBase
	participant AR as AggregateRepositoryBase
	participant ARS as AggregateRepositorySPI
	participant A as AggregateSPI
	participant PR as ___Part___Repository
	C->>ER: create(sessionInfo)
	ER-->>+AR: create(sessionInfo)
	AR->>ARS: nextAggregateId()
	AR->>ARS: doCreate(sessionInfo)
	ARS-->>ER: doCreate(...)
	ER-->>OR: doCreate(..., new extnRecord)
	OR-->>AR: newAggregate(..., new objRecord, extnRecord)
	AR->>A: doInit(id, tenant) [technical init]
	AR->>PR: init() [technical init of part repos]
	AR->>A: calcAll()
	AR->>-ARS: doAfterCreate()
	ARS->>A: doAfterCreate() [business init]
```

## get

```mermaid
sequenceDiagram
	participant C as Consumer
	participant ER as XyzRepositoryImpl
	participant OR as ObjRepositoryBase
	participant AR as AggregateRepositoryBase
	participant ARS as AggregateRepositorySPI
	participant A as AggregateSPI
	participant PR as ___Part___Repository
	participant PRS as PartRepositorySPI
	C->>ER: get(sessionInfo, id): A
	ER-->>+AR: get(...): A
	AR->>ARS: doLoad(sessionInfo, id): A
	ARS-->>ER: doLoad(sessionInfo, id): A
	ER-->>OR: doLoad(sessionInfo, fetch extnRecord)
	OR-->>AR: newAggregate(sessionInfo, fetch objRecord, extnRecord)
	AR->>ARS: doLoadParts() [load parts]
	ARS-->>AR: doLoadParts()
	AR-->>PR: load() [load from db]
	PR-->>PRS: doLoad()
	AR->>ARS: doAssignParts() [assign parts]
	ARS-->>AR: doAssignParts()
	AR-->>OR: doAssignParts()
	OR-->>A: loadXyzList()
	OR-->>ER: doAssignParts()
	ER-->>A: loadXyzList()
	AR->>A: calcVolatile()
	AR->>-ARS: doAfterLoad()
	ARS->>A: doAfterLoad() [business init]
```

todo
- doAfterLoad for Parts
- doLoadParts for Parts [assign to memory list]

## store

```mermaid
sequenceDiagram
	participant C as Consumer
	participant ER as XyzRepositoryImpl
	participant OR as ObjRepositoryBase
	participant AR as AggregateRepositoryBase
	participant ARS as AggregateRepositorySPI
	participant A as AggregateSPI
	participant PR as ___Part___Repository
	C->>ER: store(A)
	ER-->>+AR: store(A)
	AR->>ARS: doBeforeStore()
	ARS->>A: doBeforeStore() [business prep]
	AR->>A: doStore()
	AR->>ARS: doStoreParts() [store parts]
	ARS-->>AR: doStoreParts()
	AR-->>PR: store()
	AR-->>OR: doStoreParts()
	OR-->>PR: store()
	OR-->>ER: doStoreParts()
	ER-->>PR: store()
	AR->>-ARS: doAfterStore()
	ARS->>A: doAfterStore() [business init]
```

todo
- doBeforeStore for Parts
- doAfterStore for Parts

# Part Lifecycle

## create

```mermaid
sequenceDiagram
	participant C as Consumer
	participant A as Aggregate
	participant PLP as PartListProperty
	participant EPR as ___PartXyzRepositoryImpl
	participant PR as PartRepositoryBase
	participant P as Part
	C->>A: addXyz()
	A-->>PLP: addPart() (via PropertyHandler)
	PLP-->>A: addPart() (EntityWithPropertySPI callback)
	A-->>EPR: create(A, partListType)
	EPR-->>PR: create(A, partListType)
	PR->>EPR: doCreate(aggregate)
	EPR-->>PR: newPart(aggregate, new dbRecord)
	PR->>EPR: doInit(part, partId, aggregate, parent, partListTypeId)
	EPR-->>PR: doInit(...)
	PR-->>P: doInit(partId, aggregate, parent, partListTypeId)
	PR-->>P: afterCreate()
```

todo
- PartRepositorySPI.doAfterCreate
- Part calcAll, calcVolatile
- PartRepositorySPI.doInitParts

## get

See aggregate get above

## store

See aggregate store above
