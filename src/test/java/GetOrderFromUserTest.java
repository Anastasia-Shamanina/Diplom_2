import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import methods.BaseHttpClient;
import methods.OrderMethods;
import methods.UserMethods;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;
import pojo.Order;
import pojo.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static constants.Url.URL_BURGERS;

public class GetOrderFromUserTest {
    String email = "ivanovanastia_6@gmail.com";
    String password = "123456";
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
        OrderMethods orderMethods = new OrderMethods();
        Response getIngredient = orderMethods.getIngredientsHash();
        List<String> ingredients = new ArrayList<>(getIngredient.then().log().all().statusCode(200).extract().path("data._id")); // Извлечение ID ингредиентов из ответа
        Order order = new Order(ingredients.subList(0,3));
        orderMethods.createOrder(order, accessToken);
    }

    @Test
    @DisplayName("Получить заказ авторизованного пользователя")
    public void getOrder() {
        OrderMethods orderMethods = new OrderMethods();
        orderMethods.getOrder(accessToken)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

   @Test
   @DisplayName("Получить заказ неавторизованного пользователя")
   public void getOrderWithoutToken() {
       OrderMethods orderMethods = new OrderMethods();
       orderMethods.getOrderWithoutToken()
               .then().assertThat().body("message", equalTo("You should be authorised"))
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
