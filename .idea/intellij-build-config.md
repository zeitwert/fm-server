# IntelliJ IDEA - Building All JARs Automatically

This guide explains how to configure IntelliJ IDEA to automatically build all module JARs when you make changes.

## Quick Solutions

### Option 1: Use Maven Tool Window (Recommended)

1. Open the **Maven** tool window (View → Tool Windows → Maven)
2. Expand your project root (`fm-parent`)
3. Right-click on `fm-parent` → `Lifecycle` → `install`
   - This builds all modules and installs JARs to your local Maven repository
4. You can also use `package` to build JARs without installing

**Shortcut**: You can create a Maven run configuration for quick access:
- Run → Edit Configurations → `+` → Maven
- Name: "Build All Modules"
- Working directory: `$PROJECT_DIR$`
- Command line: `clean install -DskipTests`
- Click OK, then use `Ctrl+Shift+F10` or the run button

### Option 2: Configure IntelliJ to Delegate Build to Maven

1. Go to **File → Settings** (or `Ctrl+Alt+S`)
2. Navigate to **Build, Execution, Deployment → Build Tools → Maven → Runner**
3. Check **"Delegate IDE build/run actions to Maven"**
4. Select **"Run tests using Maven"**
5. Click **Apply** and **OK**

Now when you press `Ctrl+F9` (Build Project), IntelliJ will use Maven to build, which ensures JARs are created.

### Option 3: Enable Auto-Import and Build on Save

1. **File → Settings → Build, Execution, Deployment → Compiler**
2. Check **"Build project automatically"**
3. **File → Settings → Build, Execution, Deployment → Build Tools → Maven → Importing**
4. Check **"Import Maven projects automatically"**
5. Check **"Automatically download sources and documentation"**

### Option 4: Create a Build Script (Quick Access)

Create a run configuration:
1. **Run → Edit Configurations**
2. Click `+` → **Maven**
3. Configure:
   - **Name**: Build All JARs
   - **Working directory**: `$PROJECT_DIR$`
   - **Command line**: `clean install -DskipTests -pl fm-dddrive,fm-domain -am`
     - `-pl` specifies which modules to build
     - `-am` builds dependencies too
4. Save and use `Shift+F10` to run

### Option 5: Use Maven Wrapper from Terminal

In IntelliJ's terminal:
```powershell
.\mvnw.cmd clean install -DskipTests
```

Or for specific modules:
```powershell
.\mvnw.cmd clean install -pl fm-dddrive -am
```

## Troubleshooting

### JARs Not Being Built

1. **Check Maven Tool Window**: Ensure all modules are imported correctly
2. **Refresh Maven Project**: Right-click on `pom.xml` → Maven → Reload Project
3. **Clean and Rebuild**: Maven tool window → `fm-parent` → Lifecycle → `clean` → then `install`

### Changes Not Reflected

1. Make sure you're using Maven build (Option 2 above)
2. Or manually run `install` from Maven tool window after changes
3. Check that dependent modules are rebuilt: Use `-am` flag (also make)

### IntelliJ Build vs Maven Build

- **IntelliJ Build** (`Ctrl+F9`): Compiles classes but may not create JARs
- **Maven Build**: Full lifecycle including JAR creation
- **Solution**: Use Option 2 to delegate to Maven, or always use Maven tool window

## Recommended Workflow

1. **Enable Option 2** (Delegate to Maven) - This is the most reliable
2. **Use Maven Tool Window** for explicit builds when needed
3. **Create a Run Configuration** (Option 4) for quick access to build all modules

## Keyboard Shortcuts

- `Ctrl+F9`: Build Project (uses Maven if Option 2 is enabled)
- `Ctrl+Shift+F10`: Run last configuration
- `Shift+F10`: Run selected configuration
- `Alt+Shift+X, M`: Run Maven goal (if Maven tool window is focused)
