import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    `java-library`
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
    api(platform(SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))
    developmentOnly(platform(SpringBootPlugin.BOM_COORDINATES))
    testAnnotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))
    testFixturesAnnotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES))

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.mapstruct)

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // fixme... replace with new rest client
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")
    implementation("org.liquibase:liquibase-core")
    implementation(libs.flying.saucer.pdf)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.pdfbox)

    runtimeOnly("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.bundles.okhttp.mockwebserver)
    testImplementation(libs.approvaltests)

    testRuntimeOnly("com.h2database:h2")

    testFixturesAnnotationProcessor("org.projectlombok:lombok")
    testFixturesCompileOnly("org.projectlombok:lombok")
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
