# Building All Module JARs in IntelliJ IDEA

## Problem
When you make changes in modules like `fm-dddrive`, the JAR file is not automatically built, causing dependency issues in other modules.

## Solutions (Choose One)

### ✅ Solution 1: Delegate Build to Maven (RECOMMENDED)

**This is the best solution** - it makes IntelliJ use Maven for all builds, ensuring JARs are created.

1. **File → Settings** (or `Ctrl+Alt+S`)
2. Navigate to: **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. ✅ Check **"Delegate IDE build/run actions to Maven"**
4. ✅ Check **"Run tests using Maven"**
5. Click **Apply** and **OK**

**Result**: Now when you press `Ctrl+F9` (Build Project), IntelliJ will use Maven, which creates JARs automatically.

---

### Solution 2: Use Maven Tool Window

1. Open **Maven** tool window: **View → Tool Windows → Maven** (or `Alt+1` then select Maven)
2. Expand your project: `fm-parent`
3. Right-click on `fm-parent` → **Lifecycle** → **install**
   - This builds all modules and installs JARs to your local Maven repository

**Quick Access**: You can also use the green play button next to `install` in the Maven tool window.

---

### Solution 3: Use Run Configurations

Two run configurations have been created for you:

1. **Run → Edit Configurations** (or `Alt+Shift+F10`)
2. You'll see:
   - **"Build All Modules"** - Builds everything with `clean install`
   - **"Build All JARs Only"** - Builds only the JAR modules (fm-common, fm-dddrive, fm-domain)

3. Select one and press `Shift+F10` to run, or click the green play button.

---

### Solution 4: Use Build Scripts

Two scripts have been created in the project root:

- **`build-all-jars.bat`** (Windows batch file)
- **`build-all-jars.ps1`** (PowerShell script)

**To use**:
1. Open IntelliJ's terminal (View → Tool Windows → Terminal)
2. Run: `.\build-all-jars.bat` or `.\build-all-jars.ps1`

---

### Solution 5: Command Line (Maven Wrapper)

In IntelliJ's terminal:

```powershell
# Build all modules
.\mvnw.cmd clean install -DskipTests

# Build specific module and its dependencies
.\mvnw.cmd clean install -pl fm-dddrive -am -DskipTests
```

**Parameters explained**:
- `-pl fm-dddrive` = build only fm-dddrive module
- `-am` = also make (build dependencies too)
- `-DskipTests` = skip tests for faster builds

---

## Recommended Setup

1. **Enable Solution 1** (Delegate to Maven) - This solves the problem permanently
2. **Keep Solution 2** (Maven Tool Window) as a backup for explicit builds
3. **Use Solution 3** (Run Configurations) for quick access via keyboard shortcuts

---

## Troubleshooting

### JARs still not being built?

1. **Refresh Maven**: Right-click on `pom.xml` → **Maven → Reload Project**
2. **Clean first**: Maven tool window → `fm-parent` → Lifecycle → `clean`
3. **Then build**: Maven tool window → `fm-parent` → Lifecycle → `install`

### Changes not reflected in dependent modules?

Make sure to use the `-am` flag when building a specific module:
```powershell
.\mvnw.cmd clean install -pl fm-dddrive -am
```

This ensures that when you build `fm-dddrive`, all modules that depend on it are also rebuilt.

### IntelliJ shows errors but Maven builds fine?

1. **Invalidate Caches**: **File → Invalidate Caches...** → **Invalidate and Restart**
2. **Reimport Maven**: Right-click on `pom.xml` → **Maven → Reload Project**

---

## Keyboard Shortcuts Reference

- `Ctrl+F9` - Build Project (uses Maven if Solution 1 is enabled)
- `Ctrl+Shift+F10` - Run last configuration
- `Shift+F10` - Run selected configuration
- `Alt+1` - Open/Close Project tool window (then select Maven)
- `Alt+Shift+X, M` - Run Maven goal (when Maven tool window is focused)

---

## Why This Happens

IntelliJ IDEA has its own build system that compiles Java/Kotlin classes but doesn't always trigger Maven's `package` phase, which creates JAR files. By delegating to Maven (Solution 1), you ensure that the full Maven lifecycle runs, including JAR creation.
