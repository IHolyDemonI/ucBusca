package Actions;

import Models.UserBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.HashMap;

public class UserAction extends Action implements SessionAware{
    private String username = null;
    private String password = null;
    private boolean hasNewNotifications = false;
    private boolean isAdmin = false;

    private HashMap <String, Object> serverAnswer;

    public String login(){
        if (!credentialsAreSet())
            return "error";

        this.getUserBean().setUsername(this.username);
        this.getUserBean().setPassword(this.password);

        serverAnswer = getUserBean().tryLogin();

        if (serverAnswer == null)
            return "error";

        if (serverAnswer.containsKey("Error"))
            return "wrongLogin";
        else {
            setHasNewNotifications((boolean) serverAnswer.get("hasNewNotifications"));
            setAdmin((boolean) serverAnswer.get("isAdmin"));
            session.put("Username", username);
            session.put("Password", password);
            session.put("hasNewNotifications", hasNewNotifications);
            session.put("isAdmin", isAdmin);

            if ((boolean) session.get("isAdmin"))
                return "admin";

            return "success";
        }
    }

    public String register(){
        if (!credentialsAreSet())
            return "error";

        this.getUserBean().setUsername(this.username);
        this.getUserBean().setPassword(this.password);

        serverAnswer = getUserBean().tryRegister();

        if (serverAnswer == null)
            return "error";

        if (serverAnswer.containsKey("Error"))
            return "usernameExists";
        else {
            return "success";
        }
    }

    private boolean credentialsAreSet(){
        return username != null && password != null;
    }

    /* =============== GETTERS AND SETTERS =============== */

    private UserBean getUserBean() {
        if (!session.containsKey("UserBean")) {
            this.setUserBean(new UserBean());
        }
        return (UserBean) session.get("UserBean");
    }

    private void setUserBean(UserBean userBean) {
        session.put("UserBean", userBean);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHasNewNotifications() {
        return hasNewNotifications;
    }

    public void setHasNewNotifications(boolean hasNewNotifications) {
        this.hasNewNotifications = hasNewNotifications;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
