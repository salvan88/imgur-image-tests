package ru.vasiljev.a.a;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.valijev.a.a.Endpoints;
import ru.valijev.a.a.enums.Errors;
import ru.valijev.a.a.enums.Images;
import ru.valijev.a.a.dto.PostImageUploadResponse;
import ru.valijev.a.a.utils.FileEncodingUtils;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("POST upload image test case")
public class PostImageUploadTest extends BaseTest {

    private PostImageUploadResponse response;
    private String delImageHash;

    /* Позитивные тесты */

    @Test
    @Step("Тест")
    @DisplayName("(+) Изображение(JPG) передача в Base64 меньше 10 Мб")
    void uploadFile10JpgBase64PositiveTest() {
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
        delImageHash = response.getData().getDeletehash();
    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Изображение(BMP) передача файлом в 1 pix")
    @Description("Ошибка несовпадения загруженного типа изображения")
    void uploadFileSmallBmpPositiveTest() {
        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.smallBmpExample.path))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

        assertThat(response.getData().getType()).isEqualTo("image/bmp");

        delImageHash = response.getData().getDeletehash();
    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Изображение(GIF) передача URL меньше 10Мб")
    void uploadFileGifUrlPositiveTest() {
        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", Images.UrlImageExample.path)
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respPosSpec)
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

        assertThat(response.getData().getType()).isEqualTo("image/gif");
        assertThat(response.getData().getAnimated()).isEqualTo(true);

        delImageHash = response.getData().getDeletehash();
    }

    /* Негативные тесты */

    @Test
    @Step("Тест")
    @DisplayName("(-) Изображение(BMP) передача файлом больше 10Мб")
    void uploadFile11MbNegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.bigImageFile.path))
                .expect()
                .body("data.error", is(Errors.overSizeLimit.message))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Аудио файл(MP3) передача файлом меньше 10Мб")
    void uploadFileMp3NegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.audioFile.path))
                .expect()
                .body("data.error.message", is(Errors.invalidType.message))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Текстовый файл(PDF) передача файлом меньше 10Мб")
    void uploadFilePdfNegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.pdfFile.path))
                .expect()
                .body("data.error.message", is(Errors.invalidType.message))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Видео файл(MP4) передача файлом меньше 10Мб")
    void uploadFileSmallMp4NegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.videoFile.path))
                .expect()
                .body("data.error.message", is(Errors.invalidType.message))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @Test
    @Step("Тест")
    @DisplayName("(-) Текстовый файл(BMP) передача файлом")
    void uploadFileTxtInBmpFormatNegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.txtFile.path))
                .expect()
                .body("data.error.message", is(Errors.invalidType.message))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respNegSpec);
    }

    @AfterEach
    @Step("Удаление мусора")
    @DisplayName("Удаление мусора")
    void tearDown() {
        if(delImageHash != null) {
            deleteImage(delImageHash);
        }
    }
}