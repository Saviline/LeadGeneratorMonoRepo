plugins {
    id("java-common-conventions")
    id("io.spring.dependency-management") version "1.1.7"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.0")
    }
}

dependencies {
    // Core module
    implementation(project(":campaign-core"))

    // Reactive PostgreSQL (R2DBC)
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")
    implementation("org.postgresql:postgresql") // For schema migrations

    // Reactive MongoDB (for FormSchema validation lookups)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    // RabbitMQ for messaging
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // Jackson for JSON serialization
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Test dependencies
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
