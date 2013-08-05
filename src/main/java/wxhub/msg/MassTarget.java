package wxhub.msg;

/**
 * Target of mass message.
 *
 * @author Tigerwang
 */
public class MassTarget {
    public static final int GROUP_ALL = -1;
    public static final int SEX_ALL = 0;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;
    
    private int group;
    private int sex;
    private String country;
    private String prov;
    private String city;
    
    
    /**
     * Construct target for all user
     */
    public MassTarget() {
        this.group = GROUP_ALL;
        this.sex = SEX_ALL;
    }
    
    /**
     * Construct target for specified group
     */
    public MassTarget(int group) {
        this.group = (group < -1)? GROUP_ALL : group;
        this.sex = SEX_ALL;
    }
    
    public MassTarget(int group, int sex, String country, String prov, String city) {
        this.group = (group < -1)? GROUP_ALL : group;
        this.sex = (sex < 0 || sex > 2)? SEX_ALL : sex;
        this.country = country;
        this.prov = prov;
        this.city = city;
    }
    
    
    public int getGroup() {
        return group;
    }
    public int getSex() {
        return sex;
    }
    public String getCountry() {
        return country;
    }
    public String getProvince() {
        return prov;
    }
    public String getCity() {
        return city;
    }
}