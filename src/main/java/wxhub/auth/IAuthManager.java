package wxhub.auth;

/**
 * Authentication manage.
 *
 * @author Tigerwang
 */
public interface IAuthManager {
    /**
     * Save authentication info.
     */
    void saveAuth(PubAccount acct);
    
    /**
     * Get authentication info.
     */
    PubAccount getAuth(String acctId);
    
    
    /**
     * Try logon to weixin, result maybe success/error
     */
    PubAccount logon(String acctId);
    
    /**
     * Logon expire.
     */
    void expire(String acctId);
    
    /**
     * Check account logon, logon when necessary.
     */
    PubAccount checkLogon(String acctId);
}