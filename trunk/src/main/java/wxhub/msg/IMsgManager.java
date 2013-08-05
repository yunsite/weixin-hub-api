package wxhub.msg;

/**
 * Message publish manager.
 *
 * @author Tigerwang
 */
public interface IMsgManager {
    /**
     * Send single message to specified fakes.
     *
     * @param  acctId       public account id
     * @param  fakeIds      destination fakes
     * @param  msg          message to send
     */
    void sendSingleMsg(String acctId, String fakeIds, Message msg);
    
    /**
     * Send mass message.
     *
     * @param  acctId       public account id
     * @param  target       target to send
     * @param  msg          message to send
     */
    void sendMassMsg(String acctId, MassTarget target, Message msg);
}