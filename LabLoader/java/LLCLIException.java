/**
 * general exception for when something goes wrong with the cli for sending out neat output
 */
public class LLCLIException extends Exception{
    public LLCLIException(String msg){
        super(msg);
    }
}
