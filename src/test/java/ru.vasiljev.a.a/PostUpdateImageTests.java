package ru.vasiljev.a.a;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class PostUpdateImageTests extends BaseTest {

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
    @DisplayName("Обновить title у изображения")
    void getImageExistTest() {
        given()
                .headers("Authorization", token)
                .multiPart("title", "123")
                .multiPart("description", "321")
                .expect()
                .body("success", is(true))
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .statusCode(200);

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