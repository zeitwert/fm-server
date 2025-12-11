# Phase 2c: DDDrive Cleanup

**Status:** Pending  
**Dependencies:** [Phase 2b - Domain Migration](03-phase-2b-domain-migration.md)  
**Next Phase:** [Phase 3 - REST API](05-phase-3-rest-api.md)

## Objective

Remove the old external dddrive dependency after all domains have been migrated to the new embedded dddrive.

## Prerequisites

Before starting this phase, verify:
- [ ] All 8 domains migrated (Phase 2b complete)
- [ ] All tests pass
- [ ] Application runs correctly
- [ ] No runtime errors related to dddrive

## Tasks

### 1. Verify No Old Imports Remain

Run a search for old dddrive imports:

```bash
# Search for old imports (should return no results)
grep -r "import io.dddrive\." --include="*.java" --include="*.kt" fm-domain/
grep -r "import io.dddrive\." --include="*.java" --include="*.kt" fm-server/
grep -r "import io.dddrive\." --include="*.java" --include="*.kt" fm-common/
```

**Expected:** No matches (all should be `io.dddrive.core.*`)

- [ ] No old imports found in `fm-domain`
- [ ] No old imports found in `fm-server`
- [ ] No old imports found in `fm-common`

### 2. Remove Old Dependency

Update parent `pom.xml`:

```xml
<!-- REMOVE this dependency -->
<dependency>
    <groupId>io.zeitwert</groupId>
    <artifactId>dddrive</artifactId>
    <version>1.0.18</version>
</dependency>
```

- [ ] Remove old dddrive dependency from parent POM
- [ ] Remove from any module POMs if present

### 3. Clean and Rebuild

```bash
mvn clean install
```

- [ ] Build succeeds without old dependency
- [ ] No compilation errors
- [ ] All tests pass

### 4. Remove Compatibility Code

If any temporary compatibility code was added during migration:

- [ ] Remove any adapter/bridge classes
- [ ] Remove any duplicate type definitions
- [ ] Remove any wrapper classes

### 5. Update Documentation

- [ ] Update README.md if it references old dddrive
- [ ] Update any architecture documentation
- [ ] Update dependency documentation

### 6. Final Verification

- [ ] Application starts successfully
- [ ] All CRUD operations work
- [ ] All domain functionality works
- [ ] Performance is acceptable
- [ ] No memory leaks from duplicate classes

## Verification Script

Create a verification script to ensure cleanup is complete:

```bash
#!/bin/bash
# verify-dddrive-cleanup.sh

echo "Checking for old dddrive imports..."
OLD_IMPORTS=$(grep -r "import io.dddrive\." --include="*.java" --include="*.kt" . | grep -v "io.dddrive.core" | wc -l)

if [ "$OLD_IMPORTS" -gt 0 ]; then
    echo "ERROR: Found $OLD_IMPORTS old dddrive imports:"
    grep -r "import io.dddrive\." --include="*.java" --include="*.kt" . | grep -v "io.dddrive.core"
    exit 1
fi

echo "Checking for old dddrive in POMs..."
OLD_POM=$(grep -r "io.zeitwert.*dddrive.*1.0" --include="pom.xml" . | wc -l)

if [ "$OLD_POM" -gt 0 ]; then
    echo "ERROR: Found old dddrive dependency in POM files"
    grep -r "io.zeitwert.*dddrive.*1.0" --include="pom.xml" .
    exit 1
fi

echo "All checks passed!"
```

## Rollback Plan

If issues are found after removing old dependency:

1. Re-add old dependency to POM
2. Identify which domain(s) still have issues
3. Fix the specific domain migration
4. Retry cleanup

## Completion Criteria

- [ ] Old `io.zeitwert:dddrive` dependency removed
- [ ] Build succeeds
- [ ] All tests pass
- [ ] Application functions correctly
- [ ] No old dddrive code or imports remain
- [ ] Documentation updated

