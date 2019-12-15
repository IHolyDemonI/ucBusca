package Actions;

import org.apache.struts2.interceptor.SessionAware;

public class SearchHistoryAction extends Action implements SessionAware {
    public String execute()  {
        return "success";
    }
}
