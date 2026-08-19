FROM eclipse-temurin:26-jre
ARG H2_VERSION=2.4.240
ADD https://repo1.maven.org/maven2/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar /opt/h2/h2.jar
RUN chmod 0444 /opt/h2/h2.jar && mkdir /data && chown 10001:10001 /data
COPY --chmod=755 entrypoint.sh /entrypoint.sh
USER 10001
EXPOSE 9092
ENTRYPOINT ["/entrypoint.sh"]

