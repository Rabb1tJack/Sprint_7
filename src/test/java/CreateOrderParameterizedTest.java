import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.*;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@RunWith(Parameterized.class)
public class CreateOrderParameterizedTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    private final String[] color;


    public CreateOrderParameterizedTest(String[] color) {
        this.color = color;

    }

    @Parameterized.Parameters
    public static Object[][] colours(){
        return new Object[][]{
                { new String[] { "BLACK" }},
                { new String[] { "GREY" }},
                { new String[] { "BLACK", "GREY" }},
                { new String[] { }},
                { null},
        };
    }
    @Test
    public void createOrderTest(){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                "Luffy",
                "Pupkin",
                "RedLine",
                "Molodeznaya",
                "88005553535",
                100500,
                "2020-06-06",
                "I'll be king of pirates!",
                color
                );
        Response response = sendPostRequestCreateOrder(createOrderRequest);
        compareStatusCodeCreateOrder(response, 201);
        CreateOrderResponse actualBody =  getCreateOrderResponse(response);
        validateOrderCreateResponseIsNotNull(actualBody);
        validateCreateOrderResponseTrack(actualBody);

    }
    @Step("Send POST request to /api/v1/orders")
    public Response sendPostRequestCreateOrder(CreateOrderRequest request) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(request)
                .post("/api/v1/orders");

        return response;
    }
    @Step("Compare Response.statusCode to something")
    public void compareStatusCodeCreateOrder(Response response, int expectedStatusCode){
        int actualStatusCode = response.statusCode();
        MatcherAssert.assertThat(actualStatusCode, is(expectedStatusCode));
    }
    @Step("Validate CourierAddResponse object is not null")
    public void validateOrderCreateResponseIsNotNull(CreateOrderResponse actualBody) {
        MatcherAssert.assertThat(actualBody, notNullValue());
    }
    @Step("Deserialize Response.body as object type of CourierAddResponse")
    public CreateOrderResponse getCreateOrderResponse(Response response) {
        return response.body().as(CreateOrderResponse.class);
    }
    @Step("Validate CreateOrderResponse.track")
    public void validateCreateOrderResponseTrack(CreateOrderResponse actualBody){
        MatcherAssert.assertThat(actualBody.getTrack(),is(not(0)));
    }

}
