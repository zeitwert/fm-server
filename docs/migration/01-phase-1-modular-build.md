# Phase 1: Modular Maven Build Structure

**Status:** Pending  
**Dependencies:** None  
**Next Phase:** [Phase 2a - DDDrive Setup](02-phase-2a-dddrive-setup.md)

## Objective

Convert fm-server from a single module to a multi-module Maven project.

## Target Structure

```
fm-server/
├── pom.xml (parent)
├── fm-common/         # Shared utilities, enums, base classes
├── fm-dddrive/        # Embedded DDDrive framework (from dfp-dddrive)
├── fm-jooq-adapter/   # PostgreSQL/jOOQ persistence adapters for dddrive
├── fm-domain/         # Domain models (building, contact, portfolio, etc.)
├── fm-ui/             # React frontend (moved from src/main/ui)
│   ├── pom.xml        # Frontend Maven plugin configuration
│   ├── package.json
│   └── src/
└── fm-server/         # Spring Boot application
    └── (copies fm-ui build output to static/)
```

## Tasks

### 1. Create Parent POM

- [ ] Create new `pom.xml` at root with `<packaging>pom</packaging>`
- [ ] Define `<modules>` section listing all child modules
- [ ] Move dependency management from current pom to parent
- [ ] Define common properties (Java version, Spring Boot version, etc.)
- [ ] Configure plugin management for shared plugins

**Reference:** [dfp-app-server/pom.xml](../dfp-app-server/pom.xml)

### 2. Create fm-common Module

- [ ] Create `fm-common/pom.xml`
- [ ] Extract shared utilities and helper classes
- [ ] Extract base enums if any are shared across domains
- [ ] Extract common configuration classes

### 3. Create fm-domain Module

- [ ] Create `fm-domain/pom.xml`
- [ ] Move domain packages:
  - `io.zeitwert.fm.account`
  - `io.zeitwert.fm.building`
  - `io.zeitwert.fm.contact`
  - `io.zeitwert.fm.portfolio`
  - `io.zeitwert.fm.task`
  - `io.zeitwert.fm.dms`
  - `io.zeitwert.fm.collaboration`
  - `io.zeitwert.fm.oe`
  - `io.zeitwert.fm.doc`
  - `io.zeitwert.fm.obj`
- [ ] Add dependency on `fm-common`
- [ ] Keep external dddrive dependency for now

### 4. Create fm-ui Module

- [ ] Create `fm-ui/pom.xml` with frontend-maven-plugin
- [ ] Move `src/main/ui/*` to `fm-ui/`
- [ ] Configure Node/Yarn installation
- [ ] Configure build commands (`yarn install`, `yarn build`)
- [ ] Update paths in `package.json` if needed

**fm-ui/pom.xml structure:**
```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <configuration>
        <nodeVersion>v18.12.1</nodeVersion>
        <yarnVersion>v1.22.19</yarnVersion>
        <workingDirectory>${project.basedir}</workingDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install-frontend-tools</id>
            <goals><goal>install-node-and-yarn</goal></goals>
        </execution>
        <execution>
            <id>yarn-install</id>
            <goals><goal>yarn</goal></goals>
            <configuration><arguments>install</arguments></configuration>
        </execution>
        <execution>
            <id>build-frontend</id>
            <goals><goal>yarn</goal></goals>
            <phase>prepare-package</phase>
            <configuration><arguments>build</arguments></configuration>
        </execution>
    </executions>
</plugin>
```

### 5. Create fm-server Module

- [ ] Create `fm-server/pom.xml`
- [ ] Move server entry point (`Application.java`)
- [ ] Move configuration packages (`io.zeitwert.fm.server.config`)
- [ ] Move session management (`io.zeitwert.fm.server.session`)
- [ ] Add dependencies on `fm-domain`, `fm-common`
- [ ] Configure Spring Boot plugin
- [ ] Configure resource copy from `fm-ui` build output to `static/`

**Copy UI resources:**
```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-ui-build</id>
            <phase>prepare-package</phase>
            <goals><goal>copy-resources</goal></goals>
            <configuration>
                <outputDirectory>${project.build.outputDirectory}/static</outputDirectory>
                <resources>
                    <resource>
                        <directory>${project.parent.basedir}/fm-ui/build</directory>
                        <filtering>false</filtering>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 6. Placeholder Modules (for Phase 2)

- [ ] Create empty `fm-dddrive/pom.xml` placeholder
- [ ] Create empty `fm-jooq-adapter/pom.xml` placeholder

### 7. Verify Build

- [ ] Run `mvn clean install` from parent
- [ ] Verify all modules compile
- [ ] Verify tests pass
- [ ] Verify application starts
- [ ] Verify UI is served correctly

## Files to Reference

| File | Purpose |
|------|---------|
| [dfp-app-server/pom.xml](../dfp-app-server/pom.xml) | Parent POM structure |
| [fm-server/pom.xml](pom.xml) | Current single-module POM |
| [dfp-app-server/dfp-app-server/pom.xml](../dfp-app-server/dfp-app-server/pom.xml) | Server module example |

## fm-ui Module Benefits

- Independent build lifecycle (can build UI separately)
- Cleaner separation of frontend/backend concerns
- Easier to manage Node.js/frontend dependencies
- Parallel development possible
- Can skip UI build during backend-only development (`-pl !fm-ui`)

## Completion Criteria

- [ ] All modules compile successfully
- [ ] All existing tests pass
- [ ] Application starts and serves UI
- [ ] No code changes to business logic (only restructuring)

