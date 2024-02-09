import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies{
    const val core_ktx = "androidx.core:core-ktx:${Versions.androidx_core_ktx}"
    const val lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle_runtime}"
    const val activity_compose = "androidx.activity:activity-compose:${Versions.androidx_activity_compose}"
    const val compose_bom = "androidx.compose:compose-bom:${Versions.androidx_compose_bom}"
    const val compose_ui = "androidx.compose.ui:ui:${Versions.androidx_compose_ui}"
    const val compose_ui_graphics = "androidx.compose.ui:ui-graphics:${Versions.androidx_compose_ui}"
    const val compose_ui_tooling_preview = "androidx.compose.ui:ui-tooling-preview:${Versions.androidx_compose_ui}"
    const val compose_material_3 = "androidx.compose.material3:material3:${Versions.compose_material3}"
    const val google_accompanist_pager = "com.google.accompanist:accompanist-pager:${Versions.google_accompanist_pager}"
    const val google_accompanist_pager_indicators = "com.google.accompanist:accompanist-pager-indicators:${Versions.google_accompanist_pager}"
    const val chargemap_compose_numberpicker = "com.chargemap.compose:numberpicker:${Versions.chargemap_compose_numberpicker}"
    const val apache_commons_lang3 = "org.apache.commons:commons-lang3:${Versions.apache_commons_lang3}"
    const val junit = "junit:junit:${Versions.junit}"
    const val test_ext_junit = "androidx.test.ext:junit:${Versions.androidx_test_ext_junit}"
    const val test_espresso_core = "androidx.test.espresso:espresso-core:${Versions.androidx_test_espresso_core}"
    const val compose_ui_test_junit = "androidx.compose.ui:ui-test-junit4:${Versions.androidx_compose_ui}"
    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.androidx_compose_ui}"
    const val compose_ui_test_manifest = "androidx.compose.ui:ui-test-manifest:${Versions.androidx_compose_ui}"
}

fun DependencyHandler.core(){
    implementation(Dependencies.core_ktx)
    implementation(Dependencies.lifecycle_runtime)
}
fun DependencyHandler.compose(){
    implementation(Dependencies.activity_compose)
    implementation(Dependencies.compose_ui)
    implementation(Dependencies.compose_ui_graphics)
    implementation(Dependencies.compose_ui_tooling_preview)
    implementation(Dependencies.compose_material_3)
    implementationPlatform(Dependencies.compose_bom)
    debugImplementation(Dependencies.compose_ui_tooling)
    debugImplementation(Dependencies.compose_ui_test_manifest)
}

fun DependencyHandler.accompanist(){
    implementation(Dependencies.google_accompanist_pager)
    implementation(Dependencies.google_accompanist_pager_indicators)
}

fun DependencyHandler.numberPicker(){
    implementation(Dependencies.chargemap_compose_numberpicker)
}

fun DependencyHandler.apacheCommons(){
    implementation(Dependencies.apache_commons_lang3)
}

fun DependencyHandler.test(){
    testImplementation(Dependencies.junit)
}

fun DependencyHandler.androidTest(){
    androidTestImplementation(Dependencies.test_ext_junit)
    androidTestImplementation(Dependencies.test_espresso_core)
    androidTestImplementation(Dependencies.compose_ui_test_junit)
}