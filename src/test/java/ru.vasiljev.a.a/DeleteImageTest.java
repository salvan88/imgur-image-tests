package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("DELETE image test case")
public class DeleteImageTest extends BaseTest {

    private String delImageHash;
    private final String imageURL = "https://is.gd/fqQYK4";

    @BeforeEach
    @Step("Подготовка")
    void setUp() {
        delImageHash = given()
                .filter(new AllureRestAssured())
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
    @Step("Тест")
    @DisplayName("(-) Удаление неавторизованным пользователем")
    void deleteImageNoAuthedTest() {
        given()
                .filter(new AllureRestAssured())
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
    @Step("Тест")
    @DisplayName("(+) Удаление авторизованным пользователем")
    void deleteImageAuthedTest() {
        given()
                .filter(new AllureRestAssured())
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
