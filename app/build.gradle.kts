plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.chikegam.henwoldir"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.chikegam.henwoldir"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val isCI = System.getenv("CI") == "true"
            if (isCI) {
                val keystorePath = System.getenv("CM_KEYSTORE_PATH")
                val keystorePassword = System.getenv("CM_KEYSTORE_PASSWORD")
                val keyAlias = System.getenv("CM_KEY_ALIAS")
                val keyPassword = System.getenv("CM_KEY_PASSWORD")

                if (keystorePath.isNullOrEmpty()) {
                    throw GradleException("CM_KEYSTORE_PATH is not set!")
                }

                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.installreferrer)
    implementation(libs.appsflayer)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation (project.dependencies.platform(libs.firebase.bom))
    implementation (libs.firebase.analytics.ktx)
    implementation (libs.firebase.messaging.ktx)

    implementation (libs.play.services.ads.identifier)

    implementation (libs.androidx.navigation.ui.ktx)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    implementation (libs.androidx.preference.ktx)

    implementation (libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.coroutines.android)

    implementation (libs.androidx.fragment.ktx)

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.kotlinx.serialization)

    implementation(libs.kotlinx.coroutines.android)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}