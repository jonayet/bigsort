FROM maven:3.6.2-jdk-11-slim as build

# # copy required files
WORKDIR /build
COPY pom.xml .
COPY src ./src
COPY demo/sample sample

# build package
RUN mvn package -f pom.xml

FROM openjdk:11.0.4-jre-stretch

# copy build artifacts to runtime container
WORKDIR /app
COPY --from=build /build/target/bigsort-1.0.jar .
COPY --from=build /build/sample ./sample

# run bigsort
CMD ["java", "-jar", "-DtempDir=./sample", "bigsort-1.0.jar", "./sample", "./out.txt", "500", ".txt"]