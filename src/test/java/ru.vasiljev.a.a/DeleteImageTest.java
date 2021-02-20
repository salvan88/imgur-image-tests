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

@DisplayName("DELETE image test case")
public class DeleteImageTest extends BaseTest {

    private String delImageHash;
    private PostImageUploadResponse response;

    @BeforeEach
    @Step("Подготовка")
    void setUp() {

        response = uploadCommonImage(reqAuthSpec);
        delImageHash = response.getData().getDeletehash();
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Удаление неавторизованным пользователем")
    void deleteImageNoAuthedTest() {
        given()
                .filter(new AllureRestAssured())
                .when()
                .delete("/image/{imageHash}", delImageHash)
                .prettyPeek()
                .then()
                .spec(respNoAuthSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Удаление авторизованным пользователем")
    void deleteImageAuthedTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
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
