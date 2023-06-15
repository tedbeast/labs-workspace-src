import java.io.*;
import java.net.URISyntaxException;

public class LabLoaderApplication {
    /**
     * start up the cli parser to get cli input
     */
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, LLCLIException {
        LLCLIParser LLCLIParser = new LLCLIParser();
        LLCLIParser.parseCommand();

    }
}