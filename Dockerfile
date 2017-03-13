FROM ahaines/jre-alpine

RUN apk --no-cache add ca-certificates \
 && update-ca-certificates

COPY target/uberjar/karma-tracker-*-standalone.jar /karma-tracker.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/karma-tracker.jar"]
