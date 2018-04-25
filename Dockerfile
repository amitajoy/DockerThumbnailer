#FROM ImageName
FROM docker.<server>.com:8000/lcapp:latest
#Other image examples:
#FROM java:8
#FROM aoesterer/im:v1
# The EXPOSE instruction informs Docker that the container
# listens on the specified network ports at runtime.
EXPOSE 8080
#ADD copies from source to destination
ADD /target/thumbnailer-0.0.1-SNAPSHOT.jar thumbnailer.jar
#ENTRYPOINT allows you specify a command that will
# execute when you run the container
ENTRYPOINT ["java","-jar","thumbnailer.jar"]