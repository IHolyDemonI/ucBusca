package Actions;

import Models.IndexBean;
import org.apache.struts2.interceptor.SessionAware;

public class IndexAction extends Action implements SessionAware {
    private String newURL;
    private int level;

    public String execute() throws Exception {
        if (!session.containsKey("Username"))
            return "error";

        if (session.containsKey("isAdmin")) {
            if (!(boolean) session.get("isAdmin")) {
                return "error";
            }
        }

        this.getIndexBean().setNewURL(newURL);
        this.getIndexBean().setLevel(level);

        this.getIndexBean().indexNewURL();

        return "success";
    }

    /* =============== SEARCH BEAN GETTERS AND SETTERS =============== */

    public IndexBean getIndexBean(){
        if (!session.containsKey("IndexBean"))
            this.session.put("IndexBean", new IndexBean());
        return (IndexBean) session.get("IndexBean");
    }

    public void setSearchBean(IndexBean indexBean){
        session.put("IndexBean", indexBean);
    }

    /* =============== GETTERS AND SETTERS =============== */

    public String getNewURL() {
        return newURL;
    }

    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
