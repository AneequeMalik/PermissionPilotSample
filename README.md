# PermissionPilot

PermissionPilot is a lifecycle-aware Android runtime permission library for Activities, Fragments, and Jetpack Compose.

## Installation

### JitPack

Publish the repository to GitHub, create a release tag such as `v0.1.0`, then add JitPack to the consuming app:

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
    implementation("com.github.aneeque:PermissionPilot:0.1.0")
}
```

If your GitHub owner or repository name is different, replace `aneeque` and `PermissionPilot` in the dependency coordinate.

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
                // Use the camera.
            },
            onDenied = {
                // Show rationale or retry option.
            },
            onPermanentlyDenied = {
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
                is PermissionResult.Granted -> Unit
                is PermissionResult.Denied -> Unit
                is PermissionResult.PermanentlyDenied -> Unit
            }
        }
    )

    Button(onClick = cameraPermission.launchRequest) {
        Text("Request camera")
    }
}
```

## Publishing

Publication metadata lives in `gradle.properties`:

```properties
GROUP=com.aneeque
POM_ARTIFACT_ID=permissionpilot
VERSION_NAME=0.1.0
POM_URL=https://github.com/aneeque/PermissionPilot
```

Before releasing, update `VERSION_NAME`, make sure `POM_URL` matches the GitHub repository, and run:

```bash
./gradlew clean :permissionpilot:testReleaseUnitTest :permissionpilot:publishReleasePublicationToMavenLocal
```

For Maven Central, you will also need Sonatype Central Portal credentials and signing configuration.

## License

MIT
