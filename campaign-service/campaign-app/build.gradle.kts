plugins {
    id("java-common-conventions")
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.0")
    }
}

dependencies {
    // Project modules
    implementation(project(":campaign-core"))
    implementation(project(":campaign-detail"))

    // Spring WebFlux (Reactive Web)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //Tracing
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // Loki4j: Sends logs directly to Loki via HTTP
    implementation("com.github.loki4j:loki-logback-appender:1.5.1")
    
    // SQL Observation: Automatically traces Repository/DB calls
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.1")

    // Reactive PostgreSQL (R2DBC)
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")

    // Reactive MongoDB (for FormSchema lookups)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    // RabbitMQ for async messaging
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // OAuth2 Resource Server (JWT validation)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Actuator & Metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Test dependencies
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
