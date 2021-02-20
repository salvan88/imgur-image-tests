package ru.vasiljev.a.a;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class BaseTest {
    static Properties prop = new Properties();
    static String token;
    static String username;

    static RequestSpecification reqAuthSpec;
    static ResponseSpecification respPosSpec;
    static ResponseSpecification respNegSpec;

    @BeforeAll
    static void beforeAll() {
        loadProperties();

        token = prop.getProperty("token");
        username = prop.getProperty("username");

        RestAssured.baseURI = prop.getProperty("base.url");

        reqAuthSpec = createAuthReqSpec();
        respPosSpec = createPositiveRespSpec();
        respNegSpec = createNegativeRespSpec();
    }

    static RequestSpecification createAuthReqSpec() {
        return reqAuthSpec = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();
    }

    static ResponseSpecification createPositiveRespSpec() {
        return respPosSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectBody("success", is(true))
                .expectBody("data.id", is(notNullValue()))
                .expectContentType(ContentType.JSON)
                .build();
    }

    static ResponseSpecification createNegativeRespSpec() {
        return respNegSpec = new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectStatusLine("HTTP/1.1 400 Bad Request")
                .expectBody("success", is(false))
                .expectContentType(ContentType.JSON)
                .build();
    }

    static void loadProperties()  {
        try (InputStream file = new FileInputStream("src/test/resources/application.properties")) {
            prop.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
