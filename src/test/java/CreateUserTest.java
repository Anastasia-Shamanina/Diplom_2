import io.qameta.allure.Description;
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

public class CreateUserTest {
    String email = "ivanovanastia_6@gmail.com";
    String incorrectEmail = "";
    String password = "123456";
    String name = "Настя";
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.requestSpecification = BaseHttpClient.baseRequestSpec();
    }

    @Test
    @DisplayName("Cоздание уникального пользователя")
    @Description("Создание пользователя со всеми заполненными необходимими полями")
    public void createUser() {
        User user = new User(email, password, name);
        UserMethods userMethods = new UserMethods();
        userMethods.createUser(user)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        Response responseAccessToken = userMethods.loginUser(user);
        responseAccessToken.then().assertThat().body("accessToken", notNullValue())
                .and()
                .statusCode(200);

        this.accessToken = responseAccessToken.body().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Cоздание НЕ уникального пользователя")
    @Description("Создание пользователя со всеми заполненными необходимими полями, который уже зарегистрирован")
    public void createDuplicateUser() {
        User user = new User(email, password, name);
        UserMethods userMethods = new UserMethods();
        userMethods.createUser(user)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);

        Response responseAccessToken = userMethods.loginUser(user);
        responseAccessToken.then().assertThat().body("accessToken", notNullValue())
                .and()
                .statusCode(200);

        this.accessToken = responseAccessToken.body().jsonPath().getString("accessToken");

        userMethods.createUser(user)
                .then().assertThat().body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Cоздание пользователя без email")
    @Description("Создание пользователя и не заполнить одно из обязательных полей")
    public void createIncorrectUser() {
        User user = new User(incorrectEmail, password, name);
        UserMethods userMethods = new UserMethods();
        userMethods.createUser(user)
                .then().assertThat().body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
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

