plugins {
    id("java-common-conventions")
}

dependencies {
    // Reactive types for async interfaces
    implementation("io.projectreactor:reactor-core:3.6.0")

    // Test dependencies
    testImplementation("io.projectreactor:reactor-test:3.6.0")
}
