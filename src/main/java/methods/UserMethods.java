package methods;
import io.qameta.allure.Step;
import pojo.*;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class UserMethods {
    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .post(CREATE_USER);
    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .post(LOGIN_USER);
    }

    @Step("Удалить пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER)
                .then()
                .assertThat()
                .statusCode(202);
    }

    @Step("Обновление данных авторизованного пользователя")
    public Response changeUser(User user, String accessToken){
        return given().spec(BaseHttpClient.baseRequestSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(CHANGING_USER_DATA);
    }

    @Step("Обновление данных пользователя без авторизации")
    public Response changeUserWithoutToken(User user){
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(user)
                .when()
                .patch(CHANGING_USER_DATA);
    }

}
