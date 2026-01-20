---
name: Add New Aggregate Base Field
description: Add a new base field to an aggregate with clear rules for:
- manual vs calculated fields
- calculation modes
- volatile vs persistent storage
---

# Add New Aggregate Base Field

## Goal
Add a new base field to an aggregate with clear rules for:
- manual vs calculated fields
- calculation modes
- volatile vs persistent storage

## Checklist

- Define the field type and responsibility
  - Manual: written by client/user input
  - Calculated: derived from other fields via calc logic

- Decide persistence
  - Persistent: stored in DB column and mapped in persistence layer
  - Volatile: calculated on read, never stored

- Decide calculation mode
  - Full calculation (default save): `calcAll()` runs after updates
  - Disabled calc during DTO mapping: ensure calc occurs after mapping

## Implementation Steps

- Model interface
  - Add property to aggregate interface (e.g., `var shortTextU: String?`)
  - Use clear naming for computed fields

- Aggregate implementation
  - Manual base field: `baseProperty("field")`
  - Calculated base field (volatile): `baseProperty("field") { ... }`
  - Calculated base field (persistent): assign in `doCalcAll()`

- Calc logic placement
  - Use `doCalcAll()` for persistent calculated fields
  - Use property calculator for volatile calculated fields

- Persistence mapping (if persistent)
  - Add DB column + view (Flyway)
  - Map load/store in SQL persistence provider

- DTO/JSON API
  - Calculated fields are exposed like any base property
  - For calculation-only updates: include `meta.operations = ["calculationOnly"]`

## Calculation Modes

- Normal save:
  - DTO mapping happens with calc disabled
  - Calc is re-enabled and `calcAll()` runs
  - `store()` persists and increments version

- calculationOnly:
  - DTO mapping + `calcAll()` runs
  - No store, no version increment
  - Useful for "what-if" calculations

## Practical Classification Examples

- Manual + Persistent
  - User input field stored in DB

- Calculated + Persistent
  - Derived field that must persist (e.g., uppercase copy)
  - Set in `doCalcAll()` and persisted

- Calculated + Volatile
  - Derived field not stored
  - Implement via `baseProperty { ... }`

## Gotchas

- Volatile fields must not be written by client
- Persistent calculated fields must be stored in DB + view
- For jOOQ-generated tables/records, always run Flyway + jOOQ codegen after schema changes
