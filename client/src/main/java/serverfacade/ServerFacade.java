package serverfacade;

import com.google.gson.Gson;
import requestresult.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String baseUrl;
    private final HttpClient http;
    private final Gson gson;
    private String authToken;

    public ServerFacade(int port) {
        this.baseUrl = "http://localhost:" + port;
        this.http = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void clear() throws ServerFacadeException {
        sendRequest("DELETE", "/db", null, null,null,null);
        authToken = null;
    }

    public RegisterResult register(RegisterRequest request) throws ServerFacadeException {
        String body = sendRequest("POST", "/user",
                request, null, "That username is already taken.", null);
        RegisterResult result = gson.fromJson(body, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    public LoginResult login(LoginRequest request) throws ServerFacadeException {
        String body = sendRequest("POST", "/session",
                request, null, null, "Wrong username or password.");
        LoginResult result = gson.fromJson(body, LoginResult.class);
        authToken = result.authToken();
        return result;
    }

    public void logout() throws ServerFacadeException {
        sendRequest("DELETE", "/session",
                null, authToken, null, null);
        authToken = null;
    }

    //Helper: Sends any request; returns the response body as a string or throws an error
    private String sendRequest(String method, String path, Object body,
                               String authToken, String conflictMessage, String unauthorizedMessage)
            throws ServerFacadeException {
        String json;
        if (body == null) {json = null;} else {
            json = gson.toJson(body);
        }

        // Build the request
        HttpRequest.Builder builderHttpRequest = HttpRequest.newBuilder(URI.create(baseUrl + path))
                //Previous try I used .POST etc; .method() is longer but allows me to make it universal.
                .method(method, bodyPublisher(json));
        // Syntax won't allow null here ^^^, so bodyPublisher passes it in as empty if its null.
        if (authToken != null) {
            builderHttpRequest.header("authorization", authToken);
        }

        // Sends the request and gets a response.
        HttpResponse<String> response;
        try {
            response = http.send(builderHttpRequest.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ServerFacadeException("Could not reach the server.");
        } catch (InterruptedException e) {
            throw new ServerFacadeException("The request was interrupted.");
        }
        //Checks for error codes, throws as appropriate based on the status code.
        // Doesn't return - just throws instead.
        checkStatus(response.statusCode(), conflictMessage, unauthorizedMessage);

        // Returns the string of the body.
        return response.body();
    }

    // A helper function that passes in no body if its null. Just keeps the previous function slimmer.
    private HttpRequest.BodyPublisher bodyPublisher(String json) {
        if (json != null) {
            return HttpRequest.BodyPublishers.ofString(json);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private void checkStatus(int status, String conflictMessage, String unauthorizedMessage) throws ServerFacadeException {
        if (status == 200) {
            return; //If it succeeded (200), then it continues back to the request function.
        }
        if (status == 400) {
            throw new ServerFacadeException("Missing or invalid information");
        }
        if (status == 401 && unauthorizedMessage != null) {
            throw new ServerFacadeException(unauthorizedMessage);
        }
        if (status == 401 && unauthorizedMessage == null) {
            throw new ServerFacadeException("You are not logged in.");
        }
        if (status == 403 && conflictMessage != null) {
            throw new ServerFacadeException(conflictMessage);
        }
        if (status == 500) {
            throw new ServerFacadeException("Server error. Please try again.");
        }
        throw new ServerFacadeException("Something went wrong. Try again,"); //Catch all in case the other if statements don't work.
    }

 }
