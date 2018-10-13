package detection.face.facedetection;

public class GlobalVO {
    static String registrationResponse = "";
    static String firstName;
    static String lastName;
    static String accounId;

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
}
