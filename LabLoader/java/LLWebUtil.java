import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

/**
 * utility class containing static methods for sending http requests to api
 */
public class LLWebUtil {

    /**
     * retrieve the current users zip file for a specific lab
     */
    public static InputStream getSavedZip(String apiURL, String labName, String productKey) throws LLCLIException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/lab/"+labName))
                    .GET()
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {

            throw new LLCLIException("There was some error retrieving your lab from the cloud.");
        }
//        going to try to make this an httpresponse later so that we can do better error handling
        InputStream is = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body).join();

        return is;
    }
    /**
     * TODO send the saved lab zip to the api
     */
    public static void sendSavedZip(String apiURL, String labName, String productKey, byte[] zip) throws LLCLIException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/lab-save-request/"+labName))
                    .POST(BodyPublishers.ofByteArray(zip))
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {

            throw new LLCLIException("There was some error saving your lab to the cloud.");
        }
    }
    /**
     * TODO send a request to reset the users lab to the api
     * @throws LLCLIException
     */
    public static void sendResetRequest(String apiURL, String labName, String productKey) throws LLCLIException {
         HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(apiURL+"/lab-reset-request/"+labName))
                    .POST(null)
                    .header("product_key", productKey)
                    .build();
        } catch (URISyntaxException e) {

            throw new LLCLIException("There was some error resetting your lab on the cloud.");
        }
    }
}