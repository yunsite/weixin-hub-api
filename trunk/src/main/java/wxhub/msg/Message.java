package wxhub.msg;

/**
 * Message object.
 *
 * @author Tigerwang
 */
public class Message {
    public static final int TYPE_TEXT = 1;
    
    private int type;   //message type
    private String textContent;
    
    private String fromId;
    private String destId;
    
    private Message(int type, String textContent) {
        this.type = type;
        this.textContent = textContent;
    }
    
    
    /**
     * Create text message.
     */
    public static Message textMsg(String content) {
        return new Message(TYPE_TEXT, content);
    }
    
    
    public int getType() {
        return type;
    }
    public String getTextContent() {
        return textContent;
    }
}