package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.valijev.a.a.Endpoints;
import ru.valijev.a.a.enums.Errors;
import ru.valijev.a.a.dto.GetImageResponse;
import ru.valijev.a.a.dto.PostImageUploadResponse;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static ru.valijev.a.a.steps.CommonRequest.uploadCommonImage;

@DisplayName("GET image test case")
public class GetImageTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private PostImageUploadResponse response;

    @BeforeEach
    void setUp() {

        response = uploadCommonImage(reqAuthSpec);

        delImageHash = response.getData().getDeletehash();
        imageHash = response.getData().getId();

    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Получение изображения")
    void getImageExistTest() {
        GetImageResponse response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .when()
                .get(Endpoints.GET_EXIST_IMAGE_REQUEST, imageHash)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetImageResponse.class);

        assertThat(response.getData().getAccountId().toString()).isEqualTo("145276575");
        delImageHash = response.getData().getDeletehash();
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Получение несуществующего изображения")
    void getImageNotExistTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .expect()
                .body("data.error", is(Errors.imageIdReq.message))
                .when()
                .get(Endpoints.GET_NOT_EXIST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @AfterEach
    @Step("Удаление мусора")
    @DisplayName("Удаление мусора")
    void tearDown() {
        deleteImage(delImageHash);
    }
}
