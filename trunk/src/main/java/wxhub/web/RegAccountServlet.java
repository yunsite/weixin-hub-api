package wxhub.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import wxhub.auth.PubAccount;

/**
 * Register account information
 *
 * @author Tigerwang
 */
public class RegAccountServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private void saveAccount(String acctId, String passwd, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String errMsg = null;
        
        try {
            FactoryUtil.getAuthManager().saveAuth(new PubAccount(acctId, passwd));
            FactoryUtil.setAccountId(acctId);
            errMsg = "success";
        }
        catch (Throwable t) {
            errMsg = "save fail, " + t.getMessage();
        }
        
        resp.getWriter().print("{\"msg\": \"" + errMsg + "\"}");
    }
    
    private void getAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acctId = FactoryUtil.getAccountId();
        if (null == acctId)
            acctId = "";
        
        resp.getWriter().print("{\"acctId\": \"" + acctId + "\"}");
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String saveAct = req.getParameter("save");
        String acctId = req.getParameter("loginId");
        String passwd = req.getParameter("passwd");
        
        if (null != saveAct) {
            saveAccount(acctId, passwd, req, resp);
        }
        else {
            getAccount(req, resp);
        }
    }
}