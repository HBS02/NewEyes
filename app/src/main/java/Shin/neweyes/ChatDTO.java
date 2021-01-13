package Shin.neweyes;

public class ChatDTO {

    private String userName;
    private String message;

    public String getTolatitude() {
        return tolatitude;
    }

    public void setTolatitude(String tolatitude) {
        this.tolatitude = tolatitude;
    }

    private String tolatitude;

    public String getTolongtitude() {
        return tolongtitude;
    }

    public void setTolongtitude(String tolongtitude) {
        this.tolongtitude = tolongtitude;
    }

    private String tolongtitude;



    public ChatDTO() {}
    public ChatDTO(String userName, String message) {
        this.userName = userName;
        this.message = message;

    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}