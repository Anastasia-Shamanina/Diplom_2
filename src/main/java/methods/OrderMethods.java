package methods;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.Order;
import java.util.ArrayList;
import java.util.List;
import pojo.Order.*;

import static constants.Handle.*;
import static io.restassured.RestAssured.given;


public class OrderMethods {
    @Step("Получить данные об ингредиентах")
    public Response getIngredientsHash() {
        return given()
                .get(GET_INGREDIENTS);
    }

    @Step("Создать заказ с авторизацией")
    public Response createOrder(Order order, String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .headers("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutToken(Order order) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Получить заказы авторизованного пользователя")
    public Response getOrder(String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .headers("Authorization", accessToken)
                .when()
                .get(GET_ORDER);
    }
    @Step("Получить заказы не авторизованного пользователя")
    public Response getOrderWithoutToken() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .when()
                .get(GET_ORDER);
    }
}
