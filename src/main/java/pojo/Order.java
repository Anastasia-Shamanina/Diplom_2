package pojo;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import methods.BaseHttpClient;

import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class Order {

    private List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}

