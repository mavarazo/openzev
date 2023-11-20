plugins {
    java
    id("java-test-fixtures")
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.openapi.generator") version "6.3.0"
}

group = "com.mav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

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

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.liquibase:liquibase-core")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")

    runtimeOnly("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.squareup.okhttp3:okhttp:4.0.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.0.1")
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
    inputSpec.set("$projectDir/src/main/resources/api.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("com.mav.openzev.api")
    modelPackage.set("com.mav.openzev.api.model")
    configOptions.set(mapOf(
            "dateLibrary" to "java8-localdatetime",
            "hideGenerationTimestamp" to "true",
            "interfaceOnly" to "true",
            "openApiNullable" to "false",
            "useSpringBoot3" to "true",
            "useTags" to "true"
    )
    )
}
