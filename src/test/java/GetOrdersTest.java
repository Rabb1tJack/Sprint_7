import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.GetOrdersResponse;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class GetOrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void getOrdersTest() {
        Response response = sendPostRequestGetOrders();
        compareStatusCodeGetOrders(response, 200);
        GetOrdersResponse actualBody = getGetOrdersResponse(response);
        validateGetOrdersResponseIsNotNull(actualBody);
        validateGetOrdersResponseResponseOrders(actualBody);
    }
    @Step("Send GET request to /api/v1/orders")
    public Response sendPostRequestGetOrders() {
        return given()
            .header("Content-type", "application/json")
            .and()
            .get("/api/v1/orders");
    }
    @Step("Compare Response.statusCode to something")
    public void compareStatusCodeGetOrders(Response response, int expectedStatusCode){
        int actualStatusCode = response.statusCode();
        MatcherAssert.assertThat(actualStatusCode, is(expectedStatusCode));
    }
    @Step("Validate GetOrdersResponse object is not null")
    public void validateGetOrdersResponseIsNotNull(GetOrdersResponse actualBody) {
        MatcherAssert.assertThat(actualBody, notNullValue());
    }
    @Step("Deserialize Response.body as object type of GetOrdersResponse")
    public GetOrdersResponse getGetOrdersResponse(Response response) {
        return response.body().as(GetOrdersResponse.class);
    }
    @Step("Validate GetOrdersResponseResponse.orders")
    public void validateGetOrdersResponseResponseOrders(GetOrdersResponse actualBody){
        int ordersCount = actualBody.getOrders() == null ? 0 : actualBody.getOrders().length;
        MatcherAssert.assertThat(ordersCount > 0, is(true));
    }
}
