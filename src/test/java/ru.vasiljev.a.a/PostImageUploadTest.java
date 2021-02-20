package ru.vasiljev.a.a;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import ru.valijev.a.a.Endpoints;
import ru.valijev.a.a.dto.PostImageUploadResponse;
import ru.valijev.a.a.Images;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("POST upload image test case")
public class PostImageUploadTest extends BaseTest {

    private PostImageUploadResponse response;
    private String delImageHash;
    MultiPartSpecification multiPartSpec;
    RequestSpecification uploadReqSpec;

//    @BeforeEach
//    void setUp() {
//        multiPartSpec = new MultiPartSpecBuilder(fileContent)
//                .controlName("image")
//                .build();
//        uploadReqSpec = reqAuthSpec.multiPart(multiPartSpec);
//    }

    /**
     * Позитивные тесты
     **/

    @Test
    @Step("Тест")
    @DisplayName("(+) Изображение(JPG) передача в Base64 меньше 10 Мб")
    void uploadFile10JpgBase64PositiveTest() {
        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", getFileByteContent(Images.smallJpg.path))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

        assertThat(response.getData().getType()).isEqualTo("image/jpeg");
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
    }

    @Test
    @Step("Тест")
    @DisplayName("(+) Изображение(GIF) передача URL меньше 10Мб")
    void uploadFileGifUrlPositiveTest() {
        response = given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.gifExample.path))
                .when()
                .post(Endpoints.POST_IMAGE_REQUEST)
                .prettyPeek()
                .then()
                .spec(respPosSpec)
                .extract()
                .body()
                .as(PostImageUploadResponse.class);

//        assertThat(response.getData().getType()).isEqualTo("image/gif");
//        assertThat(response.getData().getAnimated()).isEqualTo(true);
    }

    /**
     * Негативные тесты
     **/

    @Test
    @Step("Тест")
    @DisplayName("(-) Изображение(BMP) передача файлом больше 10Мб")
    void uploadFile11MbNegativeTest() {
        given()
                .filter(new AllureRestAssured())
                .spec(reqAuthSpec)
                .multiPart("image", new File(Images.bigImageFile.path))
                .expect()
                .body("data.error", is("File is over the size limit"))
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

    private String getFileByteContent(String str) {
        File f = new File(str);
        str = f.getName();

        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource(str)).getFile());

        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }

//    private String getFilePathContent(String str) {
//        ClassLoader classLoader = getClass().getClassLoader();
//        File file = new File(Objects.requireNonNull(classLoader.getResource(str)).getFile());
//        return file.getAbsolutePath();
//    }

}