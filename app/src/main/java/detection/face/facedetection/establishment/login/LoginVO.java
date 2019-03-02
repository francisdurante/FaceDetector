package detection.face.facedetection.establishment.login;

public class LoginVO {

    private String id;
    private String estId;
    private String estName;
    private String locationLat;
    private String locationLon;
    private String emotion;
    private String age;
    private String status;
    private String username;
    private String password;
    private String frontStoreUrl;
    private String estAddress;
    private String estUserId;
    private int estTypeId;

    public String getFrontStoreUrl() {
        return frontStoreUrl;
    }

    public void setFrontStoreUrl(String frontStoreUrl) {
        this.frontStoreUrl = frontStoreUrl;
    }

    public String getEstAddress() {
        return estAddress;
    }

    public void setEstAddress(String estAddress) {
        this.estAddress = estAddress;
    }

    public String getEstUserId() {
        return estUserId;
    }

    public void setEstUserId(String estUserId) {
        this.estUserId = estUserId;
    }

    public int getEstTypeId() {
        return estTypeId;
    }

    public void setEstTypeId(int estTypeId) {
        this.estTypeId = estTypeId;
    }

    private int loginStatus;


    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstId() {
        return estId;
    }

    public void setEstId(String estId) {
        this.estId = estId;
    }

    public String getEstName() {
        return estName;
    }

    public void setEstName(String estName) {
        this.estName = estName;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(String locationLon) {
        this.locationLon = locationLon;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
