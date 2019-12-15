package Actions;

import Models.SearchBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAction extends Action implements SessionAware {
    private String searchTarget;

    private HashMap<String, Object> serverAnswer;

    private ArrayList<String> searchedTitles = new ArrayList<>();
    private ArrayList<String> searchedURLs = new ArrayList<>();
    private ArrayList<String> searchedCitations = new ArrayList<>();

    public String execute() throws Exception {
        if (session.containsKey("Username"))
            this.getSearchBean().setUsername((String) session.get("Username"));

        this.getSearchBean().setSearchTarget(searchTarget);

        serverAnswer = this.getSearchBean().search();

        if (serverAnswer == null)
            return "error";

        if (serverAnswer.containsKey("Error"))
            return "error";
        else {
            searchedTitles.addAll((ArrayList<String>) serverAnswer.get("SearchedTitles"));
            searchedURLs.addAll((ArrayList<String>) serverAnswer.get("SearchedURLs"));
            searchedCitations.addAll((ArrayList<String>) serverAnswer.get("SearchedCitations"));
            return "success";
        }
    }

    /* =============== SEARCH BEAN GETTERS AND SETTERS =============== */

    public SearchBean getSearchBean(){
        if (!session.containsKey("SearchBean"))
            this.session.put("SearchBean", new SearchBean());
        return (SearchBean) session.get("SearchBean");
    }

    public void setSearchBean(SearchBean searchBean){
        session.put("SearchBean", searchBean);
    }

    /* =============== GETTERS AND SETTERS =============== */

    public HashMap<String, Object> getServerAnswer() {
        return serverAnswer;
    }

    public void setServerAnswer(HashMap<String, Object> serverAnswer) {
        this.serverAnswer = serverAnswer;
    }

    public ArrayList<String> getSearchedTitles() {
        return searchedTitles;
    }

    public void setSearchedTitles(ArrayList<String> searchedTitles) {
        this.searchedTitles = searchedTitles;
    }

    public ArrayList<String> getSearchedURLs() {
        return searchedURLs;
    }

    public void setSearchedURLs(ArrayList<String> searchedURLs) {
        this.searchedURLs = searchedURLs;
    }

    public ArrayList<String> getSearchedCitations() {
        return searchedCitations;
    }

    public void setSearchedCitations(ArrayList<String> searchedCitations) {
        this.searchedCitations = searchedCitations;
    }

    public String getSearchTarget() {
        return searchTarget;
    }

    public void setSearchTarget(String searchTarget) {
        this.searchTarget = searchTarget;
    }
}
