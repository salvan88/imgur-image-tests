package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("GET image test case")
public class GetImageTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private final String imageURL = "https://is.gd/fqQYK4";

    @BeforeEach
    @Step("Подготовка")
    void setUp() {
        imageHash = given()
                .filter(new AllureRestAssured())
                .headers("Authorization", token)
                .multiPart("image", imageURL)
                .when()
                .post("/image")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Получение изображения")
    void getImageExistTest() {
        delImageHash = given()
                .filter(new AllureRestAssured())
                .headers("Authorization", token)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .get("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @AfterEach
    @Step("Удаление мусора")
    @DisplayName("Удаление мусора")
    void tearDown() {
        given()
                .filter(new AllureRestAssured())
                .headers("Authorization", token)
                .when()
                .delete("/image/{delImageHash}", delImageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
