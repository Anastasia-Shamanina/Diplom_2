import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;
import static org.hamcrest.Matchers.*;
import static constants.Url.URL_BURGERS;

public class GetOrderFromUserTest {
    String email = "ivanovanastia_6@gmail.com";
    String password = "123456";
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
        Order order = Order.getOrder();
        order.createOrder(order, accessToken);
    }

    @Test
    @DisplayName("Получить заказ авторизованного пользователя")
    public void getOrder() {
        Order order = Order.getOrder();
        order.getOrder(accessToken)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

   @Test
   @DisplayName("Получить заказ неавторизованного пользователя")
   public void getOrderWithoutToken() {
       Order order = Order.getOrder();
       order.getOrderWithoutToken()
               .then().assertThat().body("message", equalTo("You should be authorised"))
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
