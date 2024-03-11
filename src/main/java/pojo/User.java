package pojo;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class User {

    // ключи стали полями типа String
    private String email;
    private String password;
    private String name;

    // конструктор со всеми параметрами
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // конструктор без параметров
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    String emailNew;
    String passwordNew;
    String nameNew;
    private String accessToken;
}
