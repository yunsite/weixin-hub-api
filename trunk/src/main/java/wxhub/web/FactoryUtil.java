package wxhub.web;

import wxhub.HttpUtil;
import wxhub.auth.IAuthManager;
import wxhub.auth.AuthManagerImpl;
import wxhub.msg.IMsgManager;
import wxhub.msg.MsgManagerImpl;

public class FactoryUtil {
    private static String acctId;
    private static HttpUtil httpUtil;
    private static AuthManagerImpl authManager;
    private static MsgManagerImpl msgManager;
    
    
    public static void setAccountId(String id) {
        acctId = id;
    }
    public static String getAccountId() {
        return acctId;
    }
    
    public static IAuthManager getAuthManager() {
        if (null == authManager) {
            httpUtil = new HttpUtil();
            httpUtil.init();

            authManager = new AuthManagerImpl();
            authManager.setHttpUtil(httpUtil);
            authManager.init();
        }
        
        return authManager;
    }
    
    public static IMsgManager getMsgManager() {
        if (null == msgManager) {
            msgManager = new MsgManagerImpl();
            msgManager.setAuthManager(getAuthManager());
            msgManager.setHttpUtil(httpUtil);
        }
        
        return msgManager;
    }
}