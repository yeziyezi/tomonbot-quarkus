package one.yezii.tomon;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    private final static HttpClient httpClient = HttpClient.newHttpClient();

    private static String token = null;

    public static void setToken(String token) {
        HttpUtil.token = token;
    }

    public static HttpResponse<String> doPost(String url, String data) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .setHeader("Content-Type", "application/json");
        return sendRequest(builder);
    }

    public static HttpResponse<String> doGet(String url) throws URISyntaxException, IOException, InterruptedException {
        return sendRequest(HttpRequest.newBuilder(new URI(url)).GET());
    }

    private static HttpResponse<String> sendRequest(HttpRequest.Builder builder) throws IOException, InterruptedException {
        if (token != null) {
            builder.setHeader("authorization", "Bearer " + token);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
}
