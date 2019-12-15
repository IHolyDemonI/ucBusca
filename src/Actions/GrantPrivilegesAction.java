package Actions;

import Models.GrantPrivilegesBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.HashMap;

public class GrantPrivilegesAction extends Action implements SessionAware {
    String targetUser;

    private HashMap<String, Object> serverAnswer;

    public String execute(){
        if (targetUser == null)
            return "error";

        this.getGrantPrivilegesBean().setTargetUser(targetUser);

        printSession();

        serverAnswer = getGrantPrivilegesBean().grantPrivileges();

        if (serverAnswer == null || serverAnswer.containsKey("Error"))
            return "error";
        else {
            return "success";
        }
    }

    private GrantPrivilegesBean getGrantPrivilegesBean() {
        if (!session.containsKey("GrantPrivilegesBean")) {
            this.setGrantPrivilegesBean(new GrantPrivilegesBean());
        }
        return (GrantPrivilegesBean) session.get("GrantPrivilegesBean");
    }

    private void setGrantPrivilegesBean(GrantPrivilegesBean userBean) {
        session.put("GrantPrivilegesBean", userBean);
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }
}
