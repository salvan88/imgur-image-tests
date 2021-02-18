package ru.vasiljev.a.a;

import io.qameta.allure.Description;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@DisplayName("POST upload image test case")
public class PostImageUploadTest extends BaseTest {

    private final String smallJpg = "bob.jpg";
    private final String smallBmpExample = "1.bmp";
    private final String audioFile = "testAudioExample.mp3";
    private final String pdfFile = "pdfExample.pdf";
    private final String videoFile = "videoExample(8mb).mp4";
    private final String txtFile = "txtFileInBmpFormat.bmp";
    private final String bigImageFile = "11mb.bmp";
    private String delImageHash;

    /** Позитивные тесты **/

    @Test
    @DisplayName("Изображение(JPG) передача в Base64 меньше 10 Мб")
    void uploadFile10JpgBase64PositiveTest() {
        delImageHash = given()
                .headers("Authorization", token)
                .multiPart("image", getFileByteContent(smallJpg))
                .expect()
                .body("success", is(true))
                .body("data.type", is("image/jpeg"))
                .body("data.id", is(notNullValue()))
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
    @DisplayName("Изображение(BMP) передача файлом в 1 pix")
    @Description("Ошибка несовпадения загруженного типа изображения")
    void uploadFileSmallBmpPositiveTest() {
        delImageHash = given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(smallBmpExample)))
                .expect()
                .body("success", is(true))
                .body("data.type", is("image/bmp"))
                .body("data.id", is(notNullValue()))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    @DisplayName("Изображение(GIF) передача URL меньше 10Мб")
    void uploadFileGifUrlPositiveTest() {
        delImageHash = given()
                .headers("Authorization", token)
                .multiPart("image", "https://99px.ru/sstorage/86/2017/08/image_861808171630169389226.gif")
                .expect()
                .body("success", is(true))
                .body("data.type", is("image/gif"))
                .body("data.id", is(notNullValue()))
                .body("data.animated", is(true))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    /** Негативные тесты **/

    @Test
    @DisplayName("Изображение(BMP) передача файлом больше 10Мб")
    void uploadFile11MbNegativeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(bigImageFile)))
                .expect()
                .body("success", is(false))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Аудио файл(MP3) передача файлом меньше 10Мб")
    void uploadFileMp3NegativeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(audioFile)))
                .expect()
                .body("success", is(false))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Текстовый файл(PDF) передача файлом меньше 10Мб")
    void uploadFilePdfNegativeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(pdfFile)))
                .expect()
                .body("success", is(false))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Видео файл(MP4) передача файлом меньше 10Мб")
    void uploadFileSmallMp4NegativeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(videoFile)))
                .expect()
                .body("success", is(false))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Текстовый файл(BMP) передача файлом")
    void uploadFileTxtInBmpFormatNegativeTest() {
        given()
                .headers("Authorization", token)
                .multiPart("image", new File(getFilePathContent(txtFile)))
                .expect()
                .body("success", is(false))
                .when()
                .post("/image")
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @AfterEach
    @DisplayName("Удаление мусора")
    void tearDown() {
        if(delImageHash != null) {
            given()
                    .headers("Authorization", token)
                    .when()
                    .delete("/image/{delImageHash}", delImageHash)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
    }

    private String getFileByteContent(String str)  {
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

    private String getFilePathContent(String str) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(str)).getFile());
        return file.getAbsolutePath();
    }

}