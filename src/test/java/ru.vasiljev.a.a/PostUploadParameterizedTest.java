package ru.vasiljev.a.a;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.valijev.a.a.Endpoints;
import ru.valijev.a.a.dto.PostImageUploadResponse;
import ru.valijev.a.a.utils.FileEncodingUtils;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class PostUploadParameterizedTest extends BaseTest {

    private PostImageUploadResponse response;
    private String delImageHash;


    @ParameterizedTest
    @Step("Тест")
    @DisplayName("(+) Загрузка рандомного изображения Base64")
    @CsvFileSource(resources = "/paramTestFiles/paramImageExample.csv", numLinesToSkip = 1)
    void uploadParameterizedPositiveTest(String image, String expect) {
        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", FileEncodingUtils.getFileByteContent(image))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

        assertThat(response.getData().getType()).isEqualTo(expect);
        delImageHash = response.getData().getDeletehash();
    }

    @AfterEach
    @Step("Удаление мусора")
    @DisplayName("Удаление мусора")
    void tearDown() {
        if(delImageHash != null) {
            given()
                    .filter(new AllureRestAssured())
                    .spec(reqAuthSpec)
                    .when()
                    .delete(Endpoints.DELETE_IMAGE_REQUEST, delImageHash)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }

    }
}
