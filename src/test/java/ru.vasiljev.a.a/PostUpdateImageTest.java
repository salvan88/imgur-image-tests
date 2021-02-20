package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.valijev.a.a.dto.PostImageUploadResponse;

import static io.restassured.RestAssured.given;
import static ru.valijev.a.a.steps.CommonRequest.uploadCommonImage;

@DisplayName("POST update image test case")
public class PostUpdateImageTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private PostImageUploadResponse response;

    @BeforeEach
    @Step("Подготовка")
    void setUp() {

        response = uploadCommonImage(reqAuthSpec);
        delImageHash = response.getData().getDeletehash();
        imageHash = response.getData().getId();

    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Обновить title у изображения (authed)")
    void postUpdateImageTitleAuthedTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("title", "123")
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .spec(respPosSpec);

    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Обновить description у изображения (authed)")
    void postUpdateImageDescriptionAuthedTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("description", "321")
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .spec(respPosSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Обновить description у изображения (un-authed)")
    void postUpdateImageDescriptionUnAuthedTest() {
        given()
                .filter(new AllureRestAssured())
                .multiPart("description", "321")
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .spec(respNoAuthSpec);

    }

    @AfterEach
    @Step("Удаление мусора")
    @DisplayName("Удаление мусора")
    void tearDown() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .when()
                .delete("/image/{delImageHash}", delImageHash)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}