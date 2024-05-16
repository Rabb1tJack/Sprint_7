import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.CourierAddRequest;
import models.CourierAddResponse;
import models.CourierLoginResponse;
import models.FailedResponse;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierAddTest {
    public CourierAddRequest courierAddRequest = new CourierAddRequest("test11111112login", "testpass", "TestName");
    public CourierAddRequest courierAddRequestDuplicate = new CourierAddRequest("test11111112login", "test", "Test");

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

    }
    @Test
    public void courierAddPositiveTest(){
        Response response = sendPostRequestAddCourier(courierAddRequest);

        compareStatusCodeAddCourier(response, 201);

        CourierAddResponse actualBody = getCourierAddResponse(response);

        validateCourierAddResponseIsNotNull(actualBody);

        CourierAddResponse expectedBody = new CourierAddResponse(true);

        compareCourierAddResponse(actualBody, expectedBody);
    }
    @Test
    public void courierAddCourierDuplicateTest(){
        //создаем курьера
        Response response = sendPostRequestAddCourier(courierAddRequest);

        compareStatusCodeAddCourier(response, 201);

        CourierAddResponse actualBody = getCourierAddResponse(response);

        validateCourierAddResponseIsNotNull(actualBody);

        CourierAddResponse expectedBody = new CourierAddResponse(true);

        compareCourierAddResponse(actualBody, expectedBody);
        //пытаемся создать такого же курьера
        response = sendPostRequestAddCourier(courierAddRequestDuplicate);

        compareStatusCodeAddCourier(response, 409);
        FailedResponse failedResponseActualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(failedResponseActualBody);
        compareFailedResponse(failedResponseActualBody, new FailedResponse("Этот логин уже используется. Попробуйте другой."));
    }
    @Test
    public void courierAddWithoutLoginTest(){
        Response response = sendPostRequestAddCourier(new CourierAddRequest(null,"test","test"));
        compareStatusCodeAddCourier(response, 400);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Недостаточно данных для создания учетной записи"));
    }
    @Test
    public void courierAddWithoutPasswordTest(){
        Response response = sendPostRequestAddCourier(new CourierAddRequest("takogologinatochnonet",null,"test"));
        compareStatusCodeAddCourier(response, 400);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Недостаточно данных для создания учетной записи"));
    }

    @Step("Send POST request to /api/v1/courier")
    public Response sendPostRequestAddCourier(CourierAddRequest request) {
        return given()
            .header("Content-type", "application/json")
            .and()
            .body(request)
            .post("/api/v1/courier");
    }

    @Step("Compare Response.statusCode to something")
    public void compareStatusCodeAddCourier(Response response, int expectedStatusCode){
        int actualStatusCode = response.statusCode();
        MatcherAssert.assertThat(actualStatusCode, is(expectedStatusCode));
    }

    @Step("Deserialize Response.body as object type of CourierAddResponse")
    public CourierAddResponse getCourierAddResponse(Response response) {
        return response.body().as(CourierAddResponse.class);
    }

    @Step("Validate CourierAddResponse object is not null")
    public void validateCourierAddResponseIsNotNull(CourierAddResponse actualBody) {
        MatcherAssert.assertThat(actualBody, notNullValue());
    }

    @Step("Compare CourierAddResponse object to something")
    public void compareCourierAddResponse(CourierAddResponse actualBody, CourierAddResponse expectedBody){
        MatcherAssert.assertThat(actualBody.getOk(), is(expectedBody.getOk()));
    }
    @Step("Compare FailedResponse to something")
    public void compareFailedResponse(FailedResponse actualBody, FailedResponse expectedBody){
        MatcherAssert.assertThat(actualBody.getMessage(), is(expectedBody.getMessage()));
    }

    @Step("Validate FailedResponse object is not null")
    public void validateFailedResponseIsNotNull(FailedResponse actualBody) {
        MatcherAssert.assertThat(actualBody, notNullValue());
    }

    @Step("Deserialize Response.body as object type of FailedResponse")
    public FailedResponse getFailedResponse(Response response) {
        return response.body().as(FailedResponse.class);
    }

    @After
    public void clear() {

        String login = courierAddRequest.getLogin();
        String password = courierAddRequest.getPassword();

        String json = String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password);

        Response response = given()
            .header("Content-type", "application/json")
            .and()
            .body(json)
            .post("/api/v1/courier/login");

        if (response.statusCode() == 200) {
            CourierLoginResponse loginBody = response.body().as(CourierLoginResponse.class);

            if (loginBody != null && loginBody.getId() != 0) {
                given()
                    .header("Content-type", "application/json")
                    .and()
                    .delete(String.format("/api/v1/courier/%s", loginBody.getId()));
            }
        }
    }
}

