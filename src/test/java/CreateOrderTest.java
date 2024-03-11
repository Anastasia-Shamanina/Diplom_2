import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import jdk.jfr.Description;
import methods.BaseHttpClient;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import org.junit.After;
import pojo.Order;
import pojo.User;
import methods.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static constants.Url.URL_BURGERS;


public class CreateOrderTest {
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
    }

    @Test
    @DisplayName("Создать заказ с авторизацией и ингредиентами")
    public void createOrderWithTokenAndIngredients() {
        OrderMethods orderMethods = new OrderMethods();
        Response getIngredient = orderMethods.getIngredientsHash();
        List<String> ingredients = new ArrayList<>(getIngredient.then().log().all().statusCode(200).extract().path("data._id")); // Извлечение ID ингредиентов из ответа
        Order order = new Order(ingredients.subList(0,3)); // Создание заказа на основе выбранных ингредиентов

        orderMethods.createOrder(order, accessToken)
                .then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Создать заказ с авторизацией без ингредиентов")
    public void createOrderWithTokenWithoutIngredients() {
        OrderMethods orderMethods = new OrderMethods();
        Order order = new Order(null);
        orderMethods.createOrder(order, accessToken)
                .then().assertThat().body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(400);
    }

    private static String incorrectHash = "12a34567b89";
    @Test
    @DisplayName("Создать заказ с авторизацией и неверным хешем")
    public void createOrderWithTokenAndIncorrectHash() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(incorrectHash);
        Order order = new Order(ingredients);
        OrderMethods orderMethods = new OrderMethods();
        orderMethods.createOrder(order, accessToken)
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создать заказ без авторизации")
    @Description("Тест падает потому что, нет проверки авторизации по этой ручке на стороне сервиса, уточнено у наставника, что можно оставить тест, возможно устарела дока.")
    public void createOrderWithoutToken() {
        OrderMethods orderMethods = new OrderMethods();
        Response getIngredient = orderMethods.getIngredientsHash();
        List<String> ingredients = new ArrayList<>(getIngredient.then().log().all().statusCode(200).extract().path("data._id")); // Извлечение ID ингредиентов из ответа
        Order order = new Order(ingredients.subList(0,3));

        orderMethods.createOrderWithoutToken(order)
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
