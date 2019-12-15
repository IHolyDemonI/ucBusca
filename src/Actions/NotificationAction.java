package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class NotificationAction extends Action implements SessionAware {
    public String execute() throws Exception {
        return "success";
    }
}
