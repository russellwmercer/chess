package serverfacade;

import com.google.gson.Gson;
import java.net.http.HttpClient;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient http;
    private final Gson gson;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.http = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
 }
