package wxhub.auth;

import java.util.Map;

/**
 * Information about public account.
 *
 * @author Tigerwang
 */
public class PubAccount {
    private String login;   //login name
    private String passwd;  //password with md5
    private String status;  //logon status, ON/OFF/EXP/ERR
    
    private String token;   //token string
    private long lastTS;    //logon timestamp
    
    private Map<String, String> cookies;
    
    
    public PubAccount() {
        this.status = "OFF";
        this.lastTS = -1L;
    }
    public PubAccount(String login, String passwd) {
        this.login = login;
        this.passwd = passwd;
        this.status = "OFF";
        this.lastTS = -1L;
    }
    
    
    public String getLogin() {
        return login;
    }
    public String getPasswd() {
        return passwd;
    }
    public String getStatus() {
        return status;
    }
    public String getToken() {
        return token;
    }
    public long getLastTS() {
        return lastTS;
    }
    public String getCookieStr() {
        if (null == cookies || cookies.size() == 0)
            return null;
        
        StringBuffer buf = new StringBuffer();
        int idx = 0;
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            if (null != entry.getValue() && !"".equals(entry.getValue())) {
                if (idx > 0)
                    buf.append("; ");
                buf.append(entry.getKey() + "=" + entry.getValue());
                
                idx++;
            }
        }
        
        return buf.toString();
    }
    
    
    public void successLogon(long ts, String token, Map<String, String> cookies) {
        this.lastTS = ts;
        this.token = token;
        this.cookies = cookies;
        this.status = "ON";
    }
    
    public void errorLogon(long ts) {
        this.lastTS = ts;
        this.status = "ERR";
    }
    
    public void expire() {
        this.status = "EXP";
        this.cookies = null;
    }
}