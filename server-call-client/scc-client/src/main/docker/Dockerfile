FROM bellsoft/liberica-openjdk-alpine:11.0.16 as builder
WORKDIR application
ADD maven/${project.build.finalName}.jar ${project.build.finalName}.jar
RUN java -Djarmode=layertools -jar ${project.build.finalName}.jar extract

FROM bellsoft/liberica-openjdk-alpine:11.0.16

LABEL PROJECT_NAME=${project.artifactId} \
      PROJECT=${project.id}

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
RUN true #Fake layer to fix GitHub issue `layer does not exist`
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]