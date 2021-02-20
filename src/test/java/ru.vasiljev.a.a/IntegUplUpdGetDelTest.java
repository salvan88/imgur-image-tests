package ru.vasiljev.a.a;


import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.valijev.a.a.Endpoints;
import ru.valijev.a.a.Images;
import ru.valijev.a.a.dto.GetImageResponse;
import ru.valijev.a.a.dto.PostImageUploadResponse;
import ru.valijev.a.a.utils.FileEncodingUtils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;


public class IntegUplUpdGetDelTest extends BaseTest {

    private String imageHash;
    private String delImageHash;
    private PostImageUploadResponse response;
    Faker faker = new Faker();

    @Test
    @Step("Интеграционный тест")
    @DisplayName("(+) Интеграционный тест на загрузку, обновление, получение и удаление изображения(JPG)")
    void uplUpdGetDelIntegrationTest() {

        /* Загрузка изображения */

        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", FileEncodingUtils.getFileByteContent(Images.smallJpg.path))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

        assertThat(response.getData().getType()).isEqualTo("image/jpeg");

        imageHash = response.getData().getId();
        delImageHash = response.getData().getDeletehash();

        /* Обновление title и description */

        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("title", "" + faker.chuckNorris())
                .multiPart("description", "" + faker.chuckNorris())
                .when()
                .post("/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .spec(respPosSpec);

        /* Получение изображения */

        GetImageResponse getImgResponse = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .when()
                .get(Endpoints.GET_EXIST_IMAGE_REQUEST, imageHash)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(GetImageResponse.class);

        assertThat(response.getData().getId()).isEqualTo(getImgResponse.getData().getId());
        assertThat(getImgResponse.getData().getTitle()).isNotNull();
        assertThat(getImgResponse.getData().getDescription()).isNotNull();

        /* Удаление изображения */

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
