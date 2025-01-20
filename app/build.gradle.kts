plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.antivirus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.antivirus"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1") // OkHttp dependency
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1") // Optional logging interceptor
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit for network requests
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
// OkHttp for handling HTTP requests
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.extension.okhttp)
    implementation(libs.media3.datasource.okhttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}