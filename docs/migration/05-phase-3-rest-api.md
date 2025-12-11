# Phase 3: Migrate API Layer from JSON:API to REST

**Status:** Pending  
**Dependencies:** [Phase 2c - Cleanup](04-phase-2c-cleanup.md)  
**Next Phase:** [Phase 4 - Update UI](06-phase-4-update-ui.md)

## Objective

Replace Crnk JSON:API repositories with Spring REST controllers.

## Current State (JSON:API with Crnk)

```java
// Example: ObjBuildingApiRepositoryImpl.java
@Controller("objBuildingApiRepository")
public class ObjBuildingApiRepositoryImpl
        extends AggregateApiRepositoryBase<ObjBuilding, ObjBuildingVRecord, ObjBuildingDto>
        implements ObjBuildingApiRepository {
    // ...
}
```

**API Format:** JSON:API specification with relationships, includes, sparse fieldsets

## Target State (REST Controllers)

```kotlin
@RestController
@RequestMapping("/rest/buildings")
class BuildingController {
    @Autowired
    lateinit var buildingApiRepo: ObjBuildingApiRepository

    @GetMapping
    fun getBuildings(): ResponseEntity<List<ObjBuildingDto>>

    @GetMapping("/{id}")
    fun getBuilding(@PathVariable id: String): ResponseEntity<ObjBuildingDto>

    @PostMapping
    fun createBuilding(@RequestBody dto: ObjBuildingDto): ResponseEntity<ObjBuildingDto>

    @PatchMapping("/{id}")
    fun patchBuilding(
        @PathVariable id: String,
        @RequestParam("version") version: Int,
        @RequestBody patch: JsonNode
    ): ResponseEntity<ObjBuildingDto>

    @DeleteMapping("/{id}")
    fun deleteBuilding(@PathVariable id: String): ResponseEntity<Void>
}
```

**API Format:** Standard REST with JSON DTOs

## Migration Order

| Order | Domain | Endpoint | Priority |
|-------|--------|----------|----------|
| 1 | Account | `/rest/accounts` | High |
| 2 | Building | `/rest/buildings` | High |
| 3 | Contact | `/rest/contacts` | High |
| 4 | Portfolio | `/rest/portfolios` | Medium |
| 5 | Task | `/rest/tasks` | Medium |
| 6 | Document | `/rest/documents` | Medium |
| 7 | User | `/rest/users` | Medium |
| 8 | Tenant | `/rest/tenants` | Low |

## Tasks

### 1. Create REST Infrastructure

- [ ] Create base controller class with common error handling
- [ ] Create standard response DTOs
- [ ] Configure Jackson for REST serialization
- [ ] Add OpenAPI/Swagger documentation (optional)

### 2. For Each Domain

Use this template for each domain REST migration:

#### Domain: [NAME]

**Current:** `io.zeitwert.fm.[domain].adapter.api.jsonapi`  
**Target:** `io.zeitwert.fm.[domain].adapter.api.rest`

##### Tasks
- [ ] Create `[Domain]Controller.kt` with REST endpoints
- [ ] Create/update DTO classes for REST format
- [ ] Create API repository interface
- [ ] Implement API repository
- [ ] Add endpoint security configuration
- [ ] Write controller tests
- [ ] Document API endpoints

---

### 3. Domain: Account

**Endpoints:**
- `GET /rest/accounts` - List accounts
- `GET /rest/accounts/{id}` - Get account
- `POST /rest/accounts` - Create account
- `PATCH /rest/accounts/{id}` - Update account
- `DELETE /rest/accounts/{id}` - Delete account

**Files to Create:**
```
account/adapter/api/rest/
├── AccountController.kt
├── dto/
│   └── ObjAccountDto.kt
└── impl/
    └── ObjAccountApiRepositoryImpl.kt
```

- [ ] Create AccountController
- [ ] Create REST DTOs
- [ ] Implement API repository
- [ ] Add tests

---

### 4. Domain: Building

**Endpoints:**
- `GET /rest/buildings` - List buildings
- `GET /rest/buildings/{id}` - Get building with ratings
- `POST /rest/buildings` - Create building
- `PATCH /rest/buildings/{id}` - Update building
- `DELETE /rest/buildings/{id}` - Delete building
- `POST /rest/buildings/{id}/ratings` - Add rating
- `GET /rest/buildings/{id}/projection` - Get projection

**Files to Create:**
```
building/adapter/api/rest/
├── BuildingController.kt
├── BuildingRatingController.kt
├── BuildingProjectionController.kt
├── dto/
│   ├── ObjBuildingDto.kt
│   ├── ObjBuildingPartRatingDto.kt
│   └── ProjectionDto.kt
└── impl/
    └── ObjBuildingApiRepositoryImpl.kt
```

- [ ] Create controllers
- [ ] Create REST DTOs
- [ ] Implement API repository
- [ ] Add tests

---

### 5. Update Security Configuration

Update `WebSecurityConfiguration.java` to secure new REST endpoints:

```java
http.authorizeHttpRequests(auth -> auth
    // Old JSON:API endpoints (keep during transition)
    .requestMatchers("/api/**").authenticated()
    // New REST endpoints
    .requestMatchers("/rest/**").authenticated()
    // ...
);
```

- [ ] Add `/rest/**` security rules
- [ ] Configure CORS for REST endpoints
- [ ] Update JWT filter if needed

### 6. Parallel Operation (Transition Period)

During UI migration, both APIs should work:

- [ ] Keep Crnk/JSON:API endpoints active (`/api/*`)
- [ ] Add new REST endpoints (`/rest/*`)
- [ ] Document which endpoints are deprecated
- [ ] Plan removal of old endpoints after UI migration

### 7. Remove Crnk Dependencies (After Phase 4)

Once UI is migrated to REST:

```xml
<!-- REMOVE these after UI migration -->
<dependency>
    <groupId>io.crnk</groupId>
    <artifactId>crnk-core</artifactId>
</dependency>
<dependency>
    <groupId>io.crnk</groupId>
    <artifactId>crnk-setup-spring</artifactId>
</dependency>
```

- [ ] Remove Crnk dependencies
- [ ] Remove `CrnkConfiguration.java`
- [ ] Remove all `*ApiRepository` JSON:API classes
- [ ] Remove JSON:API DTOs

## Files to Reference

| File | Purpose |
|------|---------|
| [HouseholdController.kt](../dfp-app-server/dfp-finplan/src/main/java/ch/dfp/finplan/household/api/rest/HouseholdController.kt) | REST controller pattern |
| [CrnkConfiguration.java](src/main/java/io/zeitwert/fm/server/config/crnk/CrnkConfiguration.java) | Current Crnk config (to remove later) |
| [ObjBuildingApiRepositoryImpl.java](src/main/java/io/zeitwert/fm/building/adapter/api/jsonapi/impl/ObjBuildingApiRepositoryImpl.java) | Current JSON:API pattern |

## REST Controller Template

```kotlin
@RestController
@RequestMapping("/rest/[domain]s")
class [Domain]Controller {

    @Autowired
    lateinit var apiRepo: Obj[Domain]ApiRepository

    @Autowired
    @Qualifier("ui")
    lateinit var objectMapper: ObjectMapper

    @GetMapping
    fun getAll(): ResponseEntity<List<Obj[Domain]Dto>> =
        ResponseEntity.ok(apiRepo.findAll())

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: String): ResponseEntity<Obj[Domain]Dto> =
        apiRepo.findOne(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: Obj[Domain]Dto): ResponseEntity<Obj[Domain]Dto> =
        ResponseEntity.status(HttpStatus.CREATED).body(apiRepo.save(dto))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: String,
        @RequestParam("version") version: Int,
        @RequestBody patch: JsonNode
    ): ResponseEntity<Obj[Domain]Dto> {
        val existing = apiRepo.findOne(id) ?: return ResponseEntity.notFound().build()
        val patched = applyPatch(existing, patch, version)
        return ResponseEntity.ok(apiRepo.save(patched))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        apiRepo.delete(id)
        return ResponseEntity.noContent().build()
    }
}
```

## Completion Criteria

- [ ] All domain REST controllers created
- [ ] All REST endpoints working
- [ ] Security configured for `/rest/**`
- [ ] Both `/api/*` (JSON:API) and `/rest/*` work during transition
- [ ] API documentation available
- [ ] Controller tests pass

