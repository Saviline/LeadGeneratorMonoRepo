plugins {
    id("java-common-conventions")
    id("io.spring.dependency-management") version "1.1.7"
}



dependencyManagement {
    imports {
        // The "Newest" Version (Released Nov 2025)
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.0")
    }
}


dependencies{
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
    testImplementation("org.assertj:assertj-core:3.25.1")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
     implementation("com.networknt:json-schema-validator:1.0.87")
    implementation(project(":submission-core"))
}