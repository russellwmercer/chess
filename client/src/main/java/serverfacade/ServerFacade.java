package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient http;
    private final Gson gson;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.http = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void clear() throws ServerFacadeException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/db"))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ServerFacadeException("Could not clear.");
            }
        } catch (IOException | InterruptedException e) {
            throw new ServerFacadeException("Could not reach the server.");
        }
    }
 }
