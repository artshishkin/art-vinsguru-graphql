FROM bellsoft/liberica-openjdk-alpine:11.0.16 as builder

WORKDIR /usr/share/app

COPY *.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM bellsoft/liberica-openjdk-alpine:11.0.16

EXPOSE 7070

WORKDIR /usr/share/app
COPY --from=builder /usr/share/app/dependencies/ ./
COPY --from=builder /usr/share/app/spring-boot-loader/ ./
COPY --from=builder /usr/share/app/snapshot-dependencies/ ./
COPY --from=builder /usr/share/app/application/ ./
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]
