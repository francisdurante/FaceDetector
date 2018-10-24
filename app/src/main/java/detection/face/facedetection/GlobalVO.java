package detection.face.facedetection;

public class GlobalVO {
    static String registrationResponse = "";
    static String firstName;
    static String lastName;
    static String accounId;
    static String latidtude;
    static String longitude;

    static String getRegistrationResponse() {
        return registrationResponse;
    }

    static void setRegistrationResponse(String registrationResponse1) {
        registrationResponse = registrationResponse1;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        GlobalVO.firstName = firstName;
    }

    public static String getLastname() {
        return lastName;
    }

    public static void setLastname(String lastname) {
        GlobalVO.lastName = lastname;
    }

    public static String getAccounId() {
        return accounId;
    }

    public static void setAccounId(String accounId) {
        GlobalVO.accounId = accounId;
    }

    public static String getLatitude() {
        return latidtude;
    }

    public static void setLatitude(String latitude) {
        GlobalVO.latidtude = latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static void setLongitude(String longitude) {
        GlobalVO.longitude = longitude;
    }
}
