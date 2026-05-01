plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

android {
    namespace = "com.aneeque.permissionpilot"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)

    // Compose (for compose extensions)
    api(platform(libs.compose.bom))
    api(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.activity.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = providers.gradleProperty("GROUP").get()
                artifactId = providers.gradleProperty("POM_ARTIFACT_ID").get()
                version = providers.gradleProperty("VERSION_NAME").get()

                pom {
                    name.set(providers.gradleProperty("POM_NAME"))
                    description.set(providers.gradleProperty("POM_DESCRIPTION"))
                    url.set(providers.gradleProperty("POM_URL"))

                    licenses {
                        license {
                            name.set(providers.gradleProperty("POM_LICENSE_NAME"))
                            url.set(providers.gradleProperty("POM_LICENSE_URL"))
                        }
                    }

                    developers {
                        developer {
                            id.set(providers.gradleProperty("POM_DEVELOPER_ID"))
                            name.set(providers.gradleProperty("POM_DEVELOPER_NAME"))
                        }
                    }

                    scm {
                        val projectUrl = providers.gradleProperty("POM_URL").get()
                        url.set(projectUrl)
                        connection.set("scm:git:$projectUrl.git")
                        developerConnection.set("scm:git:$projectUrl.git")
                    }
                }
            }
        }
    }
}
