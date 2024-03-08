import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;

import static org.hamcrest.Matchers.*;
import static constants.Url.URL_BURGERS;


public class CreateOrderTest {
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
    }

    @Test
    @DisplayName("Создать заказ с авторизацией и ингредиентами")
    public void createOrder1() {
        Order order = Order.getOrder();
        order.createOrder(order, accessToken)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создать заказ с авторизацией без ингредиентами")
    public void createOrder2() {
        Order order = new Order(null);
        order.createOrder(order, accessToken)
                .then().assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Создать заказ с авторизацией и неверным хешем")
    public void createOrder3() {
        Order order = Order.getIncorrectOrder();
        order.createOrder(order, accessToken)
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создать заказ без авторизации")
    @Description("Тест падает потому что, нет проверки авторизации по этой ручке на стороне сервиса, уточнено у наставника, что можно оставить тест, возможно устарела дока.")
    public void createOrder4() {
        Order order = Order.getOrder();
        order.createOrderWithoutToken(order)
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
