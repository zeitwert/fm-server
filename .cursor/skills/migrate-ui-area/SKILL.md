---
name: Migrate UI Area
description: Migrate or create a new aggregate area in the fm-ux application.
---

# Implementing an Entity Area in fm-ux

This cookbook describes how to migrate or create a new entity area in the fm-ux application.

## Overview

An entity area consists of:
- **List View** (`{Entity}Area`) - Table with create modal using `ItemsPage`
- **Detail View** (`{Entity}Page`) - Edit form with `useEditableEntity` hook
- **API Layer** - CRUD operations using `createEntityApi`
- **Query Hooks** - TanStack Query hooks for data fetching
- **Routes** - TanStack Router file-based routes

## Code Style

**Avoid superfluous comments.** The code should be self-documenting. Do not add:
- File header JSDoc comments that just restate the file name
- Interface/type comments that repeat what's obvious from the code
- Inline comments explaining obvious code (e.g., `// Basic fields`, `// Relations`)
- Function comments that just repeat the function name
- Section comments in JSX that label obvious sections

Only add comments when they explain *why* something is done a certain way, not *what* the code does.

## Discovering Entity Fields

Before implementing the UI, gather field definitions from these sources:

### 1. List Columns (`fm-domain`)

**Location:** `fm-domain/src/main/resources/config/t0/{module}/datamarts/{entities}/layouts/default.json`

Primary source for list view columns. The `header` array defines which columns to show:

```json
{
  "header": [
    { "id": "name", "label": "Name", "value": "name" },
    { "id": "accountType", "label": "Type", "value": "accountType" },
    { "id": "owner", "label": "Owner", "value": "owner" }
  ]
}
```

Map these to `columns` in `{Entity}Area.tsx`. Note: nested values like `accountType` (which maps to `accountType.name` in the data section) should use `dataIndex: ["accountType", "name"]` in the column definition.

### 2. Form Definitions (`fm-ui`)

**Location:** `fm-ui/src/areas/{entity}/ui/forms/{Entity}Form.ts`

Primary source for form field list, types, required flags, and data sources:

| Field Type | TypeScript Type | UI Component |
|------------|-----------------|--------------|
| `TextField` | `string` | `AfInput` / `AfTextArea` |
| `EnumeratedField` | `Enumerated` | `AfSelect` (code table) |
| `AggregateField` | `Enumerated` | `AfSelect` (relationship) |
| `NumberField` | `number` | `AfNumber` |
| `DateField` | `string` (ISO) | `AfDatePicker` |
| `IdField` | `string` | (display only) |

### 3. API Implementation (`fm-ui`)

**Location:** `fm-ui/src/@zeitwert/ui-model/fm/{entity}/service/impl/{Entity}ApiImpl.ts`

Primary source for JSON:API configuration (module, path, includes, relations).

Copy these values directly to your new `api.ts`:
- `MODULE` → `module`
- `PATH` → `path`
- `TYPE` → `type`
- `INCLUDES` → `includes`
- `RELATIONS` → `relations`

### Workflow

1. **Check layout config** (`fm-domain/.../layouts/default.json`) - Columns for list view
2. **Check `{Entity}Form.ts`** - Form fields, types, required flags, and dropdown sources
3. **Check `{Entity}ApiImpl.ts`** - Copy module, path, includes, and relations for the API

### Troubleshooting: Server Sources

If tests fail or you need to verify field definitions, check these server files:

**`{Entity}Impl.kt`** (`fm-domain/src/main/java/io/zeitwert/fm/{module}/model/impl/`)
- Authoritative field definitions using property delegates
- `baseProperty<T>` for primitives, `enumProperty<T>` for code tables, `referenceProperty<T>` for relationships

**`{Entity}DtoAdapter.kt`** (`fm-domain/src/main/java/io/zeitwert/fm/{module}/adapter/jsonapi/impl/`)
- `config.relationship()` defines JSON:API relationships
- `config.field()` defines field name mappings or custom getters/setters
- `config.exclude()` hides fields from the DTO

## Directory Structure

Create the following structure under `fm-ux/src/areas/{entity}/`:

```
fm-ux/src/areas/{entity}/
├── types.ts           # Entity type definitions
├── schemas.ts         # Zod validation schemas
├── api.ts             # Entity API using createEntityApi
├── queries.ts         # TanStack Query hooks
├── index.ts           # Module exports
└── ui/
    ├── {Entity}Area.tsx           # List view (ItemsPage)
    ├── {Entity}Page.tsx           # Detail view
    ├── {Entity}Preview.tsx        # Preview drawer (optional)
    └── forms/
        ├── {Entity}MainForm.tsx       # Main form tab
        └── {Entity}CreationForm.tsx   # Creation modal form
```

Also create route files under `fm-ux/src/routes/`:

```
fm-ux/src/routes/
├── {entity}.tsx               # Layout route (renders <Outlet />)
├── {entity}.index.tsx         # List view at /{entity}
└── {entity}.${entity}Id.tsx   # Detail view at /{entity}/:id
```

## Step-by-Step Implementation

The **account area** is the canonical reference implementation. For each file below, read the corresponding account file and adapt it to your entity.

### 1. Types (`types.ts`)

Defines TypeScript interfaces for the entity and list item.

**Reference:** `fm-ux/src/areas/account/types.ts`

**Adaptation notes:**
- Replace `Account*` with your entity name
- Add/remove fields based on field discovery from fm-ui
- Include `EntityMeta` for optimistic locking support
- Define only: full entity interface, list item interface (fewer fields)
- **Do NOT add FormData type** - use schema-derived types from `schemas.ts` instead

### 2. Schemas (`schemas.ts`)

Zod validation schemas for form validation.

**Reference:** `fm-ux/src/areas/account/schemas.ts`

**Adaptation notes:**
- Create both `{entity}CreationSchema` and `{entity}FormSchema` (separate schemas for creation vs editing)
- Import `enumeratedSchema` and `displayOnly` from `common/utils/zodMeta`
- Match required flags from fm-ui field definitions

### 3. API (`api.ts`)

Entity API using `createEntityApi` factory for CRUD operations.

**Reference:** `fm-ux/src/areas/account/api.ts`

**Adaptation notes:**
- Copy module, path, type, includes, and relations from `{Entity}ApiImpl.ts` in fm-ui
- Create both `{entity}Api` (full entity) and `{entity}ListApi` (list with fewer includes)
- Only true relationships go in `includes` and `relations` (see "Code Tables vs Relationships" gotcha)

### 4. Query Hooks (`queries.ts`)

TanStack Query hooks for data fetching and mutations.

**Reference:** `fm-ux/src/areas/account/queries.ts`

**Adaptation notes:**
- Replace entity name in query keys, function names, and success messages
- Include `useCreate{Entity}`, `useUpdate{Entity}`, optionally `useDelete{Entity}`
- Add `get{Entity}QueryOptions` for prefetching if needed
- Use German messages for success/error notifications

### 5. List View (`ui/{Entity}Area.tsx`)

Table view using `ItemsPage` component with create modal.

**Reference:** `fm-ux/src/areas/account/ui/AccountArea.tsx`

**Adaptation notes:**
- Define columns based on `header` array from layout config (`fm-domain/.../layouts/default.json`)
- For `Enumerated` fields, use `dataIndex: ["{field}", "name"]` to access the nested `name` property
- **Icon**: Use `getArea("{entity}")?.icon` from `AppConfig.ts` instead of hardcoding. This ensures consistency across Area, Page, and Preview components.
- Use `canCreate()` permission check with appropriate roles (see Gotcha #9)
- Pass `PreviewComponent` if you want a preview drawer (optional)
- Use `[...{entity}Keys.lists()]` spread for queryKey (readonly array conversion)

### 6. Preview Panel (`ui/{Entity}Preview.tsx`) - Optional

Preview drawer shown when clicking the eye icon in the list.

**Reference:** `fm-ux/src/areas/account/ui/AccountPreview.tsx`

**Adaptation notes:**
- Receives `id` and `onClose` props
- Fetch full entity using query hook
- Show key fields in `Descriptions` component
- Include "Edit" button that navigates to detail page
- Use `getLogoUrl()` if entity has a logo, with `onError` fallback
- **Use CSS utility classes** instead of inline styles (`.af-preview-container`, `.af-preview-avatar`, etc.)

### 7. Detail View (`ui/{Entity}Page.tsx`)

Edit form using `useEditableEntity` hook with tabs.

**Reference:** `fm-ux/src/areas/account/ui/AccountPage.tsx`

**Key patterns from account (follow exactly):**
- Use `AfForm` wrapper (not `FormProvider` directly)
- Place `EditControls` in `Tabs tabBarExtraContent` (not in header)
- Wrap page in `<div className="af-flex-column af-full-height">`
- Use `ItemPageHeader` with `details` array for metadata display
- Use `ItemPageLayout` with `RelatedPanel` in `rightPanel`

**Adaptation notes:**
- Pass schema to `useEditableEntity` - display-only fields are auto-detected
- Update field references in header details
- Add additional tabs for related data if needed

### 8. Main Form (`ui/forms/{Entity}MainForm.tsx`)

Form fields for the main tab.

**Reference:** `fm-ux/src/areas/account/ui/forms/AccountMainForm.tsx`

**Key patterns from account (follow exactly):**
- Use `AfFieldGroup` with `legend` for grouping (not `Card`)
- Use `Row`/`Col` for multi-column layout
- Use `AfFieldRow` for inline field groups
- Use `useFormContext` to access form state if needed
- **Do NOT include tenant field** - shown in page header already

**Adaptation notes:**
- Match field layout to the old fm-ui form
- Use correct `source` prop for AfSelect dropdowns

### 9. Creation Form (`ui/forms/{Entity}CreationForm.tsx`)

Modal form for creating new entities.

**Reference:** `fm-ux/src/areas/account/ui/forms/AccountCreationForm.tsx`

**Key patterns from account (follow exactly):**
- Use `AfForm` with `onSubmit` prop (handles form wrapper + submission)
- Validate required fields manually in submit handler (no zodResolver)
- Set smart defaults for owner (current user)
- **Do NOT include tenant field** - server sets tenant automatically

**Adaptation notes:**
- Include only fields needed for creation (minimal set)
- Use `useCreate{Entity}` mutation hook
- Call `onSuccess()` after successful creation
- **Navigate to the created entity's detail page after success with try/catch**

**Parent context pattern:** If the entity can be created from a parent entity's page (e.g., creating a Contact from an Account), extend `CreateFormProps` to accept the parent entity as an optional prop. See `ContactCreationForm` for an example.

### 10. Routes

TanStack Router file-based routes. These are pure boilerplate - copy from the account routes and replace entity names.

**Reference files:**
- `fm-ux/src/routes/account.tsx` (layout - just renders `<Outlet />`)
- `fm-ux/src/routes/account.index.tsx` (list - renders `{Entity}Area`)
- `fm-ux/src/routes/account.$accountId.tsx` (detail - extracts ID param and renders `{Entity}Page`)

### 11. Translations

Create translation files for both German and English.

**Location:** `fm-ux/src/i18n/locales/de/{entity}.json` and `.../en/{entity}.json`

**Reference:** `fm-ux/src/i18n/locales/de/account.json` and `.../en/account.json`

**Structure guidelines:**
- `label`: Nouns for field labels, entity names, tab names, section headings
- `action`: Verbs for buttons, links, user actions
- `message`: Notifications, errors, validation messages, descriptions

**ICU Plural Format:**

Use ICU MessageFormat for entity counts (requires `i18next-icu` plugin):

```json
"entityCount": "{count, plural, =0 {No contacts} one {# contact} other {# contacts}}"
```

Usage in code: `t("contact.label.entityCount", { count: items.length })`

**Accessing Translations:**

Use the unified namespace with dot-notation:

```typescript
import { useTranslation } from "react-i18next";

const { t } = useTranslation();

// Access translations with dot-notation
t("contact.label.name")           // Field label
t("contact.action.backToList")    // Action button
t("contact.message.notFound")     // Error message
t("common.action.save")           // Shared common translations
```

**Required keys per entity:**
- `label.entity`: Entity singular name (e.g., "Kontakt")
- `label.entityCount`: ICU plural format for counts
- `label.*`: All field labels, tab names, section headings
- `action.backToList`: Navigation back to list
- `message.notFound`, `message.notFoundDescription`: Error states
- `message.validation.*`: Form validation messages

**Update `i18n/index.ts`:**

1. Add imports for both languages:
```typescript
import enEntity from "./locales/en/{entity}.json";
import deEntity from "./locales/de/{entity}.json";
```

2. Add to the resources object (namespaces are at root level, not nested under `translation`):
```typescript
const resources = {
  en: {
    // ... existing namespaces
    {entity}: enEntity,
  },
  de: {
    // ... existing namespaces
    {entity}: deEntity,
  },
};
```

3. Add the namespace to the `namespaces` array:
```typescript
const namespaces = ["common", "login", "app", "home", "account", "{entity}"];
```

### 12. Index Export (`index.ts`)

Re-export public API from the area module. Use explicit named exports (not `export *`) for better tree-shaking and clarity.

**Reference:** `fm-ux/src/areas/account/index.ts`

Export types, schemas, API, queries, and UI components that other areas might need.

## Common Patterns & Gotchas

### 1. Code Tables vs Relationships (CRITICAL)

**Common mistake**: Adding code tables or base fields to `relations` causes errors like `"Invalid relationship name: owner for account"`.

**Rule:**
- **Code tables**: NOT in `includes` or `relations` (they're attributes)
- **Base fields** (`owner`, `tenant`): NOT in `includes` or `relations` - these are fields from `AggregateDtoAdapterBase`, not relationships
- **Relationships**: MUST be in both `includes` and `relations`

**How to identify:** Check the DTO adapter (`{Entity}DtoAdapter.kt`) - fields with `config.relationship()` are relationships, fields with `config.field()` are attributes. The base class `AggregateDtoAdapterBase` configures `owner` and `tenant` as fields (attributes), not relationships. Alternatively, check the JSONAPI response: `relationships` section = relationships, `attributes` = attributes/code tables/base fields.

### 2. Display-Only Fields (Schema Metadata)

Use the `displayOnly()` helper to mark form fields that should be displayed but not submitted:

```typescript
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export const {entity}FormSchema = z.object({
  name: z.string().min(1, "Name ist erforderlich"),
  status: enumeratedSchema,  // shared schema for Enumerated fields
  // Display-only: automatically excluded from submission
  contacts: displayOnly(z.array(z.any()).optional()),
});
```

**How it works:**
- `displayOnly()` wraps a Zod schema and adds metadata via `.describe()`
- `transformFromForm()` automatically detects and excludes these fields
- No need to pass `displayOnlyFields` to `useEditableEntity` - it's detected from schema metadata

### 3. Readonly Array in queryKey

`ItemsPage` expects `string[]` but query keys are `readonly`. Spread to convert:

```typescript
queryKey={[...{entity}Keys.lists()]}  // ✓ Correct
queryKey={{entity}Keys.lists()}       // ✗ Type error
```

### 4. Related Panel Components

`NotesList`, `TasksList`, and `ActivityTimeline` are presentational components. They expect data arrays, not entity IDs. Pass empty arrays for stubs:

```typescript
<NotesList notes={[] as Note[]} />
<TasksList tasks={[] as Task[]} />
<ActivityTimeline activities={[] as Activity[]} />
```

### 5. Relative Imports

The project uses relative imports, not path aliases. Import paths are relative to the file location.

### 6. Form Components

Use `Af*` components from `common/components/form`:
- `AfForm` - Form wrapper (combines FormProvider + Ant Form + optional HTML form)
- `AfInput` - Text input
- `AfTextArea` - Multi-line text
- `AfNumber` - Numeric input with precision/suffix support
- `AfSelect` - Dropdown with `source` prop for code tables or `options` for manual options
- `AfDatePicker` - Date selection
- `AfCheckbox` - Boolean checkbox
- `AfFieldRow` - Horizontal field container
- `AfFieldGroup` - Fieldset with legend

### 7. Grid System (24-Column)

The AF form components use a **24-column grid system** (consistent with Ant Design's `Col` component). The `size` prop determines field width:

| Size | Width | Use Case |
|------|-------|----------|
| 6    | 25%   | Quarter width |
| 8    | 33%   | Third width |
| 12   | 50%   | Half width |
| 18   | 75%   | Three-quarters |
| 24   | 100%  | Full width (default) |

```typescript
<AfFieldRow>
  <AfInput name="code" label="Code" size={6} />      {/* 25% */}
  <AfInput name="name" label="Name" size={18} />     {/* 75% */}
</AfFieldRow>
```

### 8. Styling with Design Tokens and CSS Classes

**Prefer CSS utility classes over inline styles.** The application uses a design system with:

- **CSS utility classes** in `global.css` (e.g., `.af-mb-16`, `.af-loading-inline`, `.af-flex-center`)
- **`useStyles()` hook** from `common/hooks/useStyles` for theme-aware style patterns

**Available CSS utility classes:**

- Layout: `.af-flex`, `.af-flex-column`, `.af-flex-center`, `.af-flex-between`, `.af-flex-end`
- Spacing: `.af-mb-0`, `.af-mb-4`, `.af-mb-8`, `.af-mb-16`, `.af-mb-24`, `.af-ml-*`, `.af-p-*`, `.af-p-48`
- Loading: `.af-loading-container` (full-height), `.af-loading-inline` (with padding)
- Cards: `.af-card-header`, `.af-card-header-connected`, `.af-card-body-connected`
- Preview: `.af-preview-container`, `.af-preview-avatar`, `.af-preview-avatar-placeholder`, `.af-preview-avatar-image`, `.af-preview-name`, `.af-preview-name-text`, `.af-preview-description-label`, `.af-preview-description-text`, `.af-preview-actions`
- Full height: `.af-full-height`

### 9. Permission Checks

Use the shared permission utilities from `common/utils/permissions`:

```typescript
import { canModifyEntity, canCreateEntity } from "../../../common/utils";

// In Area component (needs userRole and tenantType from sessionStore)
const canCreate = canCreateEntity("account", userRole, tenantType);

// In Page component
const canEdit = canModifyEntity("account", userRole);
```

**When migrating a new entity:** Add the entity's permission logic to `common/utils/permissions.ts`. Check the fm-ui area's `canCreate` condition (e.g., in `{Entity}Area.tsx`) and translate it to the new permission functions. For example, Account in fm-ui uses `session.isAdmin && (session.isKernelTenant || session.isAdvisorTenant)` which maps to the `canCreateEntity("account", ...)` case.

### 10. Tenant Handling

**Tenant fields are NOT shown in forms.** The tenant is already displayed in the page header (readonly) and the server handles setting the tenant automatically. Do not include tenant fields in:
- Creation forms (server sets tenant based on session)
- Main forms (shown in header, not editable)

This simplifies forms significantly by removing conditional tenant logic.

### 11. Navigation Components

Use TanStack Router's `<Link>` component instead of `<a onClick>` for navigation:

```typescript
// ✗ Bad - accessibility issues
<a onClick={() => navigate({ to: "/account" })}>Back to list</a>

// ✓ Good - proper link semantics
import { Link } from "@tanstack/react-router";
<Link to="/account">Back to list</Link>
```

### 12. Query Key Factory

Always use the query key factory instead of literal arrays:

```typescript
// ✗ Bad - hardcoded array
queryKey: ["account"]

// ✓ Good - use factory
import { accountKeys } from "../queries";
queryKey: accountKeys.all
```

## Verification Checklist

After implementing, verify:

1. **TypeScript**: `pnpm tsc --noEmit`
2. **Linting**: `pnpm lint` (fix with `pnpm lint:fix`)
3. **Build**: `pnpm build`
4. **Tests**: `pnpm test:run`

## Reference Files

**Account implementation (canonical example):**
- `fm-ux/src/areas/account/` - Complete area implementation

**Common components:**
- `fm-ux/src/common/components/form/` - Form components (AfForm, AfInput, etc.)
- `fm-ux/src/common/components/items/` - ItemsPage, ItemPageHeader, EditControls
- `fm-ux/src/common/components/related/` - RelatedPanel, NotesList, TasksList

**Utilities:**
- `fm-ux/src/common/utils/zodMeta.ts` - enumeratedSchema, displayOnly helper
- `fm-ux/src/common/utils/formTransformers.ts` - entity ↔ form conversion
- `fm-ux/src/common/utils/permissions.ts` - canModifyEntity, canCreateEntity helpers
- `fm-ux/src/common/api/entityApi.ts` - createEntityApi factory

**Styling:**
- `fm-ux/src/styles/global.css` - CSS utility classes
- `fm-ux/src/common/hooks/useStyles.ts` - Theme-aware styles
