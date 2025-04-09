// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
buildscript {
    dependencies {
        // ✅ Add this here to enable Firebase plugin support
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
    }
}