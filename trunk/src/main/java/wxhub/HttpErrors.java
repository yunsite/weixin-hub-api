package wxhub;

/**
 * Error code definition
 *
 * @author Tigerwang
 */
public class HttpErrors {
    public static final int BAD_SSL_PROTOCOL = 1001;
    public static final int SSL_INIT_FAIL = 1002;
    public static final int MULTIPART_BUILD_FAIL = 1003;
    public static final int HTTP_EXEC_ERROR = 1004;
    public static final int HTTP_RESP_ERROR = 1005;
    public static final int HTTP_PARSE_HTMLDOC = 1006;
    public static final int HTTP_PARSE_JSON = 1007;
    
    
    public static final int AJAX_INVOKE_FAIL = 1101;    //ajax response error
    public static final int LOGON_TOKEN_FAIL = 1102;    //token not found in logon result
    public static final int ACCT_NOT_FOUND = 1103;      //public account not found
    public static final int SINGLE_MSG_FAIL = 1104;     //send single message fail
}