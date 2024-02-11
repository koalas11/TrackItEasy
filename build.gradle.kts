// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.gradle.android.cache-fix") version "3.0" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("androidx.room") version "2.6.1" apply false
    id("com.google.protobuf") version "0.9.4" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}