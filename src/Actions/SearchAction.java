package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class SearchAction extends Action implements SessionAware {
    private String searchTarget = null;

    public String execute() throws Exception {
        return "success";
    }

    public String getSearchTarget() {
        return searchTarget;
    }

    public void setSearchTarget(String searchTarget) {
        this.searchTarget = searchTarget;
    }
}
