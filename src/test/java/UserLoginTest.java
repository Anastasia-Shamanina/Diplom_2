import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;

import static org.hamcrest.Matchers.*;
import static constants.Url.URL_BURGERS;

public class UserLoginTest {
    String email = "ivanovanastia_6@gmail.com";
    String incorrectEmail = "ivanovanastia_7@gmail.com";
    String password = "123456";
    String incorrectPassword = "654321";
    String name = "Настя";
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = URL_BURGERS;

        // Создание пользователя
        User user = new User(email, password, name);
        Response responseAccessToken = user.createUser();
        responseAccessToken.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        this.accessToken = responseAccessToken.body().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин под существующим пользователем,")
    public void loginUser() {
        User user = new User(email, password, name);
        user.loginUser()
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Логин с неверным неверным логином и паролем")
    public void loginNonexistentUser() {
        User user = new User(incorrectEmail, incorrectPassword, name);
        user.loginUser()
                .then().assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);
    }

    @After
    public void userDeletion() {
        // Отправляем DELETE-запрос на удаление пользователя
        try {
            User user = new User(email, password, name);
            user.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Такого пользователя не существует - удаление невозможно.");
        }
    }
}
