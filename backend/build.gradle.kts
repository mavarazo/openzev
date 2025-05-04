plugins {
    `java`
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.12.0"
}

group = "com.mav"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
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
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.mapstruct)

    annotationProcessor(libs.hibernate.jpamodelgen)

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
    modelNameSuffix.set("Dto")

    configOptions.set(
        mapOf(
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
