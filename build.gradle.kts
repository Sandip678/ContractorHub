// Top-level build file. Do NOT add per-module dependency versions here.
// All versions live in gradle/libs.versions.toml (PART 3, ContractorHub roadmap).

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
}
