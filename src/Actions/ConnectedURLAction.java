package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class ConnectedURLAction extends Action implements SessionAware {
    String targetURL;

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    public String execute() throws Exception {
        return "success";
    }
}
