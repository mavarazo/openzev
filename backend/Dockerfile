FROM eclipse-temurin:22-jre AS builder

WORKDIR /builder

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract --destination extracted

FROM eclipse-temurin:22-jre

WORKDIR /app

COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

CMD java $JAVA_OPTS $JAVA_OPTS_EXT org.springframework.boot.loader.launch.JarLauncher