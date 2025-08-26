import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.github.triplet.play") version "3.12.1"
}

val versionProperties = Properties().apply {
    load(FileInputStream(rootProject.file("version.properties")))
}

val signingKeystorePath = file(System.getenv("ANDROID_KEYSTORE_PATH") ?: "keystore.jks")

android {
    namespace = "eu.sailwithdamian.gpsclock"
    compileSdk = 35

    defaultConfig {
        applicationId = "eu.sailwithdamian.gpsclock"
        minSdk = 33
        targetSdk = 35
        versionCode = versionProperties["VERSION_CODE"].toString().toInt()
        versionName = versionProperties["VERSION_NAME"].toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        if (signingKeystorePath.exists()) {
            create("release") {
                storeFile = signingKeystorePath
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEYSTORE_KEY_NAME")
                keyPassword = System.getenv("ANDROID_KEYSTORE_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (file(signingKeystorePath).exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    bundle {
        storeArchive {
            enable = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tasks.register("increaseVersionCode") {
    doLast {
        versionProperties["VERSION_CODE"] = (versionProperties["VERSION_CODE"].toString().toInt() + 1).toString()
	rootProject.file("version.properties").writer().use { writer ->
            versionProperties.store(writer, null)
        }
    }
}

play {
}
