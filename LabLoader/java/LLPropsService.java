

import java.io.*;

/**
 * utility class for managing the labs.properties file
 */
public class LLPropsService {
    public String propsPath = "./labs.properties";
    public String productKey;
    public String apiUrl;
    public String currentLab;

    public LLPropsService () throws LLCLIException {
        productKey = retrievePkey();
        apiUrl = retrieveUrl();
        currentLab = retrieveCurrentLab();
    }
    /**
     * grab the product key from the properties file
     * @return
     * @throws LLCLIException
     */
    public String retrievePkey() throws LLCLIException {
        if(productKey != null){
            return productKey;
        }
        try {
            BufferedReader propsReader = new BufferedReader(new FileReader(propsPath));
            String line = propsReader.readLine();
            String key = line.split("=")[1];
            return key;

        } catch (FileNotFoundException e) {
            throw new LLCLIException("Error locating properties file");
        } catch (IOException e) {
            throw new LLCLIException("Error reading properties file");
        }
    }

    /**
     * grab the url for the properties file
     * @return
     * @throws LLCLIException
     */
    public String retrieveUrl() throws LLCLIException {
        if(apiUrl != null){
            return apiUrl;
        }
        try {
            BufferedReader propsReader = new BufferedReader(new FileReader(propsPath));
//            read 2 lines
            String line = propsReader.readLine();
            line = propsReader.readLine();
            String url = line.split("=")[1];
            return url;

        } catch (FileNotFoundException e) {
            throw new LLCLIException("Error locating properties file");
        } catch (IOException e) {
            throw new LLCLIException("Error reading properties file");
        }
    }

    /**
     * grab the current lab from the properties file
     * @return
     * @throws LLCLIException
     */
    public String retrieveCurrentLab() throws LLCLIException {
        try {
            BufferedReader propsReader = new BufferedReader(new FileReader(propsPath));
            String line = propsReader.readLine();
            line = propsReader.readLine();
            line = propsReader.readLine();
            String lab = line.split("=")[1];
            return line;
        } catch (FileNotFoundException e) {
            throw new LLCLIException("Error locating properties file");
        } catch (IOException e) {
            throw new LLCLIException("Error reading properties file");
        }
    }
    /**
     * set the current lab in the properties file
     * @param labName
     * @return
     * @throws LLCLIException
     */
    public String setCurrentLab(String labName) throws LLCLIException {

        try {
            BufferedWriter propsWriter = new BufferedWriter(new FileWriter(propsPath, false));
            propsWriter.write("product_key="+productKey+"\n");
            propsWriter.write("api_url="+apiUrl+"\n");
            propsWriter.write("current_lab="+labName);
            propsWriter.close();
            return labName;
        } catch (IOException e) {
            throw new LLCLIException("There was some issue writing to your properties file.");
        }
    }

    public String getPropsPath() {
        return propsPath;
    }

    public String getCurrentLab(){
        return currentLab;
    }

    public String getProductKey() {
        return productKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

}
