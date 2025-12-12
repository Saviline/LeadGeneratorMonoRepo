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


    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation(project(":formschema-core"))
}