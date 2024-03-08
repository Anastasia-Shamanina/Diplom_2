import java.util.ArrayList;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.ResourceBundle;
import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class Order {
    private String accessToken;

    private ArrayList<Object> ingredients;

    public ArrayList<Object> getIngredients() {
        return ingredients;
    }
    public void setIngredients(ArrayList<Object> ingredients) {
        this.ingredients = ingredients;
    }
    public Order(ArrayList<Object> ingredients) {
        this.ingredients = ingredients;
    }

    private static String bunN200i = "61c0c5a71d1f82001bdaaa75";
    private static String fillingBeef = "61c0c5a71d1f82001bdaaa71";
    private static String incorrectHash = "12a34567b89";
    public static Order getOrder() {
        ArrayList<Object> order = new ArrayList<>();
        order.add(bunN200i);
        order.add(fillingBeef);
        return new Order(order);
    }

    public static Order getIncorrectOrder() {
        ArrayList<Object> order = new ArrayList<>();
        order.add(incorrectHash);
        return new Order(order);
    }

    //Создание заказа с авторизацией
    public Response createOrder(Order order, String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .headers("Authorization", accessToken)
                .body(new Order(ingredients))
                .when()
                .post(CREATE_ORDER);
    }

    //Создание заказа без авторизацией
    public Response createOrderWithoutToken(Order order) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .body(new Order(ingredients))
                .when()
                .post(CREATE_ORDER);
    }

    //Получение заказа пользователя с авторизацией
    public Response getOrder(String accessToken) {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .headers("Authorization", accessToken)
                .when()
                .get(GET_ORDER);
    }
    //Получение заказа пользователя без авторизации
    public Response getOrderWithoutToken() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .when()
                .get(GET_ORDER);
    }
}

