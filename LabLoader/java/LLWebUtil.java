import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * utility class containing static methods for sending http requests to api
 */
public class LLWebUtil {

    /**
     * retrieve the current users zip file for a specific lab
     */
    public static InputStream getSavedZip(String labName) throws LLCLIException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = null;
        LLPropsService propsService = new LLPropsService();
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(propsService.getApiUrl()+"/lab/"+labName))
                    .GET()
                    .header("product_key", propsService.getProductKey())
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
    public static void sendSavedZip() {

    }
    /**
     * TODO send a request to reset the users lab to the api
     */
    public static void sendResetRequest() {
    }
}