package ru.vasiljev.a.a;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("POST update image test case")
public class PostUpdateImageTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private final String imageURL = "https://is.gd/fqQYK4";

    @BeforeEach
    void setUp() {
        JsonPath result = given()
                .headers("Authorization", token)
                .multiPart("image", imageURL)
                .when()
                .post("/image")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        imageHash = result.getString("data.id");
        delImageHash = result.getString("data.deletehash");
    }

    @Test
    @DisplayName("Обновить title у изображения (authed)")
    void postUpdateImageTitleAuthedTest() {
        given()
                .headers("Authorization", token)
                .multiPart("title", "123")
                .expect()
                .body("success", is(true))
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    @DisplayName("Обновить description у изображения (authed)")
    void postUpdateImageDescriptionAuthedTest() {
        given()
                .headers("Authorization", token)
                .multiPart("description", "321")
                .expect()
                .body("success", is(true))
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Обновить description у изображения (un-authed)")
    void postUpdateImageDescriptionUnAuthedTest() {
        given()
                .multiPart("description", "321")
                .expect()
                .body("success", is(false))
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .statusCode(401);

    }

    @AfterEach
    @DisplayName("Удаление мусора")
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("/image/{delImageHash}", delImageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}