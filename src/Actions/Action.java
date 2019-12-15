package Actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class Action extends ActionSupport implements SessionAware {
    public Map<String, Object> session;

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
        session.put("isLoggedin", true);
    }

    public String execute() throws Exception{
        return "success";
    }

    public void printSession(){
        for (String name: session.keySet()){
            String key = name.toString();
            String value = session.get(name).toString();
            System.out.println(key + " -> " + value);
        }
    }
}
