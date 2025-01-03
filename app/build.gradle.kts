plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    //DI
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

    //Download from gradle
    id("de.undercouch.download") version "5.6.0"
}

android {
    namespace = "com.example.tensorflowlitetest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tensorflowlitetest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// import DownloadModels task
project.ext.set("ASSET_DIR", "$projectDir/src/main/assets")

// Download default models; if you wish to use your own models then
// place them in the "assets" directory and comment out this line.
apply(from= "download_models.gradle")

//Downaload gesture model
apply(from= "download_gesture_model.gradle")

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

    //View model
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    //DI
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //Permissions manager
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    //Camera x to use camera
    val camerax_version = "1.4.0-rc01"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    // If you want to additionally use the CameraX View class
    implementation ("androidx.camera:camera-view:${camerax_version}")

    //Import Coil to use component AsyncImage
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Tensorflow lite
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    //implementation("com.google.ai.edge.litert:litert:1.0.1")
    implementation("com.google.android.gms:play-services-tflite-gpu:16.0.0")

    //Mediapipe
    implementation("com.google.mediapipe:tasks-vision:0.10.14")

    //Navigation with compose
    val nav_version = "2.8.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    //Navigation with Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}