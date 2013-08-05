package wxhub;

import java.io.File;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;

/**
 * Data for http post.
 *
 * @author Tigerwang
 */
public class PostData {
    private String name;
    private String value;
    private File file;
    
    private static Map<String, String> extMap;
    static {
        extMap = new HashMap<String, String>();
        extMap.put("jpg", "image/jpeg");
        extMap.put("jpeg", "image/jpeg");
        extMap.put("gif", "image/gif");
        extMap.put("png", "image/png");
        extMap.put("", "application/octet-stream");
    }
    
    public PostData(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public PostData(String name, File file) {
        this.name = name;
        this.file = file;
    }
    
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
    public File getFile() {
        return file;
    }
    public boolean isFile() {
        return (null != file);
    }
    
    public String getFileContentType() {
        int idx = file.getName().lastIndexOf(".");
        String fileExt = (idx <= 0)? "" : file.getName().substring(idx + 1).toLowerCase();
        
        String type = extMap.get(fileExt);
        if (null == type)
            type = extMap.get("");
        
        return type;
    }
    
    
    /**
     * Check whether contains file parameter
     */
    public static boolean hasFile(PostData[] data) {
        if (null == data || data.length == 0)
            return false;
        
        for (PostData post : data) {
            if (post.isFile())
                return true;
        }
        
        return false;
    }
    
    /**
     * Get encoded string for post data
     */
    public static String getEncodeString(PostData[] data) {
        StringBuffer buf = new StringBuffer("");
        int idx = 0;
        
        if (null != data) {
            for (PostData post : data) {
                if (null != post.getName() && null != post.getValue()) {
                    if (idx > 0)
                        buf.append("&");
                    try {
                        String str = URLEncoder.encode(post.getName(), HttpUtil.DEF_CHARSET) + "=" + URLEncoder.encode(post.getValue(), HttpUtil.DEF_CHARSET);
                        buf.append(str);
                        
                        idx++;
                    }
                    catch (Exception ex) {}
                }
            }
        }
        
        return buf.toString();
    }
}