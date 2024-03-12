import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import methods.BaseHttpClient;
import methods.UserMethods;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;
import pojo.User;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {
    String email = "ivanovanastia_6@gmail.com";
    String incorrectEmail = "ivanovanastia_7@gmail.com";
    String password = "123456";
    String incorrectPassword = "654321";
    String name = "Настя";
    String accessToken;

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

    @Test
    @DisplayName("Логин под существующим пользователем,")
    public void loginUser() {
        User user = new User(email, password, name);
        UserMethods userMethods = new UserMethods();
        userMethods.loginUser(user)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Логин с неверным неверным логином и паролем")
    public void loginNonexistentUser() {
        User user = new User(incorrectEmail, incorrectPassword, name);
        UserMethods userMethods = new UserMethods();
        userMethods.loginUser(user)
                .then().assertThat().body("message", equalTo("email or password are incorrect"))
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
