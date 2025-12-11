# Phase 4: Update fm-ui Module to Use REST API

**Status:** Pending  
**Dependencies:** [Phase 3 - REST API](05-phase-3-rest-api.md)  
**Next Phase:** [Phase 5 - Kotlin Migration](07-phase-5-kotlin.md)

## Objective

Migrate the React UI from JSON:API to REST API consumption.

## Current State

- Uses `json-api-normalizer` and `jsonapi-serializer`
- API client expects JSON:API format with relationships
- MobX stores handle JSON:API normalized data

**Current Dependencies (package.json):**
```json
{
  "json-api-normalizer": "^1.0.4",
  "jsonapi-serializer": "^3.6.7"
}
```

## Target State

- Direct REST calls using axios
- Simpler DTO structure without JSON:API envelope
- MobX stores handle plain JSON objects

## Tasks

### 1. Create REST API Client

Create a new API client service for REST endpoints:

```typescript
// fm-ui/src/api/restClient.ts
import axios from 'axios';

const restClient = axios.create({
  baseURL: '/rest',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add auth interceptor
restClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default restClient;
```

- [ ] Create `restClient.ts`
- [ ] Add authentication interceptor
- [ ] Add error handling interceptor
- [ ] Add response transformation if needed

### 2. Create Domain API Services

For each domain, create a REST API service:

```typescript
// fm-ui/src/api/buildingApi.ts
import restClient from './restClient';
import { ObjBuildingDto } from '../@zeitwert/ui-model/building';

export const buildingApi = {
  getAll: () => restClient.get<ObjBuildingDto[]>('/buildings'),
  getOne: (id: string) => restClient.get<ObjBuildingDto>(`/buildings/${id}`),
  create: (dto: ObjBuildingDto) => restClient.post<ObjBuildingDto>('/buildings', dto),
  patch: (id: string, version: number, patch: any) => 
    restClient.patch<ObjBuildingDto>(`/buildings/${id}?version=${version}`, patch),
  delete: (id: string) => restClient.delete(`/buildings/${id}`)
};
```

- [ ] Create `accountApi.ts`
- [ ] Create `buildingApi.ts`
- [ ] Create `contactApi.ts`
- [ ] Create `portfolioApi.ts`
- [ ] Create `taskApi.ts`
- [ ] Create `documentApi.ts`
- [ ] Create `userApi.ts`

### 3. Update MobX Stores

Update stores to use REST API instead of JSON:API:

**Before (JSON:API):**
```typescript
@action
async fetchBuilding(id: string) {
  const response = await jsonApiClient.get(`/api/buildings/${id}`);
  const normalized = normalize(response.data);
  this.building = normalized.buildings[id];
}
```

**After (REST):**
```typescript
@action
async fetchBuilding(id: string) {
  const response = await buildingApi.getOne(id);
  this.building = response.data;
}
```

- [ ] Update account store
- [ ] Update building store
- [ ] Update contact store
- [ ] Update portfolio store
- [ ] Update task store
- [ ] Update document store
- [ ] Update user store

### 4. Update Model Types

Update TypeScript types to match REST DTOs:

**Location:** `fm-ui/src/@zeitwert/ui-model/`

- [ ] Update building types
- [ ] Update contact types
- [ ] Update account types
- [ ] Update other domain types
- [ ] Remove JSON:API relationship types

### 5. Update Form Submissions

Update forms to submit REST PATCH format:

**Before (JSON:API):**
```typescript
const payload = {
  data: {
    type: 'buildings',
    id: building.id,
    attributes: { name: newName }
  }
};
```

**After (REST):**
```typescript
const patch = [
  { op: 'replace', path: '/name', value: newName }
];
await buildingApi.patch(building.id, building.version, patch);
```

- [ ] Update building forms
- [ ] Update contact forms
- [ ] Update account forms
- [ ] Update other forms

### 6. Remove JSON:API Dependencies

After all stores and forms are migrated:

```bash
cd fm-ui
yarn remove json-api-normalizer jsonapi-serializer
```

- [ ] Remove `json-api-normalizer`
- [ ] Remove `jsonapi-serializer`
- [ ] Remove related TypeScript types
- [ ] Remove old API client code

### 7. Update package.json

```json
{
  "dependencies": {
    // Remove:
    // "json-api-normalizer": "^1.0.4",
    // "jsonapi-serializer": "^3.6.7",
    
    // Keep:
    "axios": "^1.2.2",
    // ...
  }
}
```

- [ ] Remove JSON:API packages
- [ ] Verify axios is present
- [ ] Run `yarn install`

### 8. Testing

- [ ] Test all list views load correctly
- [ ] Test all detail views load correctly
- [ ] Test all create operations
- [ ] Test all update operations
- [ ] Test all delete operations
- [ ] Test error handling
- [ ] Test authentication flow
- [ ] Test logout/session expiry

## Files to Update

```
fm-ui/src/
├── api/
│   ├── restClient.ts          # NEW
│   ├── buildingApi.ts         # NEW
│   ├── contactApi.ts          # NEW
│   └── ...                    # NEW for each domain
├── @zeitwert/
│   └── ui-model/
│       ├── building/          # Update types
│       ├── contact/           # Update types
│       └── ...
├── areas/
│   ├── building/
│   │   ├── BuildingList.tsx   # Update API calls
│   │   ├── BuildingDetail.tsx # Update API calls
│   │   └── ...
│   └── ...
└── app/
    └── stores/                # Update MobX stores
```

## Migration Strategy

Migrate one area at a time:

1. **Building** (largest, most complex)
2. **Contact**
3. **Account**
4. **Portfolio**
5. **Task**
6. **Document**
7. **User/Tenant**

For each area:
1. Create REST API service
2. Update store
3. Update components
4. Test thoroughly
5. Move to next

## Rollback Plan

During migration, both APIs are available:
- JSON:API: `/api/*`
- REST: `/rest/*`

If issues found, can revert specific components to use JSON:API.

## Completion Criteria

- [ ] All UI components use REST API
- [ ] JSON:API dependencies removed
- [ ] All functionality works correctly
- [ ] No console errors
- [ ] Performance acceptable
- [ ] All manual tests pass

