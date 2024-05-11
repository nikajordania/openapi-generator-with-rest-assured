package ge.tbc.tbcacademy;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pet.store.v3.api.PetApi;
import pet.store.v3.invoker.ApiClient;
import pet.store.v3.invoker.JacksonObjectMapper;
import pet.store.v3.model.Category;
import pet.store.v3.model.Pet;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;
import static pet.store.v3.invoker.ResponseSpecBuilders.shouldBeCode;
import static pet.store.v3.invoker.ResponseSpecBuilders.validatedWith;


public class PetApiV3Test {

    //    private ApiClient api = ApiClient.api(ApiClient.Config.apiConfig());
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
                        .setBaseUri("https://petstore3.swagger.io/api/v3")));
    }

    @Test
    public void printRawResponseTest() {
        Pet body = new Pet();
        body.setId(1L);
        Response res = api.pet().addPet()
                .body(body).execute(response -> response);

        System.out.println(res.getBody().prettyPrint());
    }

    @Test
    public void find() {
        Response res = api.pet().findPetsByStatus().statusQuery(Pet.StatusEnum.AVAILABLE)
                .execute(response -> response);

        System.out.println(res.getBody().prettyPrint());
    }

    @Test
    public void findObjectExample() {
        PetApi.FindPetsByStatusOper pets = api.pet().findPetsByStatus().statusQuery(Pet.StatusEnum.AVAILABLE);
        List<Pet> pets1 = pets.executeAs(validatedWith(shouldBeCode(SC_OK)));

        pets1.forEach(pet -> System.out.println(pet.getName()));

    }

    @Test
    public void findObjectRestAssuredExample() {
        var petList =
                given()
                        .log().all()
                        .spec(new RequestSpecBuilder().setConfig(config().objectMapperConfig(
                                objectMapperConfig().defaultObjectMapper(
                                        JacksonObjectMapper.jackson()
                                ))).build()
                        )
                        .queryParam("status", Pet.StatusEnum.AVAILABLE)
//                        .accept("application/json,application/xml")
                        .accept(ContentType.JSON)
                        .when()
                        .get("https://petstore3.swagger.io/api/v3/pet/findByStatus")
                        .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .extract().jsonPath().getList("$", Pet.class);

        petList.forEach(pet -> System.out.println(pet.getName()));
    }

    @Test
    public void findObjectExampleObj() {
        PetApi.FindPetsByStatusOper pets = api.pet().findPetsByStatus()
                .statusQuery(Pet.StatusEnum.AVAILABLE);
        List<Pet> pets1 = pets.execute(response -> response).jsonPath().getList("$", Pet.class);

        pets1.forEach(pet -> System.out.println(pet.getName()));
    }

//    {
//  "id": 10,
//  "name": "doggie",
//  "category": {
//    "id": 1,
//    "name": "Dogs"
//  },
//  "photoUrls": [
//    "string"
//  ],
//  "tags": [
//    {
//      "id": 0,
//      "name": "string"
//    }
//  ],
//  "status": "available"
//}

    @Test
    public void putPetToStoreTest() {

        Pet pet = new Pet();
        pet
                .id(10L)
                .name("doggie")
                .category(new Category().id(10L).name("asd"))
                .addPhotoUrlsItem("sda")
                .addPhotoUrlsItem("sfdgsgbfb")

                .addTagsItem(new pet.store.v3.model.Tag().id(123L).name("asdas"))

                .status(Pet.StatusEnum.AVAILABLE)


        ;
        Pet pet1 = api.pet().addPet().body(pet).executeAs(response -> {

            response.then().log().all();
            validatedWith(shouldBeCode(200));
            return response;
        });

        System.out.println(pet1);
    }

    @Test
    public void putPetToStoreTest2() {

        Pet pet = new Pet();
        pet
                .id(10L)
                .name("doggie")
                .category(new Category().id(10L).name("Dogs"))
                .addPhotoUrlsItem("s1")
                .addPhotoUrlsItem("s2")

                .addTagsItem(new pet.store.v3.model.Tag().id(123L).name("asdas"))

                .status(Pet.StatusEnum.AVAILABLE);

        var ref = new Object() {
            Response res;
        };


        Pet pet1 = api.pet().addPet().body(pet).executeAs(response -> {
//            response.then().log().all();

            ref.res = response;
            return response;
        });

        System.out.println(pet1);
        ref.res.then()
                .statusCode(200)
                .body("id", equalTo(10),
                        "name", equalTo("doggie"),
                        "category.id", equalTo(10),
                        "category.name", equalTo("Dogs"),
                        "photoUrls", hasItems("s1", "s2"),
                        "tags[0].id", equalTo(123),
                        "tags[0].name", equalTo("asdas"),
                        "status", equalTo("available"));
        ref.res.then().log().headers();
    }

}