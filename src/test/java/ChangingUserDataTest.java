import methods.BaseHttpClient;
import methods.UserMethods;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import pojo.User;

import static org.hamcrest.Matchers.*;



@RunWith(Parameterized.class)
public class ChangingUserDataTest {
    String accessToken;
    String email = "ivanovanastia_6@gmail.com";
    String password = "123456";
    String name = "Настя";

    @Before
    public void setUp() {
        RestAssured.requestSpecification = BaseHttpClient.baseRequestSpec();

                // Создание пользователя
        User user = new User(email, password, name);
        UserMethods userMethods = new UserMethods();
        Response responseAccessToken = userMethods.createUser(user);
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
        UserMethods userMethods = new UserMethods();
        Response userForChange = userMethods.changeUser(userNew, accessToken);
        userForChange.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void changeUserWithoutAuth() {
        User userNew = new User(emailNew, passwordNew, nameNew);
        UserMethods userMethods = new UserMethods();
        Response userForChange = userMethods.changeUserWithoutToken(userNew);
        userForChange.then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @After
    public void userDeletion() {
        // Отправляем DELETE-запрос на удаление пользователя
        try {
            UserMethods userMethods = new UserMethods();
            userMethods.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Такого пользователя не существует - удаление невозможно.");
        }
    }
}
