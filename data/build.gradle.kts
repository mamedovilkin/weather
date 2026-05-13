import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.github.mamedovilkin.weather.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()
        localProperties.load(FileInputStream(localPropertiesFile))

        buildConfigField("String", "BASE_URL", localProperties["BASE_URL"].toString())
        buildConfigField("String", "GEOCODING_URL", localProperties["GEOCODING_URL"].toString())
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.play.services.location)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}