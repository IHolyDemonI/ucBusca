package Actions;

import Models.NotificationBean;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAction extends Action implements SessionAware {
    private HashMap<String, Object> serverAnswer;

    private ArrayList<String> notifications = new ArrayList<>();
    private ArrayList<String> newNotifications = new ArrayList<>();

    public String execute() throws Exception {
        if (!session.containsKey("Username"))
            return "error";

        this.getNotificationBean().setUsername((String) session.get("Username"));

        serverAnswer = getNotificationBean().getNotifications();

        if (serverAnswer == null)
            return "error";

        if (serverAnswer.containsKey("Error"))
            return "error";
        else {
            notifications.addAll((ArrayList<String>)serverAnswer.get("Notifications"));
            newNotifications.addAll((ArrayList<String>)serverAnswer.get("NewNotifications"));
            return "success";
        }
    }

    public HashMap<String, Object> getServerAnswer() {
        return serverAnswer;
    }

    public void setServerAnswer(HashMap<String, Object> serverAnswer) {
        this.serverAnswer = serverAnswer;
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<String> getNewNotifications() {
        return newNotifications;
    }

    public void setNewNotifications(ArrayList<String> newNotifications) {
        this.newNotifications = newNotifications;
    }

    public NotificationBean getNotificationBean(){
        if (!session.containsKey("NotificationBean"))
            this.setNotificationBean(new NotificationBean());
        return (NotificationBean) session.get("NotificationBean");
    }

    public void setNotificationBean(NotificationBean notificationBean){
        session.put("NotificationBean", notificationBean);
    }
}
