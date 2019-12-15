package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class AdministrationPageAction extends Action implements SessionAware {
    public String execute() throws Exception {
        return "success";
    }
}
