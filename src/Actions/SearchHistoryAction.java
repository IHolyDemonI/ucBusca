package Actions;

import Models.SearchHistoryBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchHistoryAction extends Action implements SessionAware {
    private String username;
    private ArrayList<String> mySearchHistory;


    public String execute()  {
        if(this.session.containsKey("Username")){
            this.username = (String) this.session.get("Username");
            HashMap<String, Object> serverAnswer =
                    this.getSearchHistoryBean().fetchMySearchHistory(this.username);

            if(serverAnswer == null){
                return "error";
            }

            this.mySearchHistory = (ArrayList<String>)serverAnswer.get("SearchHistory");
            if(this.mySearchHistory == null){
                this.mySearchHistory = new ArrayList<>();
            }

            return "success";
        }
        else {
            return "login";
        }
    }

    public ArrayList<String> getMySearchHistory(){
        return mySearchHistory;
    }

    private SearchHistoryBean getSearchHistoryBean() {
        if (!session.containsKey("SearchHistoryBean")) {
            this.setSearchHistoryBean(new SearchHistoryBean());
        }
        return (SearchHistoryBean) session.get("SearchHistoryBean");
    }

    private void setSearchHistoryBean(SearchHistoryBean searchHistoryBean) {
        session.put("SearchHistoryBean", searchHistoryBean);
    }
}
