package components;

import common.Spec;
import config.Environment;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class UserComponent { // each method in a component would return a REST-assured Response object

    public static Response getAllUsers() {
        Response allUsersResponse = given()
                .spec(Spec.requestSpec)
                .get(Environment.usersEndpoint);
        allUsersResponse
                .then()
                .assertThat()
                .spec(Spec.successResponseSpec);
        return allUsersResponse;
    }

    public static Response getUser(String username) {
        User user = new User( );
        user.setUsername("Delphine");
        Response allUsersResponse = given()
                .spec(Spec.requestSpec)
                //.param("username", username)
                .body(user)
                .get(Environment.usersEndpoint);
        allUsersResponse
                .then()
                .assertThat()
                .spec(Spec.successResponseSpec);
        return allUsersResponse;
    }
}
