FROM ubuntu:latest

RUN apt-get update && apt-get install openjdk-14-jre -y

ADD . .

ENV api_key=

ENTRYPOINT java -jar DiceBot-1.1-jar-with-dependencies.jar $api_key