FROM ubuntu:latest

RUN apt-get update && apt-get install openjdk-18-jre -y

ADD . .

ENV api_key=

ENTRYPOINT java -jar DiceBot-1.2-jar-with-dependencies.jar $api_key