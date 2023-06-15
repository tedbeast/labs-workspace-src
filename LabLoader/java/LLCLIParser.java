

import java.io.IOException;
import java.util.Scanner;

/**
 * class manages cli input to send appropriate calls of service class methods
 */
public class LLCLIParser {

    /**
     * main switch for taking in cli command
     */
    public void parseCommand() throws LLCLIException {
        LLPropsService propsService = new LLPropsService();
        System.out.println(helpOutput());
        String[] command = new Scanner(System.in).nextLine().split(" ");
        try {
            if (command.length < 1 || command[0].equals("info") || command[0].equals("help")) {
            } else if (command[0].equals("save")) {
                LLLabProcessor labProcessor = new LLLabProcessor(propsService.getCurrentLab());
                labProcessor.sendSaved(propsService.getCurrentLab());
                System.out.println("save functionality not done yet - sorry");
            } else if (command[0].equals("open")) {
                if (command.length < 2) {
                    throw new LLCLIException("Your second argument must include the lab name.");
                }
                LLLabProcessor labProcessor = new LLLabProcessor(command[1]);
                labProcessor.sendSaved(propsService.getCurrentLab());
                labProcessor.processSaved();
            }else if (command[0].equals("reset")) {
                LLLabProcessor labProcessor = new LLLabProcessor(propsService.getCurrentLab());
                LLWebUtil.sendResetRequest();
                labProcessor.processSaved();
            }
        }catch(LLCLIException e){
            System.out.println("An issue occurred: ");
            System.out.println(e.getMessage());
            System.out.println("You can send this to Ted Balashov: ");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * produce generic output on the startup of the app
     */
    public String helpOutput(){
        String str = "Welcome to the labs.jar CLI. Here is how to use the CLI:\n" +
                "open lab-name-here\n"+
                "Open a specific lab, using a lab name from the Labs.md file.\n"+
                "lab content & labloader authored by Ted Balashov 2023\n";
        return str;
    }
}