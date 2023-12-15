import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("java-test-fixtures")
    id("org.springframework.boot") version "3.2.0"
    id("org.openapi.generator") version "6.3.0"
}

group = "com.mav"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

sourceSets {
    main.configure {
        java {
            srcDirs("${projectDir}/build/generated/src/main/java")
        }
    }
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))

    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("org.projectlombok:lombok:1.18.30")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")

    implementation("org.springframework.boot:spring-boot-starter:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.0.4")
    implementation("org.liquibase:liquibase-core:4.20.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
    implementation("org.apache.pdfbox:pdfbox:3.0.1")

    runtimeOnly("org.springframework.boot:spring-boot-starter-validation:3.0.4")
    runtimeOnly("org.postgresql:postgresql:42.5.4")

    developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.10.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testRuntimeOnly("com.h2database:h2:2.1.214")

    testFixturesAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testFixturesCompileOnly("org.projectlombok:lombok:1.18.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.compileJava {
    dependsOn("openApiGenerate")
}

tasks.openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/api/main.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("com.mav.openzev.api")
    modelPackage.set("com.mav.openzev.api.model")
    configOptions.set(mapOf(
            "dateLibrary" to "java8-localdatetime",
            "hideGenerationTimestamp" to "true",
            "interfaceOnly" to "true",
            "openApiNullable" to "false",
            "useSpringBoot3" to "true",
            "useTags" to "true",
            "skipDefaultInterface" to "true",
    )
    )
}

tasks.openApiValidate {
    inputSpec.set("$projectDir/src/main/resources/api/main.yaml")
}
