package wxhub.msg;

import wxhub.HttpUtil;
import wxhub.auth.AuthManagerImpl;

/**
 * Message test.
 *
 * @author Tigerwang
 */
public class MsgTest {
    private static String acctId = "test";
    
    private static HttpUtil httpUtil;
    private static AuthManagerImpl authManager;
    private static MsgManagerImpl msgManager;
    
    static {
        //initialize manager
        httpUtil = new HttpUtil();
        httpUtil.init();
        
        authManager = new AuthManagerImpl();
        authManager.setHttpUtil(httpUtil);
        authManager.init();
        
        msgManager = new MsgManagerImpl();
        msgManager.setHttpUtil(httpUtil);
        msgManager.setAuthManager(authManager);
    }
    
    
    private static void sendSingle(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }
        
        String fakeIds = args[1];
        msgManager.sendSingleMsg(acctId, fakeIds, Message.textMsg(args[2]));
    }
    
    private static void sendMass(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }
        
        int group = Integer.parseInt(args[1]);
        msgManager.sendMassMsg(acctId, new MassTarget(group), Message.textMsg(args[2]));
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }
        
        try {
            if ("-s".equals(args[0])) {
                sendSingle(args);
                System.out.println("send single message done.");
            }
            else if ("-m".equals(args[0])) {
                sendMass(args);
                System.out.println("send mass message done.");
            }
            else {
                printUsage();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void printUsage() {
        System.out.println("Single Message: MsgTest -s <target_fakes> <text_content>");
        System.out.println("Mass Message: MsgTest -m <target_group> <text_content>");
    }
}