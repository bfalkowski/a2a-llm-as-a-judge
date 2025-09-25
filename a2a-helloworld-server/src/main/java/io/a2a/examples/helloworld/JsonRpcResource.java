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
