package com.sofi.moviedb;

import com.sofi.AuthenticateUser;
import com.sofi.moviedb.support.ApiTester;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class MovieDBNegativeTests {

    Properties dataProperties =new Properties();
    private ApiTester apiTester;

    @BeforeTest
    public void setup() throws IOException {
        apiTester = new ApiTester("env.properties");
        InputStream in = ClassLoader.getSystemResourceAsStream("invalidData.properties");
        dataProperties.load(in);
    }

    // Get Movie List : Negative TEST : 404 Not Found
    // GET /genre/movie/listt , Get the list of official genres for movies.
    @Test
    public void  testMovieListNegativetest() {
            apiTester.testApi("/genre/movie/listt", 404, "genres[0].name", "Action");
    }

    // Get TV List Negative Test: Verify 401 response when Invalid key is supplied to API
    @Test
    public void  testTvListNegativeTest() {
          Assert.assertEquals(extractResponseMessage("/genre/tv/list"), dataProperties.getProperty("InvalidAPIKEYMsg"));
    }

    /*
    Step 1: Create Request Token
    Step 2: Get the user to authorize the request token
    Step 3: Create Session with invalid request token
    verify  401 error with session denied message.
   */

    @Test
    public void  testSessionCreationWithInvalidRequestToken() {
        String requestToken = createRequestToken();
        AuthenticateUser authenticateUser = new AuthenticateUser();
        try {
            authenticateUser.approveUser(requestToken);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        createInvalidSession(requestToken);
    }

    private String createRequestToken(){
        return apiTester.extractGetResponse("/authentication/token/new", "request_token");
    }

    private void createInvalidSession(String requestToken){
        String invalidRequestToken = requestToken+"$";
        System.out.println("Bad Request Token:"+requestToken+"$");
        String sessionRequestBodyString = "{\n" +
                "  \"request_token\": \""+invalidRequestToken+"\"" +
                "}";
        Assert.assertEquals(extractResponseMessage("/authentication/session/new",sessionRequestBodyString), dataProperties.getProperty("InvalidSessionMsg"));
    }

    public String extractResponseMessage(String resourcePath){
        RestAssured.baseURI = dataProperties.getProperty("host");
        Response response =
                given().log().all().
                        param("api_key", dataProperties.getProperty("InvalidAPIKEY")).
                when().
                        get(resourcePath).
                then().
                        assertThat().statusCode(401).
                        and().contentType(ContentType.JSON).
                        and().extract().response();

        String responseString = response.asString();
        JsonPath js = new JsonPath(responseString);
        String actualMsg = js.get("status_message");
        return actualMsg;
    }

    public String extractResponseMessage(String resourcePath, String invalidSessionBody){
        Response response =
                given().log().all().
                        queryParam("api_key", dataProperties.getProperty("apiKey")).
                        body(invalidSessionBody).
                        contentType(ContentType.JSON).
                when().
                        post(resourcePath).
                then().
                        assertThat().statusCode(401).
                        and().contentType(ContentType.JSON).
                        and().extract().response();
        JsonPath js = new JsonPath(response.asString());
        String actualMsg = js.get("status_message");
        return actualMsg;
    }
}


