---
name: Migrate UI Area
description: Migrate or create a new aggregate area in the fm-ux application.
---

# Implementing an Entity Area in fm-ux

This cookbook describes how to migrate or create a new entity area in the fm-ux application. It covers the complete implementation from types to UI components.

## Overview

An entity area typically consists of:
- **List View** (`{Entity}Area`) - Table with create modal using `ItemsPage`
- **Detail View** (`{Entity}Page`) - Edit form with `useEditableEntity` hook
- **API Layer** - CRUD operations using `createEntityApi`
- **Query Hooks** - TanStack Query hooks for data fetching
- **Routes** - TanStack Router file-based routes

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

### 1. Types (`types.ts`)

Define the entity types based on the existing fm-ui model.

```typescript
import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface {Entity} {
  id: string;
  meta?: EntityMeta;
  
  // Basic fields
  name: string;
  description?: string;
  
  // Code tables (Enumerated)
  status: Enumerated;
  
  // Relations (also Enumerated for references)
  owner: Enumerated;
  tenant: Enumerated;
}

// Lighter type for list view (fewer relations loaded)
export interface {Entity}ListItem {
  id: string;
  name: string;
  status: Enumerated;
  owner: Enumerated;
}

// Form input type (allows nulls for unselected dropdowns)
export interface {Entity}FormInput {
  name: string;
  description?: string | null;
  status: Enumerated | null;
  owner: Enumerated | null;
  tenant: Enumerated | null;
}
```

### 2. Schemas (`schemas.ts`)

Create Zod schemas for form validation.

> **Important**: Due to Zod 4 compatibility issues with `@hookform/resolvers`, avoid using `.refine()` on nullable schemas. Validate required fields at submit time instead.

```typescript
import { z } from "zod";
import type { Enumerated } from "../../common/types";

const enumeratedSchema = z
  .object({
    id: z.string(),
    name: z.string(),
  })
  .nullable();

// Form input type (exported for useForm generic)
export interface {Entity}FormInput {
  name: string;
  description?: string | null;
  status: Enumerated | null;
  owner: Enumerated | null;
}

// Schema without .refine() for Zod 4 compatibility
export const {entity}FormSchema = z.object({
  name: z.string().min(1, "Name ist erforderlich"),
  description: z.string().optional().nullable(),
  status: enumeratedSchema,
  owner: enumeratedSchema,
});

export type {Entity}FormData = z.infer<typeof {entity}FormSchema>;
```

### 3. API (`api.ts`)

Use the `createEntityApi` factory for CRUD operations.

```typescript
import { createEntityApi } from "../../common/api/entityApi";
import type { {Entity}, {Entity}ListItem } from "./types";

export const {entity}Api = createEntityApi<{Entity}>({
  module: "{module}",           // API module (e.g., "building", "account")
  path: "{entities}",           // API path (e.g., "buildings", "accounts")
  type: "{entity}",             // JSONAPI type
  includes: "include[{entity}]=owner,status,tenant",  // Relations to include
  relations: {
    owner: "user",
    status: "{entity}Status",
    tenant: "tenant",
  },
});

// Lighter API for list view
export const {entity}ListApi = createEntityApi<{Entity}ListItem>({
  module: "{module}",
  path: "{entities}",
  type: "{entity}",
  includes: "include[{entity}]=owner,status",
  relations: {
    owner: "user",
    status: "{entity}Status",
  },
});
```

### 4. Query Hooks (`queries.ts`)

Create TanStack Query hooks for data fetching.

```typescript
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { {entity}Api, {entity}ListApi } from "./api";
import type { {Entity} } from "./types";
import type { EntityMeta } from "../../common/api/jsonapi";

export const {entity}Keys = {
  all: ["{entity}"] as const,
  lists: () => [...{entity}Keys.all, "list"] as const,
  list: (params?: string) => [...{entity}Keys.lists(), params] as const,
  details: () => [...{entity}Keys.all, "detail"] as const,
  detail: (id: string) => [...{entity}Keys.details(), id] as const,
};

export function use{Entity}List() {
  return useQuery({
    queryKey: {entity}Keys.lists(),
    queryFn: () => {entity}ListApi.list(),
  });
}

export function use{Entity}(id: string) {
  return useQuery({
    queryKey: {entity}Keys.detail(id),
    queryFn: () => {entity}Api.get(id),
    enabled: !!id,
  });
}

export function useCreate{Entity}() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Omit<{Entity}, "id">) => {entity}Api.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: {entity}Keys.lists() });
      message.success("{Entity} erstellt");
    },
    onError: (error: Error & { detail?: string }) => {
      message.error(error.detail || `Fehler: ${error.message}`);
    },
  });
}

export function useUpdate{Entity}() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<{Entity}> & { id: string; meta?: EntityMeta }) =>
      {entity}Api.update(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: {entity}Keys.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: {entity}Keys.lists() });
      message.success("{Entity} gespeichert");
    },
    onError: (error: Error & { detail?: string }) => {
      message.error(error.detail || `Fehler: ${error.message}`);
    },
  });
}
```

### 5. List View (`ui/{Entity}Area.tsx`)

Use the `ItemsPage` component for the list view.

```typescript
import { {Icon}Outlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { {entity}ListApi } from "../api";
import { {entity}Keys } from "../queries";
import { {Entity}CreationForm } from "./forms/{Entity}CreationForm";
import type { {Entity}ListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

function canCreate(role?: string): boolean {
  return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

export function {Entity}Area() {
  const { t } = useTranslation("{entity}");
  const { sessionInfo } = useSessionStore();
  const userRole = sessionInfo?.user?.role?.id;

  const columns: ColumnType<{Entity}ListItem>[] = [
    {
      title: t("name"),
      dataIndex: "name",
      key: "name",
      sorter: (a, b) => a.name.localeCompare(b.name),
      defaultSortOrder: "ascend",
    },
    {
      title: t("status"),
      dataIndex: ["status", "name"],
      key: "status",
    },
    {
      title: t("owner"),
      dataIndex: ["owner", "name"],
      key: "owner",
    },
  ];

  return (
    <ItemsPage<{Entity}ListItem>
      entityType="{entity}"
      entityLabel={t("{entities}")}
      entityLabelSingular={t("{entity}")}
      icon={<{Icon}Outlined />}
      queryKey={[...{entity}Keys.lists()]}  // Spread to convert readonly to mutable
      queryFn={() => {entity}ListApi.list()}
      columns={columns}
      canCreate={canCreate(userRole)}
      CreateForm={{Entity}CreationForm}
      getDetailPath={(record) => `/{entity}/${record.id}`}
    />
  );
}
```

### 6. Detail View (`ui/{Entity}Page.tsx`)

Use the `useEditableEntity` hook for the detail page.

```typescript
import { Card, Spin, Result, Tabs } from "antd";
import { {Icon}Outlined } from "@ant-design/icons";
import { FormProvider } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { useEditableEntity } from "../../../common/hooks/useEditableEntity";
import { ItemPageHeader, ItemPageLayout, EditControls } from "../../../common/components/items";
import { RelatedPanel } from "../../../common/components/related";
import { NotesList } from "../../../common/components/related/NotesList";
import { TasksList } from "../../../common/components/related/TasksList";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Note } from "../../../common/components/related/NotesList";
import type { Task } from "../../../common/components/related/TasksList";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { {entity}Api } from "../api";
import { {entity}FormSchema, type {Entity}FormInput } from "../schemas";
import { {Entity}MainForm } from "./forms/{Entity}MainForm";
import type { {Entity} } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";
import type { Enumerated } from "../../../common/types";

interface {Entity}PageProps {
  {entity}Id: string;
}

function canEdit(role?: string): boolean {
  return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

function transformToForm(entity: {Entity}): {Entity}FormInput {
  return {
    name: entity.name,
    description: entity.description ?? "",
    status: entity.status,
    owner: entity.owner,
  };
}

function transformFromForm(formData: Partial<{Entity}FormInput>): Partial<{Entity}> {
  const result: Partial<{Entity}> = {};
  if (formData.name !== undefined) result.name = formData.name;
  if (formData.description !== undefined) result.description = formData.description || undefined;
  if (formData.status !== undefined) result.status = formData.status as Enumerated;
  if (formData.owner !== undefined) result.owner = formData.owner as Enumerated;
  return result;
}

export function {Entity}Page({ {entity}Id }: {Entity}PageProps) {
  const { t } = useTranslation("{entity}");
  const navigate = useNavigate();
  const { sessionInfo } = useSessionStore();
  const userRole = sessionInfo?.user?.role?.id;

  const {
    entity,
    form,
    isLoading,
    isError,
    isEditing,
    isDirty,
    isStoring,
    handleEdit,
    handleCancel,
    handleStore,
  } = useEditableEntity<{Entity}, {Entity}FormInput>({
    id: {entity}Id,
    queryKey: ["{entity}"],
    queryFn: (id) => {entity}Api.get(id),
    updateFn: {entity}Api.update,
    schema: {entity}FormSchema,
    transformToForm,
    transformFromForm,
  });

  if (isLoading) {
    return (
      <div style={{ display: "flex", justifyContent: "center", padding: 48 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (isError || !entity) {
    return (
      <Result
        status="404"
        title={t("notFound")}
        subTitle={t("notFoundDescription")}
        extra={<a onClick={() => navigate({ to: "/{entity}" })}>{t("backToList")}</a>}
      />
    );
  }

  return (
    <>
      <ItemPageHeader
        icon={<{Icon}Outlined />}
        entityLabel={t("{entity}")}
        title={entity.name}
        subtitle={entity.status?.name}
        details={[
          { label: t("owner"), content: entity.owner?.name },
        ]}
        actions={
          <EditControls
            isEditing={isEditing}
            isDirty={isDirty}
            isStoring={isStoring}
            canEdit={canEdit(userRole)}
            onEdit={handleEdit}
            onCancel={handleCancel}
            onStore={handleStore}
          />
        }
      />

      <ItemPageLayout
        rightPanel={
          <RelatedPanel
            sections={[
              { key: "notes", label: "Notizen", children: <NotesList notes={[] as Note[]} /> },
              { key: "tasks", label: "Aufgaben", children: <TasksList tasks={[] as Task[]} /> },
              { key: "activity", label: "Aktivität", children: <ActivityTimeline activities={[] as Activity[]} /> },
            ]}
          />
        }
      >
        <Card>
          <FormProvider {...form}>
            <Tabs
              items={[
                {
                  key: "main",
                  label: t("tabMain"),
                  children: <{Entity}MainForm disabled={!isEditing} />,
                },
              ]}
            />
          </FormProvider>
        </Card>
      </ItemPageLayout>
    </>
  );
}
```

### 7. Forms (`ui/forms/`)

#### Main Form (`{Entity}MainForm.tsx`)

```typescript
import { Card } from "antd";
import { useTranslation } from "react-i18next";
import { AfInput, AfTextArea, AfSelect, AfFieldRow } from "../../../../common/components/form";

interface {Entity}MainFormProps {
  disabled: boolean;
}

export function {Entity}MainForm({ disabled }: {Entity}MainFormProps) {
  const { t } = useTranslation("{entity}");

  return (
    <div>
      <Card size="small" title={t("basicInfo")} style={{ marginBottom: 16 }}>
        <AfInput name="name" label={t("name")} required readOnly={disabled} />
        <AfTextArea name="description" label={t("description")} rows={4} readOnly={disabled} />
      </Card>

      <Card size="small" title={t("classification")} style={{ marginBottom: 16 }}>
        <AfFieldRow>
          <AfSelect
            name="status"
            label={t("status")}
            source="{entity}/code{Entity}Status"
            required
            readOnly={disabled}
            size={6}
          />
          <AfSelect
            name="owner"
            label={t("owner")}
            source="oe/objUser"
            required
            readOnly={disabled}
            size={6}
          />
        </AfFieldRow>
      </Card>
    </div>
  );
}
```

#### Creation Form (`{Entity}CreationForm.tsx`)

> **Important**: Due to Zod 4 compatibility issues, skip `zodResolver` for creation forms and validate manually at submit time.

```typescript
import { Button, Space, message } from "antd";
import { FormProvider, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { AfInput, AfTextArea, AfSelect } from "../../../../common/components/form";
import { useCreate{Entity} } from "../../queries";
import type { {Entity}FormInput } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";

export function {Entity}CreationForm({ onSuccess, onCancel }: CreateFormProps) {
  const { t } = useTranslation("{entity}");
  const { t: tCommon } = useTranslation("common");
  const createMutation = useCreate{Entity}();

  const form = useForm<{Entity}FormInput>({
    defaultValues: {
      name: "",
      description: "",
      status: null,
      owner: null,
    },
  });

  const handleSubmit = form.handleSubmit(async (data) => {
    // Validate required fields manually (Zod 4 compatibility)
    let hasError = false;
    if (!data.name?.trim()) {
      form.setError("name", { message: "Name ist erforderlich" });
      hasError = true;
    }
    if (!data.status) {
      form.setError("status", { message: "Status ist erforderlich" });
      hasError = true;
    }
    if (hasError) {
      message.error("Bitte füllen Sie alle Pflichtfelder aus");
      return;
    }

    await createMutation.mutateAsync({
      name: data.name,
      description: data.description,
      status: data.status!,
      owner: data.owner!,
    });
    onSuccess();
  });

  return (
    <FormProvider {...form}>
      <form onSubmit={handleSubmit}>
        <AfInput name="name" label={t("name")} required />
        <AfSelect name="status" label={t("status")} source="{entity}/code{Entity}Status" required />
        <AfSelect name="owner" label={t("owner")} source="oe/objUser" required />
        <AfTextArea name="description" label={t("description")} rows={3} />

        <div style={{ marginTop: 24, textAlign: "right" }}>
          <Space>
            <Button onClick={onCancel}>{tCommon("cancel")}</Button>
            <Button type="primary" htmlType="submit" loading={createMutation.isPending}>
              {tCommon("create")}
            </Button>
          </Space>
        </div>
      </form>
    </FormProvider>
  );
}
```

### 8. Routes

TanStack Router uses file-based routing with dot notation for nested routes. For entity areas with list and detail views, you need **three** route files:

1. **Layout Route** (`{entity}.tsx`) - Renders `<Outlet />` to pass through to child routes
2. **Index Route** (`{entity}.index.tsx`) - The list view at `/{entity}`
3. **Detail Route** (`{entity}.${entityId}.tsx`) - The detail view at `/{entity}/123`

> **Important**: If you only create `{entity}.tsx` with the list component and `{entity}.$id.tsx` for details, the detail route won't render. This is because in TanStack Router, `{entity}.$id.tsx` is a **nested child** of `{entity}.tsx`. The parent must render `<Outlet />` for children to display.

#### Layout Route (`routes/{entity}.tsx`)

This is a simple pass-through layout that renders children:

```typescript
import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/{entity}")({
  component: {Entity}Layout,
});

function {Entity}Layout() {
  return <Outlet />;
}
```

#### Index Route (`routes/{entity}.index.tsx`)

This renders the list view at `/{entity}`:

```typescript
import { createFileRoute } from "@tanstack/react-router";
import { {Entity}Area } from "../areas/{entity}/ui/{Entity}Area";

export const Route = createFileRoute("/{entity}/")({
  component: {Entity}Area,
});
```

#### Detail Route (`routes/{entity}.${{entity}Id}.tsx`)

This renders the detail view at `/{entity}/$id`:

```typescript
import { createFileRoute } from "@tanstack/react-router";
import { {Entity}Page } from "../areas/{entity}/ui/{Entity}Page";

export const Route = createFileRoute("/{entity}/${{entity}Id}")({
  component: {Entity}PageRoute,
});

function {Entity}PageRoute() {
  const { {entity}Id } = Route.useParams();
  return <{Entity}Page {entity}Id={{entity}Id} />;
}
```

### 9. Translations

Create translation files for both German and English:

#### `i18n/locales/de/{entity}.json`

```json
{
  "{entity}": "{Entity}",
  "{entities}": "{Entities}",
  "name": "Name",
  "description": "Beschreibung",
  "status": "Status",
  "owner": "Verantwortlich",
  "basicInfo": "Allgemeine Informationen",
  "classification": "Klassifizierung",
  "tabMain": "Stammdaten",
  "notFound": "{Entity} nicht gefunden",
  "notFoundDescription": "Der angeforderte {Entity} konnte nicht gefunden werden.",
  "backToList": "Zurück zur Liste"
}
```

#### Update `i18n/index.ts`

```typescript
// Add imports
import en{Entity} from "./locales/en/{entity}.json";
import de{Entity} from "./locales/de/{entity}.json";

// Add to resources
const resources = {
  en: {
    // ...existing
    {entity}: en{Entity},
  },
  de: {
    // ...existing
    {entity}: de{Entity},
  },
};

// Add to ns array
ns: ["common", "login", "app", "home", "{entity}"],
```

## Common Patterns & Gotchas

### 1. Readonly Array in queryKey

`ItemsPage` expects `string[]` but query keys are `readonly`. Spread to convert:

```typescript
queryKey={[...{entity}Keys.lists()]}  // ✓ Correct
queryKey={{entity}Keys.lists()}       // ✗ Type error
```

### 2. Zod 4 Compatibility

The `@hookform/resolvers/zod` has compatibility issues with Zod 4. Workarounds:
- For `useEditableEntity`: The hook casts the resolver internally
- For creation forms: Skip zodResolver, validate manually at submit time

### 3. Related Panel Components

`NotesList`, `TasksList`, and `ActivityTimeline` are presentational components. They expect data arrays, not entity IDs. Pass empty arrays for stubs:

```typescript
<NotesList notes={[] as Note[]} />
<TasksList tasks={[] as Task[]} />
<ActivityTimeline activities={[] as Activity[]} />
```

### 4. Relative Imports

The project uses relative imports, not path aliases. Import paths are relative to the file location:

```typescript
// From areas/{entity}/ui/{Entity}Area.tsx
import { ItemsPage } from "../../../common/components/items";
import { useSessionStore } from "../../../session/model/sessionStore";
```

### 5. Route Generation & Nested Routes

TanStack Router generates routes automatically. After creating route files, run `pnpm dev` or `pnpm build` to trigger regeneration of `routeTree.gen.ts`.

**Nested route pattern**: Files with dot notation (e.g., `{entity}.$id.tsx`) create nested child routes under the parent (`{entity}.tsx`). For children to render, the parent **must** include `<Outlet />`. Use an index route (`{entity}.index.tsx`) for the list view:

```
routes/
├── account.tsx           # Layout with <Outlet />
├── account.index.tsx     # List view at /account
└── account.$accountId.tsx  # Detail view at /account/123
```

### 6. Form Components

Use `Af*` components from `common/components/form`:
- `AfInput` - Text input
- `AfTextArea` - Multi-line text
- `AfSelect` - Dropdown with code table source
- `AfNumber` - Numeric input with formatting
- `AfFieldRow` - Horizontal row layout (uses `size` prop for grid columns)
- `AfCheckbox`, `AfDatePicker`, etc.

### 7. Permission Checks

Use role constants from `session/model/types`:

```typescript
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

function canEdit(role?: string): boolean {
  return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}
```

## Verification Checklist

After implementing, verify:

1. **TypeScript**: `pnpm tsc --noEmit`
2. **Linting**: `pnpm lint` (fix with `pnpm lint:fix`)
3. **Build**: `pnpm build`
4. **Tests**: `pnpm test:run`

## Reference Files

- **ItemsPage design**: `.cursor/plans/itemspage_itempage_design_*.plan.md`
- **Account implementation**: `fm-ux/src/areas/account/` (reference implementation)
- **Form components**: `fm-ux/src/common/components/form/`
- **Item components**: `fm-ux/src/common/components/items/`
