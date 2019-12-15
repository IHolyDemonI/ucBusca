package Actions;

import Models.ConnectedURLBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectedURLAction extends Action implements SessionAware {

    private String targetURL;
    private ArrayList<HashMap<String,String>> pages;

    public ArrayList<HashMap<String,String>> getPages() {
        return pages;
    }


    public String execute() throws Exception {
        if(this.targetURL == null){
            return "error";
        }
        HashMap<String, Object> serverAnswer =
                this.getConnectedURLBean().fetchConnectedURLs(this.targetURL);

        ArrayList<String> titles = (ArrayList<String>) serverAnswer.get("ConnectedTitles");
        ArrayList<String> urls = (ArrayList<String>) serverAnswer.get("ConnectedURLs");
        ArrayList<String> citations = (ArrayList<String>) serverAnswer.get("ConnectedCitations");
        this.pages = new ArrayList<>();
        int size = titles.size();
        for (int i = 0; i< size;i++) {
            HashMap<String,String> page = new HashMap<>();
            page.put("title", titles.get(i));
            page.put("url", urls.get(i));
            page.put("citation", citations.get(i));
            this.pages.add(page);
        }
        return "success";
    }

    private ConnectedURLBean getConnectedURLBean() {
        if (!session.containsKey("ConnectedURLBean")) {
            this.setConnectedURLBean(new ConnectedURLBean());
        }
        return (ConnectedURLBean) session.get("ConnectedURLBean");
    }

    private void setConnectedURLBean(ConnectedURLBean connectedURLBean) {
        session.put("ConnectedURLBean", connectedURLBean);
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

}
