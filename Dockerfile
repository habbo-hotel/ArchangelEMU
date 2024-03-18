FROM maven:3-eclipse-temurin-19 AS builder

# Copy the Emulator sources to the container
COPY . .
# Package it
RUN mvn package && mv /target/Morningstar*-with-dependencies.jar /target/Morningstar.jar

# Use Java 19 for running
FROM eclipse-temurin:19 AS runner

# Copy the generated source
COPY --from=builder /target/Morningstar.jar /

# Save the script to wait for the database, among running the Arcturus Emulator
RUN echo "#!/bin/bash \n java -Dfile.encoding=UTF-8 -jar /Morningstar.jar" > /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Run the Emulator with Java
ENTRYPOINT ["/entrypoint.sh"]
