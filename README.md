# Free Camera Mod

A client-side free camera mod for **Mindustry V8 (desktop/Windows)**.

## Controls

- **Hold Alt + WASD:** move the camera without moving the unit.
- **Release Alt:** keep the camera detached; WASD controls your unit again.
- **Home:** return camera to the player.
- **Settings → Game:** adjust camera speed from 50% to 5000%.

## Install

Download the compiled `.jar` from **GitHub Releases** and import it via Mindustry → Mods → Import File. **Do not import the repository ZIP**: it contains Java source code, not compiled classes.

## Build on Windows

Requires JDK 17 and Gradle 8.x. Run from the repository root:

```powershell
gradle clean jar
```

The result is `build/libs/free-camera-java.jar`. Attach this JAR to a GitHub Release (e.g. `v1.0.0`).

## Notes

- Mod ID remains `free-camera-java` to preserve compatibility with existing installs.
- The display name is **Free Camera Mod**.
- `hidden: true` is enabled for client-side behavior; individual servers may enforce their own policies.
- The source is based on the previously tested Java implementation. Changing the display name and icon does not change camera logic.

## Credits

Free-camera input approach inspired by the Mindustry client ecosystem. Review licenses and attribution before reusing external code.
