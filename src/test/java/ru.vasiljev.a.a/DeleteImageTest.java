package ru.vasiljev.a.a;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("DELETE image test case")
public class DeleteImageTest extends BaseTest {

    private String delImageHash;
    private final String imageURL = "https://is.gd/fqQYK4";

    @BeforeEach
    void setUp() {
        delImageHash = given()
                .headers("Authorization", token)
                .multiPart("image", imageURL)
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @DisplayName("Удаление неавторизованным пользователем")
    void deleteImageNoAuthedTest() {
        given()
                .expect()
                .body("data", is(notNullValue()))
                .body("success", is(false))
                .when()
                .delete("/image/{imageHash}", delImageHash)
                .prettyPeek()
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Удаление авторизованным пользователем")
    void deleteImageAuthedTest() {
        given()
                .headers("Authorization", token)
                .expect()
                .body("data", is(true))
                .body("success", is(true))
                .when()
                .delete("/image/{delImageHash}", delImageHash)
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
