package wxhub;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * Http interaction utility.
 *
 * @author Tigerwang
 */
public class HttpUtil {
    public static final String DEF_CHARSET = "UTF-8";
    
    private String charset;
    private int connPerHost = -1;
    private int connTimeout = -1;
    private int soTimeout = -1;
    
    private MultiThreadedHttpConnectionManager connManager;
    private HttpClient client;
    
    
    public String getCharset() {
        return (null == charset)? DEF_CHARSET : charset;
    }
    public int getConnPerHost() {
        return (connPerHost == -1)? 100 : connPerHost;
    }
    public int getConnTimeout() {
        return (connTimeout == -1)? 10000 : connTimeout;
    }
    public int getSoTimeout() {
        return (soTimeout == -1)? 30000 : soTimeout;
    }
    
    
    public void init() {
        init(getConnPerHost(), getConnTimeout(), getSoTimeout());
    }
    
    /**
     * Initialize client
     */
    public void init(int connPerHost, int connTimeout, int soTimeout) {
        this.connManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connManager.getParams();
        params.setDefaultMaxConnectionsPerHost(connPerHost);
        params.setConnectionTimeout(connTimeout);
        params.setSoTimeout(soTimeout);
        
        //manual cookie
        HttpClientParams clientParams = new HttpClientParams();
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        this.client = new HttpClient(clientParams, connManager);
        Protocol ssl = new Protocol("https", new SSLSocketFactory(), 443);
        Protocol.registerProtocol("https", ssl);
    }
    
    
    /**
     * Set header for http request
     */
    public void setHeader(HttpMethod httpMethod, Map<String, String> headers) {
        if (null == headers)
            return;
        
        for (Map.Entry<String, String> entry : headers.entrySet())
            httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
    }
    
    /**
     * Get method for http url
     */
    public ResponseData get(String url, PostData[] params) {
        return get(url, params, null);
    }
    
    /**
     * Get the http url
     */
    public ResponseData get(String url, PostData[] params, Map<String, String> headers) {
        String paramStr = PostData.getEncodeString(params);
        String urlStr = (null != paramStr && paramStr.length() > 0)? 
            url + ((url.indexOf("?") > 0)? "&":"?") + paramStr  :
            url;
        
        GetMethod httpMethod = new GetMethod(urlStr);
        if (null != headers && headers.size() > 0)
            setHeader(httpMethod, headers);
        
        return sendRequest(httpMethod);
    }
    
    
    /**
     * Post to url, form data url-encoded with utf-8
     */
    public ResponseData post(String url, PostData[] forms) {
        return post(url, forms, null);
    }
    
    public ResponseData post(String url, PostData[] forms, Map<String, String> headers) {
        PostMethod httpMethod = new PostMethod(url);
        
        if (null != forms && forms.length > 0) {
            for (PostData post : forms) {
                if (null != post.getName() && null != post.getValue())
                    httpMethod.addParameter(post.getName(), post.getValue());
            }
        }
        HttpMethodParams param = httpMethod.getParams();
        param.setContentCharset(getCharset());
        
        if (null != headers && headers.size() > 0)
            setHeader(httpMethod, headers);
        
        return sendRequest(httpMethod);
    }
    
    
    /**
     * Post multipart to url
     */
    public ResponseData multiPart(String url, PostData[] forms) {
        return multiPart(url, forms, null);
    }
    
    public ResponseData multiPart(String url, PostData[] forms, Map<String, String> headers) {
        if (!PostData.hasFile(forms))
            return post(url, forms, headers);

        PostMethod httpMethod = new PostMethod(url);
        Part[] parts = new Part[forms.length];
        
        try {
            int idx = 0;
            for (PostData post : forms) {
                if (post.isFile()) {
                    File file = post.getFile();
                    FilePart filePart = new FilePart(post.getName(), file.getName(), 
                            file, post.getFileContentType(), getCharset());
                    filePart.setTransferEncoding("binary");
                    parts[idx] = filePart;
                }
                else {
                    parts[idx] = new StringPart(post.getName(), post.getValue());
                }
                
                idx++;
            }
        }
        catch (IOException ioEx) {
            throw new HttpException(HttpErrors.MULTIPART_BUILD_FAIL, "create file part", ioEx);
        }
        
        httpMethod.setRequestEntity(new MultipartRequestEntity(parts, httpMethod.getParams()));
        if (null != headers && headers.size() > 0)
            setHeader(httpMethod, headers);
        
        return sendRequest(httpMethod);
    }
    
    
    /**
     * Perform http invocation, send request and retrieve response.
     */
    @SuppressWarnings("unchecked")
    public ResponseData sendRequest(HttpMethod httpMethod) {
        //retry handler
		httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		
		ResponseData res = null;
		try {
    		client.executeMethod(httpMethod);
    		
    		//prepare header
    		Map<String, Object> headers = new HashMap<String, Object>();
    		for (Header resHeader : httpMethod.getResponseHeaders()) {
    		    Object headerObj = headers.get(resHeader.getName());
    		    if (null == headerObj) {
    		        headerObj = resHeader.getValue();
    		    }
    		    else if (headerObj instanceof String) {
    		        headerObj = new ArrayList(Arrays.asList(headerObj, resHeader.getValue()));
    		    }
    		    else {
    		        ((List)headerObj).add(resHeader.getValue());
    		    }
    		    
    		    headers.put(resHeader.getName(), headerObj);
    		}
    		
    		res = new ResponseData(httpMethod.getStatusCode(), httpMethod.getStatusText(), 
    		        headers, httpMethod.getResponseBodyAsString());
    	}
    	catch (IOException ioEx) {
    	    throw new HttpException(HttpErrors.HTTP_EXEC_ERROR, "path:" + httpMethod.getPath(), ioEx);
    	}
    	finally {
    	    httpMethod.releaseConnection();
    	}
		
		return res;
    }
    
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    public void setConnPerHost(int connPerHost) {
        this.connPerHost = connPerHost;
    }
    public void setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
    }
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }
}