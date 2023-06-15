
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * the general service class for managing all lab related file manipulation
 */
public class LLLabProcessor {
    String labName;
    String zipFileName;
    List<String> whitelist;

    /**
     * constructor for creating a lab processor specific to a certain lab name
     */
    public LLLabProcessor(String labName, String zipFileName) throws LLCLIException {
        this.labName = labName;
        this.zipFileName = zipFileName;
        whitelist = new ArrayList<>();
        whitelist.add("LabLoader");
        whitelist.add(".\\.vsc");
        whitelist.add(".\\.idea");
        whitelist.add(".\\.git");
        whitelist.add(".\\.gitignore");
        whitelist.add(".\\labs.md");
        whitelist.add("labs.properties");
    }
    public LLLabProcessor(String labName) throws LLCLIException {
        this(labName, "out.zip");
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
            LLWebUtil.getSavedZip(labName).transferTo(out);
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
    public void sendSaved(String currentLab) {
        LLWebUtil.sendSavedZip();
    }
}
