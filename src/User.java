import java.util.ArrayList;
import java.util.List;

public class User {
    private int userID;
    private String username;
    private String password;
    private String fullName;
    private int usertype;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUsertype() {
        return usertype;
    }

    public String getUsertypeStr(){
        List<String> l= new ArrayList<String>();
        l.add("Audience");
        l.add("Manager");
        l.add("Others");
        return l.get(usertype);
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }
}
