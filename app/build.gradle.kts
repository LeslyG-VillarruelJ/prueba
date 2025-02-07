plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "ec.edu.epn.nanec"
    compileSdk = 35

    defaultConfig {
        applicationId = "ec.edu.epn.nanec"
        minSdk = 25
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    // Dependencias para firebase
    implementation(libs.google.firebase.messaging)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    // Dependencias adicionales para consumo de apis de backend
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    //implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    //implementation "androidx.compose.material:material:1.5.1"
    implementation(libs.material)
    //implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0-alpha05"
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Dependencias para navegación
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    // Autenticación con Firebase
    implementation(libs.firebase.auth.ktx)
    implementation (libs.play.services.auth)
    //implementation "io.coil-kt:coil-compose:2.2.2" VISTAS PERSONALIZADAS
    implementation (libs.coil.compose)
    // Geolocalización
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    // Sockets
    implementation(libs.socket.io.client)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}