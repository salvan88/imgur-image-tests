package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("POST update image test case")
public class PostUpdateImageTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private final String imageURL = "https://is.gd/fqQYK4";


    @BeforeEach
    @Step("Подготовка")
    void setUp() {
        JsonPath result = given()
                .filter(new AllureRestAssured())
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
    @Step("Тест")
    @DisplayName("(+) Обновить title у изображения (authed)")
    void postUpdateImageTitleAuthedTest() {
        given()
                .filter(new AllureRestAssured())
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
    @Step("Тест")
    @DisplayName("(+) Обновить description у изображения (authed)")
    void postUpdateImageDescriptionAuthedTest() {
        given()
                .filter(new AllureRestAssured())
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
    @Step("Тест")
    @DisplayName("(-) Обновить description у изображения (un-authed)")
    void postUpdateImageDescriptionUnAuthedTest() {
        given()
                .filter(new AllureRestAssured())
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