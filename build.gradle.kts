tasks.register("runFormSchemaApp") {

    dependsOn(gradle.includedBuild("formschema-service").task(":formschema-app:bootRun"))
}