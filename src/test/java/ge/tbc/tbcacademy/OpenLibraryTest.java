package ge.tbc.tbcacademy;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import openlibrary.api.SearchApi;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import openlibrary.invoker.ApiClient;
import openlibrary.invoker.JacksonObjectMapper;


import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.apache.http.HttpStatus.SC_OK;
import static openlibrary.invoker.ResponseSpecBuilders.shouldBeCode;
import static openlibrary.invoker.ResponseSpecBuilders.validatedWith;


public class OpenLibraryTest {

    private ApiClient api;

    @BeforeSuite
    public void createApi() {
        api = ApiClient.api(ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .log(LogDetail.ALL)
                        .setConfig(config()
                                .objectMapperConfig(objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson())))
                        .addFilter(new ErrorLoggingFilter())
                        .setBaseUri("https://openlibrary.org")));
    }

    @Test
    public void printRawResponseTest() {

        SearchApi.ReadSearchJsonSearchJsonGetOper starWars = api.search().readSearchJsonSearchJsonGet().qQuery("star wars");
        Response response = starWars.execute(validatedWith(shouldBeCode(200)).andThen(r -> r));
        System.out.println(response.jsonPath().getString("docs[0].author_alternative_name"));
    }

//    @Test
//    public void findObjectRestAssuredExample() {
//        var petList =
//                given()
//                        .log().all()
//                        .spec(new RequestSpecBuilder().setConfig(config().objectMapperConfig(
//                                objectMapperConfig().defaultObjectMapper(
//                                        JacksonObjectMapper.jackson()
//                                ))).build()
//                        )
//                        .queryParam("status", Pet.StatusEnum.AVAILABLE)
////                        .accept("application/json,application/xml")
//                        .accept(ContentType.JSON)
//                        .when()
//                        .get("https://petstore3.swagger.io/api/v3/pet/findByStatus")
//                        .then()
//                        .log().all()
//                        .statusCode(HttpStatus.SC_OK)
//                        .extract().jsonPath().getList("$", Pet.class);
//
//        petList.forEach(pet -> System.out.println(pet.getName()));
//    }


}