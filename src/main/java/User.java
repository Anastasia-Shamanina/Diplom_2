import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class User {

    // ключи стали полями типа String
    private String email;
    private String password;
    private String name;
    private String accessToken;

    // конструктор со всеми параметрами
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // конструктор без параметров
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Response createUser() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(new User(email, password, name))
                .when()
                .post(CREATE_USER);
    }

    public Response loginUser() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(new User(email, password, name))
                .when()
                .post(LOGIN_USER);
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .assertThat()
                .statusCode(202);
    }

    String emailNew;
    String passwordNew;
    String nameNew;

    //С авторизацией
    public Response changeUser(String accessToken){
        return given().spec(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .body(new User(emailNew, passwordNew, nameNew))
                .when()
                .patch(CHANGING_USER_DATA);
    }
    //Без авторизацией
    public Response changeUserWithoutToken(){
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(new User(emailNew, passwordNew, nameNew))
                .when()
                .patch(CHANGING_USER_DATA);
    }
}
