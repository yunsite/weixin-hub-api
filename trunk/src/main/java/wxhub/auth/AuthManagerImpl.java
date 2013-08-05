package wxhub.auth;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import net.sf.json.JSONObject;

import wxhub.HttpUtil;
import wxhub.PostData;
import wxhub.ResponseData;
import wxhub.HttpErrors;
import wxhub.HttpException;

/**
 * Authentication management.
 *
 * @author Tigerwang
 */
public class AuthManagerImpl implements IAuthManager {
    public static String LOGIN_URL = "https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN";
    public static Pattern TOKEN_PATTERN = Pattern.compile(".*[?&]token=(.+?)(&.*)*");
    
    private HttpUtil httpUtil;
    private Map<String, PubAccount> accts = new HashMap<String, PubAccount>();
    
    
    public void init() {
        //TBD
        saveAuth(new PubAccount("test", "xxxpass"));
    }
    
    
    /**
     * Save authentication info.
     */
    public void saveAuth(PubAccount acct) {
        accts.put(acct.getLogin(), acct);
    }
    
    /**
     * Get authentication info.
     */
    public PubAccount getAuth(String acctId) {
        return accts.get(acctId);
    }
    
    
    /**
     * Parse token from result message
     */
    private String parseToken(String msg) {
        if (null == msg)
            return null;
        
        String token = null;
        Matcher matcher = TOKEN_PATTERN.matcher(msg);
        if (matcher.matches() && matcher.groupCount() >= 1)
            token = matcher.group(1);
        
        return token;
    }
    
    /**
     * Try logon to weixin, result maybe success/error
     */
    public PubAccount logon(String acctId) {
        PubAccount acct = accts.get(acctId);
        if (null == acct)
            return null;

        //prepare form data
        PostData[] formData = new PostData[]{
                new PostData("username", acct.getLogin()),
                new PostData("pwd", acct.getPasswd()),
                new PostData("imgcode", ""),
                new PostData("f", "json")
            };
        
        long logonTime = System.currentTimeMillis();
        HttpException logonEx = null;
        String token = null;
        
        ResponseData resp = httpUtil.post(LOGIN_URL, formData);
        if (!resp.isOK()) {
            logonEx = new HttpException(HttpErrors.HTTP_RESP_ERROR, "logon response:" + resp.getStatusCode());
        }
        else {
            JSONObject result = resp.getBodyAsJSONObject();
            int errCode = result.getInt("ErrCode");
            String msg = result.getString("ErrMsg");
            
            if (errCode != 0) {
                logonEx = new HttpException(HttpErrors.AJAX_INVOKE_FAIL, "logon fail, code:" + errCode + ", msg:" + msg);
            }
            else {
                token = parseToken(msg);
                if (null == token || "".equals(token))
                    logonEx = new HttpException(HttpErrors.LOGON_TOKEN_FAIL, "logon token not found, msg:" + msg);
            }
        }
        
        //save result
        if (null != logonEx) {
            acct.errorLogon(logonTime);
            saveAuth(acct);
            throw logonEx;
        }
        else {
            acct.successLogon(logonTime, token, resp.getCookies());
            saveAuth(acct);
        }
        
        return acct;
    }
    
    /**
     * Logon expire.
     */
    public void expire(String acctId) {
        PubAccount acct = accts.get(acctId);
        if (null != acct)
            acct.expire();
    }

    /**
     * Check account logon, logon when necessary.
     */
    public PubAccount checkLogon(String acctId) {
        PubAccount acct = accts.get(acctId);
        if (null == acct)
            throw new HttpException(HttpErrors.ACCT_NOT_FOUND, "acctId:" + acctId);
        
        if (!"ON".equals(acct.getStatus()))
            acct = logon(acctId);
        
        return acct;
    }
    
    
    public void setHttpUtil(HttpUtil util) {
        this.httpUtil = util;
    }
}