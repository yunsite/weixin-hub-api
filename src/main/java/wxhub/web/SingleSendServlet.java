package wxhub.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import wxhub.msg.Message;

public class SingleSendServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fakeIds = req.getParameter("fakeIds");
        String content = req.getParameter("content");
        
        String errMsg = null;
        String acctId = FactoryUtil.getAccountId();
        if (null == acctId) {
            errMsg = "account not registered";
        }
        else {
            try {
                FactoryUtil.getMsgManager().sendSingleMsg(acctId, fakeIds, Message.textMsg(content));
                errMsg = "send success";
            }
            catch (Throwable t) {
                errMsg = "send fail, " + t.getMessage();
            }
        }
        
        resp.getWriter().print("{\"msg\": \"" + errMsg + "\"}");
    }
}