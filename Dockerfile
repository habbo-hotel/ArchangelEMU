FROM maven:latest AS builder

# Copy the Emulator sources to the container
COPY . .
# Package it
RUN mvn package && mv /target/Morningstar*-with-dependencies.jar /target/Morningstar.jar

# Use openjdk 11 for running
FROM openjdk:11 AS runner

# Copy the generated source
COPY --from=builder /target/Morningstar.jar /

# Save the script to wait for the database, among running the Arcturus Emulator
RUN echo "#!/bin/bash \n java -Dfile.encoding=UTF-8 -jar /Morningstar.jar" > /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Run the Emulator with Java
ENTRYPOINT ["/entrypoint.sh"]
