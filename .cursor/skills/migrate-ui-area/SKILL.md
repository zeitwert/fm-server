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

### 1. Types (`types.ts`)

```typescript
import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface {Entity} {
  id: string;
  meta?: EntityMeta;
  
  name: string;
  description?: string;
  status: Enumerated;
  owner: Enumerated;
  tenant: Enumerated;
}

export interface {Entity}ListItem {
  id: string;
  name: string;
  status: Enumerated;
  owner: Enumerated;
}

export interface {Entity}FormInput {
  name: string;
  description?: string | null;
  status: Enumerated | null;
  owner: Enumerated | null;
  tenant: Enumerated | null;
}
```

### 2. Schemas (`schemas.ts`)

Create Zod schemas for form validation. Avoid `.refine()` on nullable schemas due to Zod 4 compatibility; validate required fields at submit time instead.

```typescript
import { z } from "zod";
import type { Enumerated } from "../../common/types";

const enumeratedSchema = z
  .object({
    id: z.string(),
    name: z.string(),
  })
  .nullable();

export interface {Entity}FormInput {
  name: string;
  description?: string | null;
  status: Enumerated | null;
  owner: Enumerated | null;
}

export const {entity}FormSchema = z.object({
  name: z.string().min(1, "Name ist erforderlich"),
  description: z.string().optional().nullable(),
  status: enumeratedSchema,
  owner: enumeratedSchema,
});

export type {Entity}FormData = z.infer<typeof {entity}FormSchema>;
```

### 3. API (`api.ts`)

Use the `createEntityApi` factory for CRUD operations. Only relationships go in `includes` and `relations`; code tables come inline as attributes.

```typescript
import { createEntityApi } from "../../common/api/entityApi";
import type { {Entity}, {Entity}ListItem } from "./types";

export const {entity}Api = createEntityApi<{Entity}>({
  module: "{module}",
  path: "{entities}",
  type: "{entity}",
  includes: "include[{entity}]=mainContact",
  relations: {
    mainContact: "contact",
  },
});

export const {entity}ListApi = createEntityApi<{Entity}ListItem>({
  module: "{module}",
  path: "{entities}",
  type: "{entity}",
  includes: "include[{entity}]=mainContact",
  relations: {
    mainContact: "contact",
  },
});
```

### 4. Query Hooks (`queries.ts`)

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

```typescript
import { {Icon}Outlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { {entity}ListApi } from "../api";
import { {entity}Keys } from "../queries";
import { {Entity}CreationForm } from "./forms/{Entity}CreationForm";
import { {Entity}Preview } from "./{Entity}Preview";
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
      queryKey={[...{entity}Keys.lists()]}
      queryFn={() => {entity}ListApi.list()}
      columns={columns}
      canCreate={canCreate(userRole)}
      CreateForm={{Entity}CreationForm}
      PreviewComponent={{Entity}Preview}
      getDetailPath={(record) => `/{entity}/${record.id}`}
    />
  );
}
```

### 6. Preview Panel (`ui/{Entity}Preview.tsx`) - Optional

When `PreviewComponent` is provided to `ItemsPage`, an eye icon column appears after the first column. Clicking it opens a preview drawer; clicking elsewhere on the row navigates to the detail page.

The preview component receives `id` and `onClose` props and should:
- Fetch the full entity data using the query hook
- Display a summary of key information (logo/image, name, key fields)
- Provide an "Edit" button that navigates to the detail page

```typescript
import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography, theme } from "antd";
import { {Icon}Outlined, EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { use{Entity} } from "../queries";
import { getLogoUrl } from "../../../common/api/client";

const { Text, Paragraph } = Typography;
const { useToken } = theme;

interface {Entity}PreviewProps {
  id: string;
  onClose: () => void;
}

export function {Entity}Preview({ id, onClose }: {Entity}PreviewProps) {
  const { t } = useTranslation("{entity}");
  const { t: tc } = useTranslation("common");
  const navigate = useNavigate();
  const { token } = useToken();
  const [logoError, setLogoError] = useState(false);

  const { data: entity, isLoading, isError } = use{Entity}(id);

  // Reset logo error when entity changes
  useEffect(() => {
    setLogoError(false);
  }, [id]);

  const handleEdit = () => {
    onClose();
    navigate({ to: `/{entity}/${id}` });
  };

  if (isLoading) {
    return (
      <div style={{ display: "flex", justifyContent: "center", padding: 40 }}>
        <Spin />
      </div>
    );
  }

  if (isError || !entity) {
    return <Result status="error" title={t("notFound")} />;
  }

  // Use getLogoUrl if entity has a logo, otherwise show placeholder
  const logoUrl = getLogoUrl("{entity}", id);

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
      {/* Logo/Image - show placeholder if no logo or error */}
      <div style={{ display: "flex", justifyContent: "center", padding: 16 }}>
        {!logoError ? (
          <img
            src={logoUrl}
            alt={entity.name}
            style={{
              width: 120,
              height: 120,
              borderRadius: 8,
              objectFit: "contain",
              border: `1px solid ${token.colorBorderSecondary}`,
              background: token.colorBgLayout,
            }}
            onError={() => setLogoError(true)}
          />
        ) : (
          <div
            style={{
              width: 120,
              height: 120,
              borderRadius: 8,
              border: `1px solid ${token.colorBorderSecondary}`,
              background: token.colorBgLayout,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <{Icon}Outlined style={{ fontSize: 48, color: token.colorTextQuaternary }} />
          </div>
        )}
      </div>

      {/* Name */}
      <div style={{ textAlign: "center" }}>
        <Text strong style={{ fontSize: 18 }}>
          {entity.name}
        </Text>
      </div>

      {/* Details */}
      <Descriptions column={1} size="small">
        <Descriptions.Item label={t("status")}>
          {entity.status?.name || "-"}
        </Descriptions.Item>
        <Descriptions.Item label={t("owner")}>
          {entity.owner?.name || "-"}
        </Descriptions.Item>
      </Descriptions>

      {/* Description (if exists) */}
      {entity.description && (
        <div>
          <Text type="secondary" style={{ fontSize: 12 }}>
            {t("description")}
          </Text>
          <Paragraph
            style={{ marginTop: 4, marginBottom: 0 }}
            ellipsis={{ rows: 3, expandable: true }}
          >
            {entity.description}
          </Paragraph>
        </div>
      )}

      {/* Actions */}
      <Space style={{ marginTop: 8 }}>
        <Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
          {tc("edit")}
        </Button>
      </Space>
    </div>
  );
}
```

**Key points:**
- The `getLogoUrl()` function from `common/api/client` builds the logo URL for entities that have a logo relationship
- Use `onError` on the image to fall back to a placeholder icon when no logo exists
- The "Edit" button should call `onClose()` before navigating to prevent the drawer from staying open
- You can disable the preview column by passing `showPreviewColumn={false}` to `ItemsPage`

### 7. Detail View (`ui/{Entity}Page.tsx`)

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
      <div className="af-loading-inline">
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

### 8. Forms (`ui/forms/`)

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
      <Card size="small" title={t("basicInfo")} className="af-mb-16">
        <AfInput name="name" label={t("name")} required readOnly={disabled} />
        <AfTextArea name="description" label={t("description")} rows={4} readOnly={disabled} />
      </Card>

      <Card size="small" title={t("classification")} className="af-mb-16">
        <AfFieldRow>
          <AfSelect
            name="status"
            label={t("status")}
            source="{entity}/code{Entity}Status"
            required
            readOnly={disabled}
            size={12}
          />
          <AfSelect
            name="owner"
            label={t("owner")}
            source="oe/objUser"
            required
            readOnly={disabled}
            size={12}
          />
        </AfFieldRow>
      </Card>
    </div>
  );
}
```

#### Creation Form (`{Entity}CreationForm.tsx`)

Skip `zodResolver` for creation forms and validate manually at submit time.

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

        <div className="af-flex-end af-mt-24">
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

### 9. Routes

TanStack Router uses file-based routing. Create three route files:

1. **Layout Route** (`{entity}.tsx`) - Renders `<Outlet />` for child routes
2. **Index Route** (`{entity}.index.tsx`) - List view at `/{entity}`
3. **Detail Route** (`{entity}.${entityId}.tsx`) - Detail view at `/{entity}/123`

#### Layout Route (`routes/{entity}.tsx`)

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

```typescript
import { createFileRoute } from "@tanstack/react-router";
import { {Entity}Area } from "../areas/{entity}/ui/{Entity}Area";

export const Route = createFileRoute("/{entity}/")({
  component: {Entity}Area,
});
```

#### Detail Route (`routes/{entity}.${{entity}Id}.tsx`)

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

### 10. Translations

#### `i18n/locales/de/{entity}.json` and `i18n/locales/en/{entity}.json`

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

Add imports, add to resources, and add `"{entity}"` to the `ns` array.

## Common Patterns & Gotchas

### 1. Code Tables vs Relationships (CRITICAL)

**Common mistake**: Adding code tables or base fields to `relations` causes errors like `"Invalid relationship name: owner for account"`.

**Rule:**
- **Code tables**: NOT in `includes` or `relations` (they're attributes)
- **Base fields** (`owner`, `tenant`): NOT in `includes` or `relations` - these are fields from `AggregateDtoAdapterBase`, not relationships
- **Relationships**: MUST be in both `includes` and `relations`

**How to identify:** Check the DTO adapter (`{Entity}DtoAdapter.kt`) - fields with `config.relationship()` are relationships, fields with `config.field()` are attributes. The base class `AggregateDtoAdapterBase` configures `owner` and `tenant` as fields (attributes), not relationships. Alternatively, check the JSONAPI response: `relationships` section = relationships, `attributes` = attributes/code tables/base fields.

### 2. Readonly Array in queryKey

`ItemsPage` expects `string[]` but query keys are `readonly`. Spread to convert:

```typescript
queryKey={[...{entity}Keys.lists()]}  // ✓ Correct
queryKey={{entity}Keys.lists()}       // ✗ Type error
```

### 3. Related Panel Components

`NotesList`, `TasksList`, and `ActivityTimeline` are presentational components. They expect data arrays, not entity IDs. Pass empty arrays for stubs:

```typescript
<NotesList notes={[] as Note[]} />
<TasksList tasks={[] as Task[]} />
<ActivityTimeline activities={[] as Activity[]} />
```

### 4. Relative Imports

The project uses relative imports, not path aliases. Import paths are relative to the file location.

### 5. Form Components

Use `Af*` components from `common/components/form` (see form examples above).

### 6. Grid System (24-Column)

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

### 7. Styling with Design Tokens and CSS Classes

**Prefer CSS utility classes over inline styles.** The application uses a design system with:

- **CSS utility classes** in `global.css` (e.g., `.af-mb-16`, `.af-loading-inline`, `.af-flex-center`)
- **`useStyles()` hook** from `common/hooks/useStyles` for theme-aware style patterns

**Common patterns:**

```typescript
// ✓ Correct: Use CSS class for margin
<Card size="small" title={t("basicInfo")} className="af-mb-16">

// ✗ Avoid: Inline style
<Card size="small" title={t("basicInfo")} style={{ marginBottom: 16 }}>

// ✓ Correct: Use CSS class for loading state
<div className="af-loading-inline">
  <Spin size="large" />
</div>

// ✗ Avoid: Inline style
<div style={{ display: "flex", justifyContent: "center", padding: 48 }}>
  <Spin size="large" />
</div>

// ✓ Correct: Use CSS class for button row
<div className="af-flex-end af-mt-24">
  <Space>...</Space>
</div>
```

**Available CSS utility classes:**

- Layout: `.af-flex`, `.af-flex-column`, `.af-flex-center`, `.af-flex-between`, `.af-flex-end`
- Spacing: `.af-mb-0`, `.af-mb-4`, `.af-mb-8`, `.af-mb-16`, `.af-mb-24`, `.af-ml-*`, `.af-p-*`
- Loading: `.af-loading-container` (full-height), `.af-loading-inline` (with padding)
- Cards: `.af-card-header`, `.af-card-header-connected`, `.af-card-body-connected`

**When to use `useStyles()` hook:**

Use the hook when you need theme-aware styles that depend on Ant Design tokens:

```typescript
import { useStyles } from "../../../common/hooks/useStyles";

function MyComponent() {
  const { styles, token } = useStyles();
  
  // Use token for dynamic colors
  return <div style={{ color: token.colorPrimary }}>...</div>;
  
  // Or use pre-built patterns
  return <Typography.Text style={styles.readonlyField}>...</Typography.Text>;
}
```

### 8. Permission Checks

Use role constants from `session/model/types`:

```typescript
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

function canEdit(role?: string): boolean {
  return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}
```

### 9. Code Comments

Avoid superfluous comments. The code should be self-documenting. Do not add file headers, interface comments, or inline comments that restate what the code already shows. Only comment when explaining *why*, not *what*.

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
- **Design system**: `fm-ux/src/styles/global.css` (CSS utility classes)
- **Style hooks**: `fm-ux/src/common/hooks/useStyles.ts` (theme-aware styles)
- **Style constants**: `fm-ux/src/common/styles/constants.ts` (dynamic style builders)
