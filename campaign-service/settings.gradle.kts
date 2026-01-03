
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "campaign-service"
include("campaign-app")
include("campaign-core")
include("campaign-detail")
