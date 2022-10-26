FROM eclipse-temurin:18
RUN mkdir /opt/app
COPY ./target/DiceBot-1.2-jar-with-dependencies.jar /opt/app
ENV api_key=
CMD ["sh", "-c", "java -jar \"/opt/app/DiceBot-1.2-jar-with-dependencies.jar\" ${api_key}"]