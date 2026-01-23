---
name: Migrate UI Area
description: Migrate or create a new aggregate area in the fm-ux application.
---

# Implementing an Entity Area in fm-ux

This cookbook describes how to migrate or create a new entity area in the fm-ux application.

## Overview

An entity area consists of:
- **List View** (`{Entity}Area`) - Table with create modal using `ItemsPage`
- **Detail View** (`{Entity}Page`) - Edit form with `useEntityQueries` hook
- **API Layer** - CRUD operations using `createEntityApi`
- **Query Hooks** - TanStack Query hooks for data fetching
- **Routes** - TanStack Router file-based routes

## Pre-Flight Questions

Before starting implementation, gather this context:

1. **Is the area already registered in `AppConfig.ts`?**
   - Check `fm-ux/src/app/config/AppConfig.ts` for existing entry
   - Note: icon, label, permissions if present

2. **Does this area have any special characteristics?**
   - Is entity creation done from this area, or only from parent context?
   - Are there any unique permissions or workflow requirements?
   - Any unusual field types or relationships?

3. **What is the domain module name?**
   - Check the entity's location in `fm-domain/src/main/java/io/zeitwert/fm/{module}/`
   - This determines the `module` value in the API configuration

## Quick Discovery Checklist

Gather entity information from these sources (in order):

### 1. Check AppConfig.ts
**Location:** `fm-ux/src/app/config/AppConfig.ts`

Check if area is already registered. Note icon and permissions.

### 2. Find API Configuration
**Location:** `fm-ui/src/@zeitwert/ui-model/fm/{entity}/service/impl/{Entity}ApiImpl.ts`

Extract these values (copy exactly):
- `MODULE` → will be `module` in new `api.ts`
- `PATH` → will be `path`
- `TYPE` → will be `type`
- `INCLUDES` → will be `includes`
- `RELATIONS` → will be `relations`

**CRITICAL:** Only true relationships go in `includes` and `relations`. Code tables (like `accountType`) and base fields (like `owner`, `tenant`) are attributes, NOT relationships.

### 3. Find Entity Properties
**Location:** `fm-domain/src/main/java/io/zeitwert/fm/{module}/model/impl/{Entity}Impl.kt`

List all properties with their types:
- `baseProperty<String>` → `string`
- `baseProperty<Int>` → `number`
- `enumProperty<T>` → `Enumerated` (code table)
- `referenceProperty<T>` → `Enumerated` (relationship)

### 4. Find Form Fields and Validation
**Location:** `fm-ui/src/areas/{entity}/ui/forms/{Entity}Form.ts`

Map field types to TypeScript and UI components:

| Field Type | TypeScript Type | UI Component |
|------------|-----------------|--------------|
| `TextField` | `string` | `AfInput` / `AfTextArea` |
| `EnumeratedField` | `Enumerated` | `AfSelect` (code table or relationship) |
| `NumberField` | `number` | `AfNumber` |
| `DateField` | `string` (ISO) | `AfDatePicker` |
| `IdField` | `string` | (display only) |

**CRITICAL:** Note which fields are required, default values, and validation rules. These MUST be replicated exactly in your Zod schemas. The schema validation is the single source of truth - do not add manual validation in form submit handlers.

### 5. Find List Columns
**Location:** `fm-domain/src/main/resources/config/t0/{module}/datamarts/{entities}/layouts/default.json`

Extract the `header` array for list view columns. Note: nested values like `accountType` (which maps to `accountType.name` in data) should use `dataIndex: ["accountType", "name"]` in the column definition.

## File Checklist

Create these files for a full area migration:

### Area Files (`fm-ux/src/areas/{entity}/`)
- [ ] `types.ts` - Entity and list item type definitions
- [ ] `schemas.ts` - Zod validation schemas (creation + form)
- [ ] `api.ts` - Entity API configuration
- [ ] `queries.ts` - TanStack Query hooks
- [ ] `index.ts` - Module exports
- [ ] `ui/{Entity}Area.tsx` - List view with ItemsPage
- [ ] `ui/{Entity}Page.tsx` - Detail view with edit form
- [ ] `ui/{Entity}Preview.tsx` - Preview drawer (optional)
- [ ] `ui/forms/{Entity}MainForm.tsx` - Main form tab
- [ ] `ui/forms/{Entity}CreationForm.tsx` - Creation modal form

### Routes (`fm-ux/src/routes/`)
- [ ] `{entity}.tsx` - Layout route (renders `<Outlet />`)
- [ ] `{entity}.index.tsx` - List view route
- [ ] `{entity}.${entity}Id.tsx` - Detail view route

### i18n (`fm-ux/src/i18n/locales/`)
- [ ] `de/{entity}.json` - German translations
- [ ] `en/{entity}.json` - English translations

### Updates
- [ ] `AppConfig.ts` - Add area configuration (if not present)
- [ ] `i18n/index.ts` - Register namespace

## Templates

Copy-paste these templates and replace placeholders:
- `{Entity}` - PascalCase (e.g., `Contact`)
- `{entity}` - camelCase (e.g., `contact`)
- `{module}` - Domain module name (e.g., `crm`)
- `{ENTITY_ID_PARAM}` - camelCase with "Id" suffix (e.g., `contactId`)

### Template: `types.ts`

```typescript
import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface {Entity} {
	id: string;
	meta?: EntityMeta;
	// Add all entity fields here based on discovery
	name: string;
	// example: accountType: Enumerated;
	// example: owner: Enumerated;
	// example: tenant: Enumerated;
}

export interface {Entity}ListItem {
	id: string;
	// Add subset of fields for list view
	name: string;
	// example: accountType: Enumerated;
}
```

### Template: `schemas.ts`

**CRITICAL:** Copy form structure, field requirements, and default values from `fm-ui/src/areas/{entity}/ui/forms/{Entity}Form.ts`. The Zod schemas must match the old form's validation rules exactly. Use Zod validation as the single source of truth - do NOT add manual validation in submit handlers.

```typescript
import { z } from "zod";
import type { Enumerated } from "../../common/types";
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export interface {Entity}CreationFormInput {
	// Add fields needed for creation (minimal set)
	// Copy from fm-ui {Entity}Form.ts - check which fields are in the creation dialog
	name: string;
	// example: accountType: Enumerated | null;
	// example: owner: Enumerated | null;
}

export const {entity}CreationSchema = z.object({
	// Copy required/optional flags from fm-ui {Entity}Form.ts
	name: z.string().min(1, "{entity}:message.validation.nameRequired"),
	// For required Enumerated fields, use enumeratedSchema (validates non-null)
	// example: accountType: enumeratedSchema,
	// example: owner: enumeratedSchema,
	// For optional Enumerated fields, use .optional().nullable()
	// example: clientSegment: enumeratedSchema.optional().nullable(),
});

export type {Entity}CreationData = z.infer<typeof {entity}CreationSchema>;

export interface {Entity}FormInput {
	// All editable fields
	// Copy from fm-ui {Entity}Form.ts - check all fields in the form
	name: string;
	// example: description?: string | null;
	// example: accountType: Enumerated | null;
	// Display-only fields (excluded from submission)
	// example: contacts?: any[];
}

export const {entity}FormSchema = z.object({
	// Copy required/optional flags from fm-ui {Entity}Form.ts
	name: z.string().min(1, "{entity}:message.validation.nameRequired"),
	// Add validation for other editable fields
	// example: description: z.string().optional().nullable(),
	// example: accountType: enumeratedSchema,
	// Display-only fields (wrapped with displayOnly helper)
	// example: contacts: displayOnly(z.array(z.any()).optional()),
});

export type {Entity}FormData = z.infer<typeof {entity}FormSchema>;
```

### Template: `api.ts`

```typescript
import { createEntityApi } from "../../common/api/entityApi";
import type { {Entity}, {Entity}ListItem } from "./types";

export const {entity}Api = createEntityApi<{Entity}>({
	module: "{module}",
	path: "{entity}s",  // or appropriate plural
	type: "{entity}",
	includes: "include[{entity}]=mainContact,logo",  // Copy from {Entity}ApiImpl.ts INCLUDES
	relations: {
		// Copy from {Entity}ApiImpl.ts RELATIONS
		// example: mainContact: "contact",
		// example: logo: "document",
	},
});

export const {entity}ListApi = createEntityApi<{Entity}ListItem>({
	module: "{module}",
	path: "{entity}s",
	type: "{entity}",
	includes: "include[{entity}]=mainContact",  // Fewer includes for list
	relations: {
		// Subset of full relations
		// example: mainContact: "contact",
	},
});
```

### Template: `queries.ts`

```typescript
import { useQuery } from "@tanstack/react-query";
import { {entity}Api, {entity}ListApi } from "./api";
import type { {Entity} } from "./types";
import { useCreateEntity, useDeleteEntity } from "../../common/hooks";

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
	return useCreateEntity<{Entity}>({
		createFn: (data) => {entity}Api.create(data),
		listQueryKey: {entity}Keys.lists(),
		successMessage: "{Entity} erstellt",  // Update German message
	});
}

export function useDelete{Entity}() {
	return useDeleteEntity({
		deleteFn: {entity}Api.delete,
		listQueryKey: {entity}Keys.lists(),
		successMessage: "{Entity} gelöscht",  // Update German message
	});
}

export function get{Entity}QueryOptions(id: string) {
	return {
		queryKey: {entity}Keys.detail(id),
		queryFn: () => {entity}Api.get(id),
	};
}

export function get{Entity}ListQueryOptions() {
	return {
		queryKey: {entity}Keys.lists(),
		queryFn: () => {entity}ListApi.list(),
	};
}
```

### Template: `index.ts`

```typescript
export type { {Entity}, {Entity}ListItem } from "./types";
export { {entity}CreationSchema, {entity}FormSchema } from "./schemas";
export type { {Entity}CreationData } from "./schemas";
export { {entity}Api, {entity}ListApi } from "./api";
export {
	{entity}Keys,
	use{Entity}List,
	use{Entity},
	useCreate{Entity},
	useDelete{Entity},
	get{Entity}QueryOptions,
	get{Entity}ListQueryOptions,
} from "./queries";
export { {Entity}Area } from "./ui/{Entity}Area";
export { {Entity}Page } from "./ui/{Entity}Page";
```

### Template: Route `{entity}.tsx`

```typescript
import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/{entity}")({
	component: {Entity}Layout,
});

function {Entity}Layout() {
	return <Outlet />;
}
```

### Template: Route `{entity}.index.tsx`

```typescript
import { createFileRoute } from "@tanstack/react-router";
import { {Entity}Area } from "../areas/{entity}/ui/{Entity}Area";

export const Route = createFileRoute("/{entity}/")({
	component: {Entity}Area,
});
```

### Template: Route `{entity}.${entity}Id.tsx`

```typescript
import { createFileRoute } from "@tanstack/react-router";
import { {Entity}Page } from "../areas/{entity}/ui/{Entity}Page";

export const Route = createFileRoute("/{entity}/${ENTITY_ID_PARAM}")({
	component: {Entity}PageRoute,
});

function {Entity}PageRoute() {
	const { {ENTITY_ID_PARAM} } = Route.useParams();
	return <{Entity}Page {ENTITY_ID_PARAM}={{ENTITY_ID_PARAM}} />;
}
```

### Template: Translation Structure

**`de/{entity}.json`:**

```json
{
	"label": {
		"entity": "{Entity German Name}",
		"entityCount": "{count, plural, =0 {Keine {entities}} one {# {entity}} other {# {entities}}}",
		"name": "Name",
		"description": "Beschreibung",
		"tabMain": "Stammdaten",
		"tabDocuments": "Dokumente",
		"notes": "Notizen",
		"tasks": "Aufgaben",
		"activity": "Aktivität"
	},
	"action": {
		"backToList": "Zurück zur {Entity}-Liste"
	},
	"message": {
		"notFound": "{Entity} nicht gefunden",
		"notFoundDescription": "Der angeforderte {Entity} konnte nicht gefunden werden.",
		"validation": {
			"nameRequired": "Name ist erforderlich"
		}
	}
}
```

**`en/{entity}.json`:**

```json
{
	"label": {
		"entity": "{Entity}",
		"entityCount": "{count, plural, =0 {No {entities}} one {# {entity}} other {# {entities}}}",
		"name": "Name",
		"description": "Description",
		"tabMain": "Main",
		"tabDocuments": "Documents",
		"notes": "Notes",
		"tasks": "Tasks",
		"activity": "Activity"
	},
	"action": {
		"backToList": "Back to {entity} list"
	},
	"message": {
		"notFound": "{Entity} not found",
		"notFoundDescription": "The requested {entity} could not be found.",
		"validation": {
			"nameRequired": "Name is required"
		}
	}
}
```

## Integration Steps

### 1. Register Area in AppConfig.ts

Add to the `areas` array in `fm-ux/src/app/config/AppConfig.ts`:

```typescript
{
	name: "{entity}",
	icon: <Icon />,  // Choose appropriate icon
	label: "{entity}:label.entity",
	enabled: true,
},
```

### 2. Register i18n Namespace

In `fm-ux/src/i18n/index.ts`:

1. Add imports:
```typescript
import en{Entity} from "./locales/en/{entity}.json";
import de{Entity} from "./locales/de/{entity}.json";
```

2. Add to resources:
```typescript
const resources = {
	en: {
		// ... existing
		{entity}: en{Entity},
	},
	de: {
		// ... existing
		{entity}: de{Entity},
	},
};
```

3. Add to namespaces array:
```typescript
const namespaces = ["common", "login", "app", "home", "account", "{entity}"];
```

## UI Component Templates

### Template: `ui/{Entity}Area.tsx` (List View)

```typescript
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { {entity}ListApi } from "../api";
import { {entity}Keys } from "../queries";
import { {Entity}CreationForm } from "./forms/{Entity}CreationForm";
import { {Entity}Preview } from "./{Entity}Preview";
import type { {Entity}ListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

export function {Entity}Area() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";

	const columns: ColumnType<{Entity}ListItem>[] = [
		{
			title: t("{entity}:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		// Add more columns based on layout config
		// For Enumerated fields, use: dataIndex: ["fieldName", "name"]
		// For relationships, use: dataIndex: ["relationship", "caption"] or ["relationship", "name"]
		// Example:
		// {
		//   title: t("{entity}:label.accountType"),
		//   dataIndex: ["accountType", "name"],
		//   key: "accountType",
		//   sorter: (a, b) => (a.accountType?.name ?? "").localeCompare(b.accountType?.name ?? ""),
		// },
	];

	return (
		<ItemsPage<{Entity}ListItem>
			entityType="{entity}"
			entityLabelKey="{entity}.label.entityCount"
			entityLabelSingular={t("{entity}:label.entity")}
			icon={getArea("{entity}")?.icon}
			queryKey={[...{entity}Keys.lists()]}
			queryFn={() => {entity}ListApi.list()}
			columns={columns}
			canCreate={canCreateEntity("{entity}", userRole, tenantType)}
			CreateForm={{Entity}CreationForm}
			PreviewComponent={{Entity}Preview}
			getDetailPath={(record) => `/{entity}/${record.id}`}
		/>
	);
}
```

### Template: `ui/{Entity}Page.tsx` (Detail View)

```typescript
import { useState } from "react";
import { Button, Card, Modal, Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { useEntityQueries } from "../../../common/hooks/useEntityQueries";
import { ItemPageHeader, ItemPageLayout, EditControls } from "../../../common/components/items";
import { AfForm } from "../../../common/components/form";
import { RelatedPanel } from "../../../common/components/related";
import { NotesList } from "../../../common/components/related/NotesList";
import { TasksList } from "../../../common/components/related/TasksList";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Note } from "../../../common/components/related/NotesList";
import type { Task } from "../../../common/components/related/TasksList";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { canModifyEntity } from "../../../common/utils";
import { {entity}Api } from "../api";
import { {entity}Keys } from "../queries";
import { {entity}FormSchema, type {Entity}FormInput } from "../schemas";
import { {Entity}MainForm } from "./forms/{Entity}MainForm";
import type { {Entity} } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface {Entity}PageProps {
	{ENTITY_ID_PARAM}: string;
}

export function {Entity}Page({ {ENTITY_ID_PARAM} }: {Entity}PageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const {
		entity: {entity},
		form,
		isLoading,
		isError,
		isEditing,
		isDirty,
		isStoring,
		handleEdit,
		handleCancel,
		handleStore,
	} = useEntityQueries<{Entity}, {Entity}FormInput>({
		id: {ENTITY_ID_PARAM},
		queryKey: [...{entity}Keys.all],
		queryFn: (id) => {entity}Api.get(id),
		updateFn: {entity}Api.update,
		schema: {entity}FormSchema,
	});

	if (isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (isError || !{entity}) {
		return (
			<Result
				status="404"
				title={t("{entity}:message.notFound")}
				subTitle={t("{entity}:message.notFoundDescription")}
				extra={<Link to="/{entity}">{t("{entity}:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("{entity}", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("{entity}")?.icon}
				title={{entity}.name}
				details={[
					{
						label: t("{entity}:label.tenant"),
						content: {entity}.tenant?.name,
					},
					{
						label: t("{entity}:label.owner"),
						content: {entity}.owner?.name,
					},
					// Add more detail fields as needed
				]}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("{entity}:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("{entity}:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("{entity}:label.activity"),
								children: <ActivityTimeline activities={[] as Activity[]} />,
							},
						]}
					/>
				}
			>
				<Card className="af-full-height">
					<AfForm form={form}>
						<Tabs
							tabBarExtraContent={
								<EditControls
									isEditing={isEditing}
									isDirty={isDirty}
									isStoring={isStoring}
									canEdit={canEdit}
									onEdit={handleEdit}
									onCancel={handleCancel}
									onStore={handleStore}
								/>
							}
							items={[
								{
									key: "main",
									label: t("{entity}:label.tabMain"),
									children: <{Entity}MainForm disabled={!isEditing} />,
								},
								// Add more tabs as needed
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
```

### Template: `ui/{Entity}Preview.tsx` (Preview Drawer)

```typescript
import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { use{Entity} } from "../queries";
import { getLogoUrl } from "../../../common/api/client";
import { getArea } from "../../../app/config/AppConfig";

const { Text, Paragraph } = Typography;

interface {Entity}PreviewProps {
	id: string;
	onClose: () => void;
}

export function {Entity}Preview({ id, onClose }: {Entity}PreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const [logoError, setLogoError] = useState(false);

	const { data: {entity}, isLoading, isError } = use{Entity}(id);

	useEffect(() => {
		setLogoError(false);
	}, [id]);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/{entity}/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !{entity}) {
		return <Result status="error" title={t("{entity}:message.notFound")} />;
	}

	// Only if entity has logo support
	const logoUrl = getLogoUrl("{entity}", id);

	return (
		<div className="af-preview-container">
			{/* Logo - Optional, only if entity supports logos */}
			<div className="af-preview-avatar">
				{!logoError ? (
					<img
						src={logoUrl}
						alt={{entity}.name}
						className="af-preview-avatar-image"
						onError={() => setLogoError(true)}
					/>
				) : (
					<div className="af-preview-avatar-placeholder">{getArea("{entity}")?.icon}</div>
				)}
			</div>

			{/* Name */}
			<div className="af-preview-name">
				<Text className="af-preview-name-text">{{entity}.name}</Text>
			</div>

			{/* Details */}
			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("{entity}:label.name")}>
					{{entity}.name || "-"}
				</Descriptions.Item>
				{/* Add more fields based on discovery */}
				{/* Example for Enumerated field:
				<Descriptions.Item label={t("{entity}:label.accountType")}>
					{{entity}.accountType?.name || "-"}
				</Descriptions.Item>
				*/}
			</Descriptions>

			{/* Description - if entity has description field */}
			{{entity}.description && (
				<div>
					<Text className="af-preview-description-label">{t("{entity}:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{{entity}.description}
					</Paragraph>
				</div>
			)}

			{/* Actions */}
			<Space className="af-preview-actions">
				<Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
					{t("common:action.edit")}
				</Button>
			</Space>
		</div>
	);
}
```

### Template: `ui/forms/{Entity}MainForm.tsx` (Main Form)

**CRITICAL:** Copy the form layout exactly from `fm-ui/src/areas/{entity}/ui/forms/{Entity}Form.ts`. Match the field grouping, ordering, and sizing. Use the same field groups and legends.

```typescript
import { Col, Row } from "antd";
import { useFormContext } from "react-hook-form";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfNumber,
	AfDatePicker,
	AfFieldRow,
	AfFieldGroup,
} from "../../../../common/components/form";
import type { {Entity}FormInput } from "../../schemas";

interface {Entity}MainFormProps {
	disabled: boolean;
}

export function {Entity}MainForm({ disabled }: {Entity}MainFormProps) {
	const { t } = useTranslation();
	const { watch } = useFormContext<{Entity}FormInput>();

	return (
		<div>
			{/* Copy the exact layout from fm-ui {Entity}Form.ts */}
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("{entity}:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="name"
								label={t("{entity}:label.name")}
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						{/* Add more fields matching fm-ui form layout */}
						{/* Example for code table select:
						<AfFieldRow>
							<AfSelect
								name="accountType"
								label={t("{entity}:label.accountType")}
								source="{module}/codeAccountType"
								required
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						*/}
						{/* Example for number field:
						<AfFieldRow>
							<AfNumber
								name="amount"
								label={t("{entity}:label.amount")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						*/}
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend="&nbsp;">
						<AfTextArea
							name="description"
							label={t("{entity}:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("{entity}:label.organization")}>
						<AfFieldRow>
							<AfSelect
								name="owner"
								label={t("{entity}:label.owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
```

### Template: `ui/forms/{Entity}CreationForm.tsx` (Creation Form)

**CRITICAL:** 
1. Copy the form layout and field order from `fm-ui/src/areas/{entity}/ui/forms/{Entity}Form.ts`
2. Copy default values from the fm-ui form (check `initDefault()` or `buildInitial()` methods)
3. Rely on Zod schema validation from `{entity}CreationSchema` - do NOT add manual validation in the submit handler
4. Use `zodResolver` to connect the schema to React Hook Form

```typescript
import { Button, Space } from "antd";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfTextArea, AfSelect } from "../../../../common/components/form";
import { useCreate{Entity} } from "../../queries";
import { {entity}CreationSchema, type {Entity}CreationFormInput } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";
import { useSessionStore } from "../../../../session/model/sessionStore";

export function {Entity}CreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreate{Entity}();
	
	// Copy default values from fm-ui {Entity}Form.ts
	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const form = useForm<{Entity}CreationFormInput>({
		resolver: zodResolver({entity}CreationSchema),
		defaultValues: {
			name: "",
			description: "",
			owner: defaultOwner,
			// Add other default values matching fm-ui form
		},
	});

	const handleSubmit = async (data: {Entity}CreationFormInput) => {
		// No manual validation needed - Zod schema handles it
		try {
			const created{Entity} = await createMutation.mutateAsync({
				name: data.name,
				description: data.description,
				owner: data.owner!,
				// Add other fields
			});
			onSuccess();
			navigate({ to: "/{entity}/${ENTITY_ID_PARAM}", params: { {ENTITY_ID_PARAM}: created{Entity}.id } });
		} catch {
			// Error handling is done in useCreate{Entity}'s onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			{/* Copy field order and layout from fm-ui {Entity}Form.ts */}
			<AfInput name="name" label={t("{entity}:label.name")} required />

			<AfSelect name="owner" label={t("{entity}:label.owner")} source="oe/objUser" required />

			{/* Add more creation fields matching fm-ui form */}
			{/* Example:
			<AfSelect
				name="accountType"
				label={t("{entity}:label.accountType")}
				source="{module}/codeAccountType"
				required
			/>
			*/}

			<AfTextArea name="description" label={t("{entity}:label.description")} rows={3} />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="{entity}:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
```

### Key Adaptation Points

1. **List View (`{Entity}Area.tsx`)**
   - Define `columns` based on layout config `header` array
   - Use `dataIndex: ["{field}", "name"]` for Enumerated fields
   - Get icon via `getArea("{entity}")?.icon` from AppConfig
   - Use `canCreateEntity()` permission check
   - Spread queryKey: `[...{entity}Keys.lists()]`

2. **Detail View (`{Entity}Page.tsx`)**
   - Use `AfForm` wrapper (not `FormProvider` directly)
   - Place `EditControls` in `Tabs tabBarExtraContent`
   - Wrap page in `<div className="af-flex-column af-full-height">`
   - Use `ItemPageHeader` with `details` array
   - Use `ItemPageLayout` with `RelatedPanel` in `rightPanel`

3. **Preview Panel (`{Entity}Preview.tsx`)**
   - Fetch full entity using `use{Entity}(id)`
   - Use CSS utility classes (`.af-preview-*`)
   - Include "Edit" button navigating to detail page
   - Logo section is optional (only if entity supports logos)

4. **Main Form (`{Entity}MainForm.tsx`)**
   - **Copy form layout exactly from fm-ui** `{Entity}Form.ts`
   - Use `AfFieldGroup` with `legend` for grouping
   - Use `Row`/`Col` for multi-column layout (24-column grid)
   - Use `AfFieldRow` for inline field groups
   - Do NOT include tenant field (shown in page header)
   - Match field order, grouping, and sizes to the old form

5. **Creation Form (`{Entity}CreationForm.tsx`)**
   - **Copy field order and defaults from fm-ui** `{Entity}Form.ts`
   - Use `AfForm` with `onSubmit` prop
   - Use `zodResolver` with creation schema - let Zod handle validation
   - Do NOT add manual validation in submit handler
   - Set default values matching fm-ui form (check `initDefault()` or `buildInitial()`)
   - Navigate to created entity's detail page after success with try/catch
   - Do NOT include tenant field (server sets automatically)

## Code Style Guidelines

### Validation Philosophy

**Rely on Zod schemas for all validation.** Do NOT add manual validation in form submit handlers:
- ✓ Define validation rules in Zod schemas (`{entity}CreationSchema`, `{entity}FormSchema`)
- ✓ Use `zodResolver` to connect schemas to React Hook Form
- ✗ Do NOT check `!data.name?.trim()` or similar in submit handlers
- ✗ Do NOT manually call `form.setError()` for validation (only for server errors)

The schema is the single source of truth for validation rules.

### Preserve Existing Patterns

**Copy from fm-ui, don't invent:**
- Form layouts must match the old fm-ui form exactly (field order, grouping, sizes)
- Default values must match the old form's `initDefault()` or `buildInitial()` methods
- Required/optional flags must match the old form's validation
- Field types and data sources must match the old form's field definitions

### Comments

**Avoid superfluous comments.** The code should be self-documenting. Do not add:
- File header JSDoc comments that just restate the file name
- Interface/type comments that repeat what's obvious from the code
- Inline comments explaining obvious code
- Function comments that just repeat the function name
- Section comments in JSX that label obvious sections

Only add comments when they explain *why* something is done a certain way, not *what* the code does.

## Verification Checklist

After implementing, verify:

1. **TypeScript**: `pnpm tsc --noEmit`
2. **Linting**: `pnpm lint` (fix with `pnpm lint:fix`)
3. **Build**: `pnpm build`
4. **Tests**: `pnpm test:run`

---

# Appendix: Patterns & Gotchas

## 1. Code Tables vs Relationships (CRITICAL)

**Common mistake**: Adding code tables or base fields to `relations` causes errors like `"Invalid relationship name: owner for account"`.

**Rule:**
- **Code tables**: NOT in `includes` or `relations` (they're attributes)
- **Base fields** (`owner`, `tenant`): NOT in `includes` or `relations` - these are fields from `AggregateDtoAdapterBase`, not relationships
- **Relationships**: MUST be in both `includes` and `relations`

**How to identify:** Check the DTO adapter (`{Entity}DtoAdapter.kt`) - fields with `config.relationship()` are relationships, fields with `config.field()` are attributes. The base class `AggregateDtoAdapterBase` configures `owner` and `tenant` as fields (attributes), not relationships. Alternatively, check the JSONAPI response: `relationships` section = relationships, `attributes` = attributes/code tables/base fields.

## 2. Generic Mutation Hooks

Use `useCreateEntity` and `useDeleteEntity` from `common/hooks` instead of writing custom mutation hooks:

```typescript
import { useCreateEntity, useDeleteEntity } from "../../common/hooks";

export function useCreate{Entity}() {
	return useCreateEntity<{Entity}>({
		createFn: (data) => {entity}Api.create(data),
		listQueryKey: {entity}Keys.lists(),
		successMessage: "{Entity} erstellt",
	});
}

export function useDelete{Entity}() {
	return useDeleteEntity({
		deleteFn: {entity}Api.delete,
		listQueryKey: {entity}Keys.lists(),
		successMessage: "{Entity} gelöscht",
	});
}
```

**Benefits:**
- Centralized cache invalidation and user feedback
- Consistent error handling across all entities
- Less boilerplate in entity query files

**Note:** Update mutations are handled by `useEntityQueries` hook, not as separate hooks.

## 3. Display-Only Fields (Schema Metadata)

Use the `displayOnly()` helper to mark form fields that should be displayed but not submitted:

```typescript
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export const {entity}FormSchema = z.object({
	name: z.string().min(1, "Name ist erforderlich"),
	status: enumeratedSchema,
	// Display-only: automatically excluded from submission
	contacts: displayOnly(z.array(z.any()).optional()),
});
```

**How it works:**
- `displayOnly()` wraps a Zod schema and adds metadata via `.describe()`
- `transformFromForm()` automatically detects and excludes these fields
- No need to pass `displayOnlyFields` to `useEntityQueries` - it's detected from schema metadata

## 4. Readonly Array in queryKey

`ItemsPage` expects `string[]` but query keys are `readonly`. Spread to convert:

```typescript
queryKey={[...{entity}Keys.lists()]}  // ✓ Correct
queryKey={{entity}Keys.lists()}       // ✗ Type error
```

## 5. Related Panel Components

`NotesList`, `TasksList`, and `ActivityTimeline` are presentational components. They expect data arrays, not entity IDs. Pass empty arrays for stubs:

```typescript
<NotesList notes={[] as Note[]} />
<TasksList tasks={[] as Task[]} />
<ActivityTimeline activities={[] as Activity[]} />
```

## 6. Relative Imports

The project uses relative imports, not path aliases. Import paths are relative to the file location.

## 7. Form Components

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

## 8. Grid System (24-Column)

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

## 9. Styling with Design Tokens and CSS Classes

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

## 10. Permission Checks

Use the shared permission utilities from `common/utils/permissions`:

```typescript
import { canModifyEntity, canCreateEntity } from "../../../common/utils";

// In Area component (needs userRole and tenantType from sessionStore)
const canCreate = canCreateEntity("{entity}", userRole, tenantType);

// In Page component
const canEdit = canModifyEntity("{entity}", userRole);
```

**When migrating a new entity:** Add the entity's permission logic to `common/utils/permissions.ts`. Check the fm-ui area's `canCreate` condition (e.g., in `{Entity}Area.tsx`) and translate it to the new permission functions.

## 11. Tenant Handling

**Tenant fields are NOT shown in forms.** The tenant is already displayed in the page header (readonly) and the server handles setting the tenant automatically. Do not include tenant fields in:
- Creation forms (server sets tenant based on session)
- Main forms (shown in header, not editable)

This simplifies forms significantly by removing conditional tenant logic.

## 12. Navigation Components

Use TanStack Router's `<Link>` component instead of `<a onClick>` for navigation:

```typescript
// ✗ Bad - accessibility issues
<a onClick={() => navigate({ to: "/{entity}" })}>Back to list</a>

// ✓ Good - proper link semantics
import { Link } from "@tanstack/react-router";
<Link to="/{entity}">Back to list</Link>
```

## 13. Query Key Factory

Always use the query key factory instead of literal arrays:

```typescript
// ✗ Bad - hardcoded array
queryKey: ["{entity}"]

// ✓ Good - use factory
import { {entity}Keys } from "../queries";
queryKey: {entity}Keys.all
```

## 14. Parent Context Pattern

If the entity can be created from a parent entity's page (e.g., creating a Contact from an Account), extend `CreateFormProps` to accept the parent entity as an optional prop. See `ContactCreationForm` for an example.

## Reference Files

**Account implementation (canonical example):**
- `fm-ux/src/areas/account/` - Complete area implementation

**Common components:**
- `fm-ux/src/common/components/form/` - Form components (AfForm, AfInput, etc.)
- `fm-ux/src/common/components/items/` - ItemsPage, ItemPageHeader, EditControls
- `fm-ux/src/common/components/related/` - RelatedPanel, NotesList, TasksList

**Hooks:**
- `fm-ux/src/common/hooks/useEntityQueries.ts` - Detail page edit/cancel/store pattern
- `fm-ux/src/common/hooks/useEntityMutations.ts` - Generic useCreateEntity, useDeleteEntity hooks

**Utilities:**
- `fm-ux/src/common/utils/zodMeta.ts` - enumeratedSchema, displayOnly helper
- `fm-ux/src/common/utils/formTransformers.ts` - entity ↔ form conversion
- `fm-ux/src/common/utils/permissions.ts` - canModifyEntity, canCreateEntity helpers
- `fm-ux/src/common/api/entityApi.ts` - createEntityApi factory

**Styling:**
- `fm-ux/src/styles/global.css` - CSS utility classes
- `fm-ux/src/common/hooks/useStyles.ts` - Theme-aware styles
