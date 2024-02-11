plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.gradle.android.cache-fix")
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("com.google.protobuf")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kotlinenjoyers.trackiteasy"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.kotlinenjoyers.trackiteasy"
        minSdk = 26
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
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            buildConfigField("Boolean", "DebugButtons", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("Boolean", "DebugButtons", "true")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    ksp {
        arg("room.generateKotlin", "true")
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.25.2"
        }

        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") {
                        option("lite")
                    }
                    create("kotlin") {
                        option("lite")
                    }
                }
            }
        }
    }
}

dependencies {
    /* KOTLIN */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.core:core-ktx:1.12.0")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    /* LIFE CYCLE */
    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    /* WEB-VIEW */
    implementation("androidx.webkit:webkit:1.10.0")

    /* WORK MANAGER */
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    /* ROOM */
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    /* COMPOSE */
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /* PROTOBUF */
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("com.google.protobuf:protobuf-javalite:3.25.2")
    implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.2")

    /* TEST */
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /* KSOUP Kotlin Parser for HTML */
    implementation("com.mohamedrejeb.ksoup:ksoup-html:0.3.1")
    implementation("com.mohamedrejeb.ksoup:ksoup-entities:0.3.1")

    /* HTML UNIT */
    implementation(group = "org.htmlunit", name = "htmlunit3-android", version = "3.7.0")

    /* XML TO JSON */
    implementation("com.github.smart-fun:XmlToJson:1.5.3")

    /* COIL */
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    /* FIREBASE */
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}