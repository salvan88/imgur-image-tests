package ru.valijev.a.a.steps;


import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import ru.valijev.a.a.enums.Images;
import ru.valijev.a.a.dto.PostImageUploadResponse;
import ru.valijev.a.a.utils.FileEncodingUtils;

import static io.restassured.RestAssured.given;

@UtilityClass
public class CommonRequest {

    public static PostImageUploadResponse uploadCommonImage(RequestSpecification spec) {
        RequestSpecification multiPart = spec
                .multiPart(
                        new MultiPartSpecBuilder(FileEncodingUtils.getFileByteContent(Images.smallJpg.path))
                                .controlName("image")
                                .build());

        return given()
                .spec(multiPart)
                .when()
                .post("/image")
                .then()
                .extract()
                .body()
                .as(PostImageUploadResponse.class);
    }

}
