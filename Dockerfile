FROM java:8
EXPOSE 8084
#RUN mkdir /usr/src/app
#WORKDIR /usr/src/app

COPY ./sap_data_lake.jar sapdatalake.jar

ENTRYPOINT ["java", "-jar", "sapdatalake.jar"]
