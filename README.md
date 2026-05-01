# PermissionPilot

PermissionPilot is a lifecycle-aware Android runtime permission library for Activities, Fragments, and Jetpack Compose.

## Installation

### JitPack

Add JitPack to your project-level `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.AneequeMalik:PermissionPilotSample:v0.1.0")
}
```

If your GitHub owner or repository name is different, replace `aneeque` and `PermissionPilot` 
in the dependency coordinate.

### Local Maven

To publish locally for testing:

```bash
./gradlew :permissionpilot:publishToMavenLocal
```

Then consume it with:

```kotlin
repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.aneeque:permissionpilot:0.1.0")
}
```

## Activity Usage

Create the instance before the Activity reaches the started state, for example as a property or in `onCreate`.

```kotlin
class MainActivity : ComponentActivity() {
    private val permissionPilot by lazy {
        PermissionPilot.create(this)
    }

    fun requestCamera() {
        permissionPilot.requestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                // Permission granted. Use the camera.
            },
            onDenied = {
                // Permission denied. Show rationale or retry option.
            },
            onPermanentlyDenied = {
                // Permission permanently denied. Open app settings.
                permissionPilot.openAppSettings()
            }
        )
    }
}
```

## Fragment Usage

```kotlin
class CameraFragment : Fragment() {
    private val permissionPilot by lazy {
        PermissionPilot.create(this)
    }

    fun requestCamera() {
        permissionPilot.requestPermission(
            permission = Manifest.permission.CAMERA,
            onGranted = {
                // Permission granted.
            },
            onDenied = {
                // Permission denied.
            },
            onPermanentlyDenied = {
                permissionPilot.openAppSettings()
            }
        )
    }
}
```

## Compose Usage

```kotlin
@Composable
fun CameraPermissionButton() {
    val cameraPermission = rememberPermissionPilot(
        permission = Manifest.permission.CAMERA,
        onResult = { result ->
            when (result) {
                is PermissionResult.Granted -> {
                    // Permission granted.
                }

                is PermissionResult.Denied -> {
                    // Permission denied.
                }

                is PermissionResult.PermanentlyDenied -> {
                    // Permission permanently denied.
                }
            }
        }
    )

    Button(onClick = cameraPermission.launchRequest) {
        Text("Request Camera")
    }
}
```

## Publishing

Current JitPack artifact:

```kotlin
implementation("com.github.AneequeMalik:PermissionPilotSample:v0.1.0")
```
## License

MIT
