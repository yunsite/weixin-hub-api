package wxhub.msg;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import net.sf.json.JSONObject;

import wxhub.HttpUtil;
import wxhub.PostData;
import wxhub.ResponseData;
import wxhub.HttpException;
import wxhub.HttpErrors;
import wxhub.auth.IAuthManager;
import wxhub.auth.PubAccount;

/**
 * Message send/receive manager.
 *
 * @author Tigerwang
 */
public class MsgManagerImpl implements IMsgManager {
    public static final String SINGLE_MSG_URL = "https://mp.weixin.qq.com/cgi-bin/singlesend?t=ajax-response&lang=zh_CN";
    public static final String MASS_MSG_URL = "https://mp.weixin.qq.com/cgi-bin/masssend?t=ajax-response&lang=zh_CN";
    
    private IAuthManager authManager;
    private HttpUtil httpUtil;
    
    /**
     * Perform single message send.
     */
    private void doSingleSend(PubAccount acct, String destId, Message msg, boolean retryLogon) {
        //prepare form data
        PostData[] formData = null;
        if (msg.getType() == Message.TYPE_TEXT) {
            formData = new PostData[] {
                new PostData("ajax", "1"),
                new PostData("content", msg.getTextContent()),
                new PostData("error", "false"),
                new PostData("imgcode", ""),
                new PostData("tofakeid", destId),
                new PostData("token", acct.getToken()),
                new PostData("type", "1")
            };
        }
        
        //prepare header
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "https://mp.weixin.qq.com/cgi-bin/singlemsgpage");
        String cookieStr = acct.getCookieStr();
        if (null != cookieStr)
            headers.put("Cookie", cookieStr);
        
        HttpException sendEx = null;
        ResponseData resp = httpUtil.post(SINGLE_MSG_URL, formData, headers);
        if (!resp.isOK()) {
            sendEx = new HttpException(HttpErrors.HTTP_RESP_ERROR, "singlesend response:" + resp.getStatusCode());
        }
        else {
            JSONObject result = resp.getBodyAsJSONObject();
            int errCode = result.getInt("ret");
            String errMsg = result.getString("msg");
            
            if (errCode != 0) {
                if (retryLogon) {
                    authManager.expire(acct.getLogin());
                    doSingleSend(authManager.checkLogon(acct.getLogin()), destId, msg, false);
                }
                else {
                    sendEx = new HttpException(HttpErrors.AJAX_INVOKE_FAIL, "singlesend fail, code:" + errCode + ", msg:" + errMsg);
                }
            }
        }
        
        if (null != sendEx)
            throw sendEx;
    }
    
    /**
     * Perform mass message send
     */
    private void doMassSend(PubAccount acct, MassTarget target, Message msg, boolean retryLogon) {
        //prepare form data
        PostData[] formData = null;
        if (msg.getType() == Message.TYPE_TEXT) {
            formData = new PostData[] {
                    new PostData("type", "1"),
                    new PostData("content", msg.getTextContent()),
                    new PostData("error", "false"),
                    new PostData("imgcode", ""),
                    new PostData("needcomment", "0"),
                    new PostData("groupid", String.valueOf(target.getGroup())),
                    new PostData("sex", String.valueOf(target.getSex())),
                    new PostData("country", (null != target.getCountry())? target.getCountry() : ""),
                    new PostData("province", (null != target.getProvince())? target.getProvince() : ""),
                    new PostData("city", (null != target.getCity())? target.getCity() : ""),
                    new PostData("token", acct.getToken()),
                    new PostData("ajax", "1")
                };
        }

        //prepare header
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "https://mp.weixin.qq.com/cgi-bin/masssendpage");
        String cookieStr = acct.getCookieStr();
        if (null != cookieStr)
            headers.put("Cookie", cookieStr);
        
        HttpException sendEx = null;
        ResponseData resp = httpUtil.post(MASS_MSG_URL, formData, headers);
        if (!resp.isOK()) {
            sendEx = new HttpException(HttpErrors.HTTP_RESP_ERROR, "masssend response:" + resp.getStatusCode());
        }
        else {
            JSONObject result = resp.getBodyAsJSONObject();
            int errCode = result.getInt("ret");
            String errMsg = result.getString("msg");
            
            if (errCode != 0) {
                if (retryLogon) {
                    authManager.expire(acct.getLogin());
                    doMassSend(authManager.checkLogon(acct.getLogin()), target, msg, false);
                }
                else {
                    sendEx = new HttpException(HttpErrors.AJAX_INVOKE_FAIL, "masssend fail, code:" + errCode + ", msg:" + errMsg);
                }
            }
        }
        
        if (null != sendEx)
            throw sendEx;
    }
    
    
    /**
     * Send single message
     */
    public void sendSingleMsg(String acctId, String fakeIds, Message msg) {
        if (null == acctId || null == fakeIds || "".equals(fakeIds) || null == msg)
            return;
        
        //check account login
        PubAccount acct = authManager.checkLogon(acctId);
        
        int failCount = 0;
        StringBuffer failMsg = new StringBuffer();
        String[] destIds = fakeIds.split("[, ]");
        for (String destId : destIds) {
            if ("".equals(destId))
                continue;
            
            try {
                doSingleSend(acct, destId, msg, true);
            }
            catch (Throwable t) {
                failCount++;
                failMsg.append("to fake:" + destId + " [" + t.getMessage() + "]");
            }
        }
        
        if (failCount > 0)
            throw new HttpException(HttpErrors.SINGLE_MSG_FAIL, failMsg.toString());
    }

    /**
     * Send mass message.
     */
    public void sendMassMsg(String acctId, MassTarget target, Message msg) {
        if (null == acctId || null == target || null == msg)
            return;

        //check account login
        PubAccount acct = authManager.checkLogon(acctId);
        
        doMassSend(acct, target, msg, true);
    }
    
    
    
    public void setAuthManager(IAuthManager authManager) {
        this.authManager = authManager;
    }
    public void setHttpUtil(HttpUtil util) {
        this.httpUtil = util;
    }
}