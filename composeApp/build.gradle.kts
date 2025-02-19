import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.material3)
            implementation(libs.androidx.navigation.compose)
            implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.accompanist.permissions)
            implementation(libs.mapbox)
            implementation(libs.mapboxCompose)
            implementation(libs.mapboxTurf)
            implementation(libs.koin.android)
            implementation(libs.koin.android.compose)
            implementation(libs.play.services.location)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.7.0"))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.analytics)
        }
        commonMain.dependencies {
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "uk.co.oliverdelange.location_alarm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "uk.co.oliverdelange.location_alarm"
//        testApplicationId = "uk.co.oliverdelange.location_alarm.test"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            buildConfig = true
        }
        testInstrumentationRunner = "uk.co.oliverdelange.location_alarm.TestAppJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }
    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        debug {
            //https://developer.android.com/studio/build/application-id
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)

        testImplementation(libs.mockk)
        testImplementation(libs.junit)
        testImplementation(libs.androidx.junit.ktx)
        testImplementation(libs.kotlinx.coroutines.test)

        androidTestUtil(libs.androidx.test.orchestrator)

        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(libs.androidx.espresso.intent)
        androidTestImplementation(libs.androidx.uiautomator)
        androidTestImplementation(libs.androidx.runner)
        androidTestImplementation(libs.androidx.rules)
        androidTestImplementation(libs.androidx.junit.ktx)
        androidTestImplementation(libs.androidx.ui.test.junit4)


    }
}
dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose.android)
}

