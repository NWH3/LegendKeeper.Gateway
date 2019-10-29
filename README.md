# LegendKeeper.Gateway
LegendKeeper.Gateway is used as a proxy service to secure all internal microservices and provide permissions to each service for a given user.

# Technology
- Java 8
- Spring Boot 2.2.0.RELEASE
- Maven 3
- Zuul 1.4.7.RELEASE
- MongoDB

# Build & Testing

To compile the code and run test cases please run:

- mvn clean install 

If test cases need to be skipped please use the following command instead:

- mvn clean install -Dmaven.test.skip=true
