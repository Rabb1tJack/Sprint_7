import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.*;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {
    private String login = "neTakoiLogin";
    private String password = "password";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        given()
            .header("Content-type", "application/json")
            .and()
            .body(new CourierAddRequest(login, password, "Name"))
            .post("/api/v1/courier");
    }

    @Test
    public void courierLoginPositiveTest(){
        Response response = sendPostRequestLoginCourier(new CourierLoginRequest(login,password));
        compareStatusCodeLoginCourier(response, 200);
        CourierLoginResponse actualBody = getCourierLoginResponse(response);
        validateCourierLoginResponseIsNotNull(actualBody);
        validateCourierLoginResponseId(actualBody);
    }
    @Test
    public void courierLoginWithoutLoginTest(){
        Response response = sendPostRequestLoginCourier(new CourierLoginRequest(null,password));
        compareStatusCodeLoginCourier(response, 400);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Недостаточно данных для входа"));
    }
    @Test
    public void courierLoginWithoutPasswordTest(){
        Response response = sendPostRequestLoginCourier(new CourierLoginRequest(login,""));
        compareStatusCodeLoginCourier(response, 400);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Недостаточно данных для входа"));
    }
    @Test
    public void courierLoginWithIncorrectLoginTest(){
        Response response = sendPostRequestLoginCourier(new CourierLoginRequest("incorrectLogin",password));
        compareStatusCodeLoginCourier(response, 404);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Учетная запись не найдена"));
    }
    @Test
    public void courierLoginWithIncorrectPasswordTest(){
        Response response = sendPostRequestLoginCourier(new CourierLoginRequest(login,"incorrectPassword"));
        compareStatusCodeLoginCourier(response, 404);
        FailedResponse actualBody = getFailedResponse(response);
        validateFailedResponseIsNotNull(actualBody);
        compareFailedResponse(actualBody, new FailedResponse("Учетная запись не найдена"));
    }
    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPostRequestLoginCourier(CourierLoginRequest request) {
        Response response = given()
            .header("Content-type", "application/json")
            .and()
            .body(request)
            .post("/api/v1/courier/login");

        return response;
    }
    @Step("Compare Response.statusCode to something")
    public void compareStatusCodeLoginCourier(Response response, int expectedStatusCode){
        int actualStatusCode = response.statusCode();
        MatcherAssert.assertThat(actualStatusCode, is(expectedStatusCode));
    }
    @Step("Deserialize Response.body as object type of CourierLoginResponse")
    public CourierLoginResponse getCourierLoginResponse(Response response) {
        return response.body().as(CourierLoginResponse.class);
    }
    @Step("Validate CourierLoginResponse object is not null")
    public void validateCourierLoginResponseIsNotNull(CourierLoginResponse actualBody) {
        MatcherAssert.assertThat(actualBody, notNullValue());
    }
    @Step("Validate CourierLoginResponse.id")
    public void validateCourierLoginResponseId(CourierLoginResponse actualBody){
        MatcherAssert.assertThat(actualBody.getId(),is(not(0)));
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
