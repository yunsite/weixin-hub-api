package wxhub;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

/**
 * Http response data.
 *
 * @author Tigerwang
 */
public class ResponseData {
    public static Pattern COOKIE_PATTERN = Pattern.compile("^(.+?)=(.*?)([; ].*)*$");
    public static Pattern UNICODE_ESCAPE = Pattern.compile("&#([0-9]{3,5});");
    
    private Map<String, Object> headers;
    private Map<String, String> cookies;
    
    private int statusCode;
    private String statusText;
    private String bodyAsStr;
    private Document bodyAsDoc;
    private JSONObject bodyAsJSONObj;
    private JSONArray bodyAsJSONArray;
    
    
    public ResponseData(int statusCode, String content) {
        this.statusCode = statusCode;
        this.bodyAsStr = normalizeBody(content);
    }
    public ResponseData(int statusCode, Map<String, Object> headers, String content) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.cookies = parseCookie(headers);
        this.bodyAsStr = normalizeBody(content);
    }
    public ResponseData(int statusCode, String statusText, Map<String, Object> headers, String content) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.cookies = parseCookie(headers);
        this.bodyAsStr = normalizeBody(content);
    }
    
    
    private String normalizeBody(String original) {
        Matcher mm = UNICODE_ESCAPE.matcher(original);
        
        StringBuffer unescaped = new StringBuffer();
        while (mm.find()) {
            mm.appendReplacement(unescaped, Character.toString(
                    (char) Integer.parseInt(mm.group(1), 10)));
        }
        mm.appendTail(unescaped);
        
        return unescaped.toString();
    }
    
    private void addCookieValue(Map<String, String> cookieMap, String cookieStr) {
        if (null == cookieStr)
            return;
        
        Matcher matcher = COOKIE_PATTERN.matcher(cookieStr);
        if (matcher.matches() && matcher.groupCount() >= 2)
            cookieMap.put(matcher.group(1).trim(), matcher.group(2).trim());
    }
    
    /**
     * Parse cookie
     */
    private Map<String, String> parseCookie(Map<String, Object> headers) {
        Object cookieObj = (null != headers)? headers.get("Set-Cookie") : null;
        if (null == cookieObj)
            return null;
        
        Map<String, String> cookieMap = new HashMap<String, String>();
        if (cookieObj instanceof String) {
            addCookieValue(cookieMap, cookieObj.toString().trim());
        }
        else {
            for (Object obj : (List)cookieObj)
                addCookieValue(cookieMap, obj.toString().trim());
        }
        
        return cookieMap;
    }
    
    public Object getHeader(String name) {
        return headers.get(name);
    }
    public Map<String, String> getCookies() {
        return cookies;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public String getStatusText() {
        return statusText;
    }
    public boolean isOK() {
        return (statusCode == 200);
    }
    
    public String getBodyAsString() {
        return bodyAsStr;
    }
    public Document getBodyAsDocument() {
        if (null == bodyAsDoc && null != bodyAsStr) {
            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                bodyAsDoc = builder.parse(new ByteArrayInputStream(bodyAsStr.getBytes(HttpUtil.DEF_CHARSET)));
            }
            catch (Exception ex) {
                throw new HttpException(HttpErrors.HTTP_PARSE_HTMLDOC, bodyAsStr, ex);
            }
        }
        
        return bodyAsDoc;
    }
    public JSONObject getBodyAsJSONObject() {
        if (null == bodyAsJSONObj && null != bodyAsStr) {
            try {
                JSON json = JSONSerializer.toJSON(bodyAsStr);
                bodyAsJSONObj = (json instanceof JSONObject)? (JSONObject)json : null;
            }
            catch (Exception ex) {
                throw new HttpException(HttpErrors.HTTP_PARSE_JSON, bodyAsStr, ex);
            }
        }
        
        return bodyAsJSONObj;
    }
    public JSONArray getBodyAsJSONArray() {
        if (null == bodyAsJSONArray && null != bodyAsStr) {
            try {
                JSON json = JSONSerializer.toJSON(bodyAsStr);
                bodyAsJSONArray = (json instanceof JSONArray)? (JSONArray)json : null;
            }
            catch (Exception ex) {
                throw new HttpException(HttpErrors.HTTP_PARSE_JSON, bodyAsStr, ex);
            }
        }
        
        return bodyAsJSONArray;
    }
    
    
    public void setStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
        this.cookies = parseCookie(headers);
    }
    public void setBodyString(String content) {
        String oldContent = this.bodyAsStr;
        if ((null == oldContent && null == content) || (null != oldContent && oldContent.equals(content)))
            return;
        
        this.bodyAsStr = normalizeBody(content);
        this.bodyAsDoc = null;
        this.bodyAsJSONObj = null;
        this.bodyAsJSONArray = null;
    }
}