---
name: ERP UI Template Review
overview: A comprehensive code review of the fm-ux account and contact areas, identifying internationalization gaps, inconsistent patterns, type safety issues, and deviations from best practices for a Vite + TypeScript + React + Ant Design internationalized ERP UI.
todos:
  - id: i18n-schemas
    content: Move all hardcoded validation messages in schemas.ts to translation keys (both areas)
    status: pending
  - id: i18n-queries
    content: Move all success/error messages in queries.ts to translation keys (both areas)
    status: pending
  - id: i18n-forms
    content: Move hardcoded messages in creation forms to translation keys
    status: pending
  - id: i18n-pages
    content: Move hardcoded labels in Page components (RelatedPanel) to translation keys
    status: pending
  - id: i18n-common
    content: Move hardcoded German strings in ItemsPage.tsx to common translation namespace
    status: pending
  - id: types-dedupe
    content: Remove duplicate FormData types from types.ts, use only Zod-inferred types
    status: pending
  - id: validation-unify
    content: Add Zod resolver to creation forms instead of manual setError calls
    status: pending
  - id: permissions-util
    content: Extract role checking functions to shared permissions utility
    status: pending
  - id: columns-memo
    content: Wrap table column definitions in useMemo for performance
    status: pending
  - id: consistency-behavior
    content: Align creation behavior between areas (navigation, canCreate pattern)
    status: pending
isProject: false
---

# ERP UI Template Code Review

## Critical Issues

### 1. Hardcoded German Strings - i18n Violations

The most significant issue across both areas is hardcoded German text that should use translation keys.

**Schema Validation Messages** ([`schemas.ts`](fm-ux/src/areas/account/schemas.ts)):

```16:fm-ux/src/areas/account/schemas.ts
	name: z.string().min(1, "Name ist erforderlich"),
```
```20:21:fm-ux/src/areas/contact/schemas.ts
	lastName: z.string().min(1, "Nachname ist erforderlich"),
```

**Query Mutation Messages** ([`queries.ts`](fm-ux/src/areas/account/queries.ts)):

```37:41:fm-ux/src/areas/account/queries.ts
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: accountKeys.lists() });
			message.success("Kunde erstellt");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Erstellen: ${error.message}`);
```

**Creation Form Validation** ([`AccountCreationForm.tsx`](fm-ux/src/areas/account/ui/forms/AccountCreationForm.tsx)):

```39:58:fm-ux/src/areas/account/ui/forms/AccountCreationForm.tsx
		if (!data.name?.trim()) {
			form.setError("name", { message: "Name ist erforderlich" });
			hasError = true;
		}
		// ... more hardcoded messages
		if (hasError) {
			message.error("Bitte füllen Sie alle Pflichtfelder aus");
			return;
		}
```

**Page Components** - RelatedPanel labels hardcoded:

```111:119:fm-ux/src/areas/account/ui/AccountPage.tsx
							{
								key: "notes",
								label: "Notizen",
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: "Aufgaben",
								children: <TasksList tasks={[] as Task[]} />,
							},
```

**Common Components** ([`ItemsPage.tsx`](fm-ux/src/common/components/items/ItemsPage.tsx)):

- Line 295: `"ausgewählt"` (selected)
- Line 353: `"Fehler beim Laden der"` (error loading)
- Line 394: `"von"` (of)
- Line 411: `"erstellen"` (create)

---

### 2. Type Duplication and Inconsistency

**Duplicate Type Definitions:**

- `AccountFormData` is defined in both [`types.ts`](fm-ux/src/areas/account/types.ts) (line 43) and [`schemas.ts`](fm-ux/src/areas/account/schemas.ts) (line 55)
- Same issue exists for `ContactFormData`
- The schema-derived types should be the single source of truth

**Recommendation:** Remove `AccountFormData`/`ContactFormData` from `types.ts` and use only the Zod-inferred types from `schemas.ts`.

---

### 3. Inconsistent Validation Patterns

**Creation vs Edit Forms:**

- Edit forms use `standardSchemaResolver(schema)` for validation (correct)
- Creation forms manually call `form.setError()` instead of using the Zod resolver (incorrect)
```25:34:fm-ux/src/areas/account/ui/forms/AccountCreationForm.tsx
	const form = useForm<AccountCreationFormInput>({
		defaultValues: {
			name: "",
			description: "",
			// ... no resolver configured!
		},
	});
```


**Should be:**

```typescript
const form = useForm<AccountCreationFormInput>({
  resolver: standardSchemaResolver(accountCreationSchema),
  defaultValues: { ... },
});
```

---

### 4. Inconsistent Area Behaviors

| Feature | Account | Contact |

|---------|---------|---------|

| `canCreate` | Role-based check | Hardcoded `false` |

| Post-creation navigation | Stays on list | Navigates to detail |

| `CreateForm` component | Provided | Not used |

| `entitySingular` translation key | Missing | Present |

---

### 5. Performance Issues

**Table columns recreated on every render:**

```22:64:fm-ux/src/areas/account/ui/AccountArea.tsx
	const columns: ColumnType<AccountListItem>[] = [
		// ... should be memoized with useMemo
	];
```

**Role check functions inside components:**

```13:15:fm-ux/src/areas/account/ui/AccountArea.tsx
function canCreateAccount(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}
```

These should be extracted to a shared utility.

---

### 6. Error Handling Gaps

**Empty catch block:**

```86:88:fm-ux/src/areas/contact/ui/forms/ContactCreationForm.tsx
		} catch {
			// Error handling is done in useCreateContact's onError callback
		}
```

**Missing error boundaries** for component-level failures.

---

### 7. Accessibility Gaps

- Preview buttons have `aria-label` but form inputs rely only on `<label>`
- Missing `aria-describedby` linking inputs to their error messages
- The `<a>` tag in AccountPage Result component (line 73) should be a `<Button>` or have proper keyboard handling

---

### 8. Translation File Inconsistencies

**Missing keys:**

- `account.json` lacks `entitySingular` (contact has it)
- No `common.json` keys for shared panel labels (Notes, Tasks, Activity)

**Inconsistent naming:**

- Account uses `account`/`accounts` 
- Contact uses `contact`/`contacts` AND `entitySingular`/`newContact`

---

## Structural Recommendations

### 1. Create Shared Role Utility

```typescript
// common/utils/permissions.ts
export function canPerformAction(role: string | undefined, allowedRoles: string[]): boolean {
  return !!role && allowedRoles.includes(role);
}

export const EDITOR_ROLES = [ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER];
```

### 2. Create Schema Message Factory

```typescript
// common/utils/validation.ts
export function createValidationMessages(t: TFunction) {
  return {
    required: (field: string) => t('common:validation.required', { field }),
    minLength: (field: string, min: number) => t('common:validation.minLength', { field, min }),
  };
}
```

### 3. Standardize Area Module Structure

Each area should export:

- Types (single source from Zod inference)
- Schemas (with i18n error messages)
- API (no changes needed)
- Queries (with i18n success/error messages)
- UI components

### 4. Create Generic Query Factory

The queries in both areas are nearly identical. A factory could reduce boilerplate:

```typescript
export function createEntityQueries<T extends BaseEntity>(
  entityName: string,
  api: EntityApi<T>,
  listApi: EntityApi<T>,
  t: TFunction
) {
  // Returns { keys, useList, useOne, useCreate, useUpdate, useDelete }
}
```

---

## Summary Table

| Category | Issue Count | Severity |

|----------|-------------|----------|

| i18n Violations | 30+ strings | High |

| Type Duplication | 4 types | Medium |

| Validation Inconsistency | 2 forms | Medium |

| Performance | 4 instances | Low |

| Accessibility | 3 patterns | Medium |

| Error Handling | 2 instances | Low |

---

## Recommended Priority

1. **Immediate**: Extract all hardcoded strings to translation files
2. **Soon**: Unify validation approach using Zod resolver in all forms
3. **Soon**: Remove duplicate type definitions
4. **Later**: Create shared utilities for permissions and queries
5. **Later**: Add comprehensive test coverage