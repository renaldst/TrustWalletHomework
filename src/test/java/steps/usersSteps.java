package steps;

import constants.Endpoints;
import constants.Path;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.internal.com.google.gson.JsonObject;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import utilities.RestAssuredUtilities;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.fail;

public class usersSteps {

    private static Response response;

    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    public static int id;

    public usersSteps() {

        requestSpec = RestAssuredUtilities.getRequestSpecification();
        requestSpec.baseUri(Path.BASE_URI);
        responseSpec = RestAssuredUtilities.getResponseSpecification();

    }

    @Given("I perform GET call for a specific user with the id {string}")
    public void performGETCallForASpecificUserWithTheId(String userId) {
        response = given()
                .spec(requestSpec)
                .pathParam("user", userId)
                .when()
                .get(Endpoints.GET_OR_UPDATE_OR_DELETE_A_SINGLE_USER)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .log().all()
                .extract().response();
    }

    @And("I see {int} Reqres response status")
    public void verifyReqresResponseStatus(int statusCode) {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);
        assertThat("Status code is not: " + statusCode, response.getStatusCode(), is(statusCode));
    }

    @Then("I should be able to verify User with the id {string} details")
    public void verifyUserWithTheIdDetails(String userId) {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);

        if (userId.equals("4")) {
            Assert.assertEquals("Id does not match ", jsPath.getInt("data.id"), 4);
            Assert.assertEquals("Email does not match ", jsPath.getString("data.email"), "eve.holt@reqres.in");
            Assert.assertEquals("First Name does not match ", jsPath.get("data.first_name").toString(), "Eve");
            Assert.assertEquals("Last Name does not match ", jsPath.get("data.last_name").toString(), "Holt");
            Assert.assertEquals("Avatar URL does not match ", jsPath.get("data.avatar").toString(), "https://reqres.in/img/faces/4-image.jpg");

            Assert.assertEquals("Support URL does not match ", jsPath.get("support.url").toString(), "https://reqres.in/#support-heading");
            Assert.assertEquals("Support Text does not match ", jsPath.get("support.text").toString(), "To keep ReqRes free, contributions towards server costs are appreciated!");
        } else {
            fail("Unsupported User Id" + userId);
        }
    }

    @Given("I perform GET call to get a list of users on a page {int}")
    public void performGETCallToGetAListOfUsersOnAPage(int pageId) {
        response = given()
                .spec(requestSpec)
                .pathParam("number", pageId)
                .when()
                .get(Endpoints.GET_LIST_OF_USERS)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .log().all()
                .extract().response();
    }

    @Then("I should be able to verify User details on a page {int}")
    public void verifyUserDetailsOnAPage(int pageId) {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);

        switch (pageId) {
            case 1:
                Assert.assertEquals("Page number does not match ", jsPath.getInt("page"), 1);
                Assert.assertEquals("Per Page number does not match ", jsPath.getInt("per_page"), 6);
                Assert.assertEquals("Total number does not match ", jsPath.getInt("total"), 12);
                Assert.assertEquals("Total Pages number does not match ", jsPath.getInt("total_pages"), 2);

                //verifying first ([0]) user details
                Assert.assertEquals("Id does not match ", jsPath.getInt("data.id[0]"), 1);
                Assert.assertEquals("Email does not match ", jsPath.getString("data.email[0]"), "george.bluth@reqres.in");
                Assert.assertEquals("First Name does not match ", jsPath.get("data.first_name[0]").toString(), "George");
                Assert.assertEquals("Last Name does not match ", jsPath.get("data.last_name[0]").toString(), "Bluth");
                Assert.assertEquals("Avatar URL does not match ", jsPath.get("data.avatar[0]").toString(), "https://reqres.in/img/faces/1-image.jpg");

                //verifying second ([1]) user details
                Assert.assertEquals("Id does not match ", jsPath.getInt("data.id[1]"), 2);
                Assert.assertEquals("Email does not match ", jsPath.getString("data.email[1]"), "janet.weaver@reqres.in");
                Assert.assertEquals("First Name does not match ", jsPath.get("data.first_name[1]").toString(), "Janet");
                Assert.assertEquals("Last Name does not match ", jsPath.get("data.last_name[1]").toString(), "Weaver");
                Assert.assertEquals("Avatar URL does not match ", jsPath.get("data.avatar[1]").toString(), "https://reqres.in/img/faces/2-image.jpg");


                Assert.assertEquals("Support Text does not match ", jsPath.get("support.text").toString(), "To keep ReqRes free, contributions towards server costs are appreciated!");
                break;
            default:
                fail("Unsupported page Id " + pageId);
        }
    }


    @Given("I perform POST call to create a user")
    public void performPOSTCallToCreateAUser() {

        JsonObject postUserCreation = new JsonObject();
        postUserCreation.addProperty("name", "James");
        postUserCreation.addProperty("job", "QA");


        response = given()
                .contentType("application/json")
                .spec(requestSpec)
                .body(postUserCreation.toString())
                .when()
                .post(Endpoints.POST_A_USER)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .body("", hasKey("id"))
                .body("", hasKey("createdAt"))
                .log().all()
                .extract().response();
    }

    @And("I should be able to verify a newly created user details")
    public void verifyANewlyCreatedUserDetails() {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);

        Assert.assertEquals("Name does not match ", jsPath.getString("name"), "James");
        Assert.assertEquals("Job does not match ", jsPath.getString("job"), "QA");
        id = jsPath.getInt("id");
    }

    @Then("I should see a empty response")
    public void verifyEmptyResponse() {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);
        Assert.assertNull("Response is not empty ", jsPath.get("{}"));
    }

    @Given("I perform PUT call to update user details for the user")
    public void performPUTCallToUpdateUserDetailsForTheUserWithId() {

        JsonObject putUserUpdate = new JsonObject();
        putUserUpdate.addProperty("name", "NotJames");
        putUserUpdate.addProperty("job", "Developer");


        response = given()
                .contentType("application/json")
                .spec(requestSpec)
                .pathParam("user", id)
                .body(putUserUpdate.toString())
                .when()
                .put(Endpoints.GET_OR_UPDATE_OR_DELETE_A_SINGLE_USER)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .body("", hasKey("updatedAt"))
                .log().all()
                .extract().response();
    }

    @And("I should be able to verify a updated user details")
    public void verifyAUpdatedUserDetailsWithId() {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);
        Assert.assertEquals("Name does not match ", jsPath.getString("name"), "NotJames");
        Assert.assertEquals("Job does not match ", jsPath.getString("job"), "Developer");

    }

    @And("I perform DELETE call to delete the user")
    public void performDELETECallToDeleteTheUser() {
        response = given()
                .contentType("application/json")
                .spec(requestSpec)
                .pathParam("user", id)
                .when()
                .delete(Endpoints.GET_OR_UPDATE_OR_DELETE_A_SINGLE_USER)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .log().all()
                .extract().response();
    }

    @Given("I perform POST call to login with username {string} and password {string}")
    public void performPOSTCallToLoginWithUsernameAndPassword(String username, String password) {
        JsonObject loginUser = new JsonObject();
        loginUser.addProperty("email", username);
        loginUser.addProperty("password", password);


        response = given()
                .contentType("application/json")
                .spec(requestSpec)
                .body(loginUser.toString())
                .when()
                .post(Endpoints.LOGIN)
                .then()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .spec(responseSpec)
                .log().all()
                .extract().response();
    }

    @Then("I should be able to see the static login Token")
    public void verifyTheLoginToken() {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);
        Assert.assertEquals("Token does not match ", jsPath.getString("token"), "QpwL5tke4Pnpja7X4");
    }

    @And("A error message {string}")
    public void verifyErrorMessage(String errorMessage) {
        JsonPath jsPath = RestAssuredUtilities.getJsonPath(response);
        Assert.assertEquals("Error message does not match ", jsPath.getString("error"), errorMessage);
    }
}
