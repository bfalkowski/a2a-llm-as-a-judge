#!/bin/bash

# Heroku-compatible A2A Hello World Server
echo "Starting A2A Hello World Server for Heroku deployment..."

# Heroku provides PORT environment variable
export PORT=${PORT:-8080}
echo "Using port: $PORT"

# Show Java version
echo "Using Java version:"
java -version

# Create the A2A HelloWorld server structure
echo "Setting up A2A HelloWorld server..."

# Create directory structure
mkdir -p a2a-helloworld-server/src/main/java/io/a2a/examples/helloworld
mkdir -p a2a-helloworld-server/src/main/resources

# ---------------------------------------------------------------------
# pom.xml
# ---------------------------------------------------------------------
cat > a2a-helloworld-server/pom.xml << 'EOF'
<?xml version="1.0"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>io.a2a.examples</groupId>
  <artifactId>helloworld-server-heroku</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <quarkus.platform.group-id>io.quarkus.platform</quarkus.platform.group-id>
    <quarkus.platform.artifact-id>quarkus-bom</quarkus.platform.artifact-id>
    <quarkus.platform.version>3.2.9.Final</quarkus.platform.version>
  </properties>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>${quarkus.platform.group-id}</groupId>
        <artifactId>${quarkus.platform.artifact-id}</artifactId>
        <version>${quarkus.platform.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-arc</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-reactive</artifactId>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>${quarkus.platform.group-id}</groupId>
        <artifactId>quarkus-maven-plugin</artifactId>
        <version>${quarkus.platform.version}</version>
        <extensions>true</extensions>
        <executions>
          <execution>
            <goals>
              <goal>build</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
EOF

# ---------------------------------------------------------------------
# AgentCardResource.java
# ---------------------------------------------------------------------
cat > a2a-helloworld-server/src/main/java/io/a2a/examples/helloworld/AgentCardResource.java << 'EOF'
package io.a2a.examples.helloworld;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.List;

@Path("/agent")
public class AgentCardResource {

    @GET
    @Path("/card")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAgentCard() {
        return Map.of(
            "name", "HelloWorld A2A Agent (Heroku)",
            "description", "A simple A2A agent for testing connections on Heroku",
            "version", "1.0.0",
            "transport", Map.of(
                "type", "JSON-RPC",
                "endpoint", "/jsonrpc"
            ),
            "capabilities", List.of("greeting", "echo", "heroku-info")
        );
    }

    @GET
    @Path("/extendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getExtendedAgentCard() {
        return getAgentCard();
    }

    @GET
    @Path("/authenticatedExtendedCard")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAuthenticatedExtendedAgentCard() {
        return getAgentCard();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "platform", "Heroku",
            "timestamp", System.currentTimeMillis()
        );
    }
}
EOF

# ---------------------------------------------------------------------
# JsonRpcResource.java
# ---------------------------------------------------------------------
cat > a2a-helloworld-server/src/main/java/io/a2a/examples/helloworld/JsonRpcResource.java << 'EOF'
package io.a2a.examples.helloworld;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.HashMap;

@Path("/jsonrpc")
public class JsonRpcResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> handleJsonRpc(Map<String, Object> request) {
        String method = (String) request.get("method");
        Object params = request.get("params");
        Object id = request.get("id");

        Object result;
        switch (method) {
            case "greeting":
                result = Map.of("message", "Hello from A2A HelloWorld Agent running on Heroku!");
                break;
            case "echo":
                result = Map.of("echo", params);
                break;
            case "heroku-info":
                Map<String, Object> herokuInfo = new HashMap<>();
                herokuInfo.put("dyno", System.getenv("DYNO"));
                herokuInfo.put("release_version", System.getenv("HEROKU_RELEASE_VERSION"));
                herokuInfo.put("slug_commit", System.getenv("HEROKU_SLUG_COMMIT"));
                herokuInfo.put("port", System.getenv("PORT"));
                result = Map.of("heroku_environment", herokuInfo);
                break;
            default:
                return Map.of(
                    "jsonrpc", "2.0",
                    "error", Map.of(
                        "code", -32601,
                        "message", "Method not found: " + method
                    ),
                    "id", id
                );
        }

        return Map.of(
            "jsonrpc", "2.0",
            "result", result,
            "id", id
        );
    }
}
EOF

# ---------------------------------------------------------------------
# application.properties
# ---------------------------------------------------------------------
cat > a2a-helloworld-server/src/main/resources/application.properties << 'EOF'
quarkus.http.host=0.0.0.0
quarkus.http.port=${PORT:8080}

# CORS for A2A
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=*

# Logging
quarkus.log.level=INFO
quarkus.log.console.enable=true
quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n
EOF

# ---------------------------------------------------------------------
# system.properties
# ---------------------------------------------------------------------
cat > system.properties << 'EOF'
java.runtime.version=17
EOF

# ---------------------------------------------------------------------
# Build & Run
# ---------------------------------------------------------------------
echo "Building A2A HelloWorld server for Heroku..."
cd a2a-helloworld-server

mvn -B -DskipTests package
if [ $? -ne 0 ]; then
    echo "Failed to build A2A HelloWorld server"
    exit 1
fi

echo "Starting Quarkus app on port $PORT..."
exec java $JAVA_OPTS -jar target/quarkus-app/quarkus-run.jar
