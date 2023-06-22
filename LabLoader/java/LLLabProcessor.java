
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * the general service class for managing all lab related file manipulation
 */
public class LLLabProcessor {
    String apiURL;
    String labName;
    String zipFileName;
    String productKey;
    List<String> whitelist;

    /**
     * constructor for creating a lab processor specific to a certain lab name
     */
    public LLLabProcessor(String apiURL, String labName, String productKey, String zipFileName) throws LLCLIException {
        this.labName = labName;
        this.zipFileName = zipFileName;
        this.apiURL = apiURL;
        this.productKey = productKey;
        whitelist = new ArrayList<>();
        whitelist.add("LabLoader");
        whitelist.add(".\\.vsc");
        whitelist.add(".\\.idea");
        whitelist.add(".\\.git");
        whitelist.add(".\\packed.zip");
        whitelist.add(".\\.gitignore");
        whitelist.add(".\\labs.md");
        whitelist.add("labs.properties");
    }
    public LLLabProcessor(String apiURL, String labName, String productKey) throws LLCLIException {
        this(apiURL, labName, productKey, "out.zip");
    }

    /**
     * the main flow for pulling a lab from the api
     */
    public void processSaved() throws LLCLIException, IOException {
        clearWorkspace();
        loadSavedLabZip();
        try{
            unzipFile();
        }catch(IOException e){
            throw new LLCLIException("There was some issue unzipping your lab.");
        }
        File zipFile = new File(zipFileName);
        zipFile.delete();
        LLPropsService propsService = new LLPropsService();
        propsService.setCurrentLab(labName);
    }

    /**
     * convert input stream from web into a zip file
     * @throws URISyntaxException
     * @throws IOException
     */
    public void loadSavedLabZip() throws LLCLIException {

        try (FileOutputStream out = new FileOutputStream(zipFileName)) {
            LLWebUtil.getSavedZip(apiURL, labName, productKey).transferTo(out);
        }catch(IOException e){
            throw new LLCLIException("There was some issue loading the lab contents.");
        }
    }
    /**
     * the zip file already exists, now unzip all contents
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void unzipFile() throws IOException {
        String fileZip = "./"+zipFileName;
        File destDir = new File("./");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }
    /**
     * unpacks subdirectories of the zip file
     */
    public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public void clearWorkspace() throws IOException {
        File dir = new File("./");

        recursivelyDeleteFiles(dir);
    }

    /**
     * delete all files not matching a whitelist patter by recursively traversing the directory
     * @param dir
     * @throws IOException
     */
    public void recursivelyDeleteFiles(File dir) throws IOException {
        File[] files = dir.listFiles();
        for(File f : files){
            if(f.isDirectory()){
                recursivelyDeleteFiles(f);
            }
            boolean deleteFlag = true;
            for(String s : whitelist){

                if(f.getPath().contains(s)) {
                    deleteFlag = false;
                }

            }
            if(deleteFlag){
//                System.out.println("should be deleted: "+f.getPath());
                f.delete();
            }else{
//                System.out.println("should not be deleted: "+f.getPath());
            }
        }
    }

    /**
     * TODO: pack all of the current lab contents, excluding whitelisted files, into a zip and send the zip
     * over to API
     * @param currentLab
     */
    public void sendSaved()throws LLCLIException {
        File zip = new File("packed.zip");
        try{
        zip = pack();
        byte[] zipBytes = Files.readAllBytes(Paths.get("packed.zip"));
        LLWebUtil.sendSavedZip(apiURL, labName, productKey, zipBytes);
        }catch(IOException e){
            throw new LLCLIException("There was some issue zipping the workspace.");
        }finally{
            if(zip.exists()){
                zip.delete();
            }
        }
    }

    /**
     * package the current workspace to zip, excluding whitelist files
     * @param sourceDirPath
     * @param zipFilePath
     * @throws IOException
     */
    public File pack() throws IOException {
        Path p = Files.createFile(Paths.get("./packed.zip"));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get("./");
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path) && !containedInPath(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
            File zip = new File("packed.zip");
        }catch(IOException e){
            e.printStackTrace();
        }
        return null; 
    }
    /**
     * check if a current directory file belongs in the whitelist and should not be zipped
     * (i guess the whitelist has become a blacklist? idk)
     * @param path
     * @return
     */
    public boolean containedInPath(Path path){
        for(int i = 0; i < whitelist.size(); i++){
            System.out.println("PATH: "+path.toString() + "WHITELISTED: "+whitelist.get(i));
            if(path.toString().contains(whitelist.get(i))){
                return true;
            }
        }
        return false;
    }
}