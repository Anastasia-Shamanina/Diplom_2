import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static constants.Handle.*;
import static constants.Url.URL_BURGERS;


@RunWith(Parameterized.class)
public class ChangingUserDataTest {
    String accessToken;
    String accessTokenNew;
    String email = "ivanovanastia_6@gmail.com";
    String password = "123456";
    String name = "Настя";

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

    String emailNew;
    String passwordNew;
    String nameNew;

    public ChangingUserDataTest(String emailNew, String passwordNew, String nameNew) {
        this.emailNew = emailNew;
        this.passwordNew = passwordNew;
        this.nameNew = nameNew;
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {"ivanovanastia_6@gmail.com", "123456", "Лера"}, //меняем имя
                {"ivanovanastia_6@gmail.com", "654321", "Настя"}, //меняем пароль
                {"ivanovanastia_7@gmail.com", "123456", "Настя"}, //меняем email
        };
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void changeUserWithAuth() {
        User userNew = new User(emailNew, passwordNew, nameNew);
        Response userForChange = userNew.changeUser(accessToken);
        userForChange.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void changeUserWithoutAuth() {
        User userNew = new User(emailNew, passwordNew, nameNew);
        Response userForChange = userNew.changeUserWithoutToken();
        userForChange.then().assertThat().body("message", equalTo("You should be authorised"))
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
