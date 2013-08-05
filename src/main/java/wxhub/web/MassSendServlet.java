package wxhub.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import wxhub.msg.Message;
import wxhub.msg.MassTarget;

public class MassSendServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String groupId = req.getParameter("groupId");
        String content = req.getParameter("content");
        
        String errMsg = null;
        String acctId = FactoryUtil.getAccountId();
        if (null == acctId) {
            errMsg = "account not registered";
        }
        else {
            try {
                MassTarget target = (null != groupId)? new MassTarget(Integer.parseInt(groupId)) : new MassTarget();
                FactoryUtil.getMsgManager().sendMassMsg(acctId, target, Message.textMsg(content));
                errMsg = "send success";
            }
            catch (Throwable t) {
                errMsg = "send fail, " + t.getMessage();
            }
        }
        
        resp.getWriter().print("{\"msg\": \"" + errMsg + "\"}");
    }
}