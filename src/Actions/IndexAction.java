package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class IndexAction extends Action implements SessionAware {
    String newURL;

    public String getNewURL() {
        return newURL;
    }

    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }


    public String execute() throws Exception {
        return "success";
    }
}
