package com.sofi.moviedb.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiTester {
    private Properties properties ;

    public ApiTester(String filePath) throws IOException {
        properties = new Properties();
        InputStream in = ClassLoader.getSystemResourceAsStream(filePath);
        properties.load(in);
        in.close();
        RestAssured.baseURI = properties.getProperty("baseUri");
        RestAssured.basePath = properties.getProperty("basePath");
    }

    public void testApi(String resourcePath, int statusCode, String name, String value) {
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put(name, value);
        testApi(resourcePath, statusCode, bodyMap);
    }

    public void testApi(String resourcePath, int statusCode, Map<String, Object> bodyMap) {
        testApi(resourcePath, statusCode, null, bodyMap);
    }

    public void testApi(String resourcePath, int statusCode, Map<String, String> pathParams, Map<String, Object> bodyMap) {
        testApi(resourcePath, statusCode, pathParams, bodyMap, null);
    }

    public void testApi(String resourcePath, int statusCode, Map<String, String> pathParams, Map<String, Object> bodyMap, Map<String, String> queryParams) {
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.param("api_key", properties.getProperty("apiKey"));
        if(pathParams != null) {
            for (String key : pathParams.keySet()) {
                httpRequest.pathParam(key, pathParams.get(key));
            }
        }
        if(queryParams != null){
            for (String qkey : queryParams.keySet()) {
                httpRequest.param(qkey, queryParams.get(qkey));
            }
        }
        ValidatableResponse validatableResponse =
                httpRequest.when().
                        get(resourcePath).
                then().
                        assertThat().statusCode(statusCode).
                        and().contentType(ContentType.JSON);
        if(statusCode==200){
        for (String bodyMapKey : bodyMap.keySet()) {
            if (bodyMap.get(bodyMapKey) instanceof Integer) {
                validatableResponse.body(bodyMapKey, equalTo((Integer) (bodyMap.get(bodyMapKey))));
            } else if (bodyMap.get(bodyMapKey) instanceof String) {
                validatableResponse.body(bodyMapKey, equalTo(bodyMap.get(bodyMapKey)));
            } else {
                throw new UnsupportedOperationException("Given Type Not Supported for Value - " + bodyMap.get(bodyMapKey).getClass());
            }
        }
        }
    }

    public String extractGetResponse(String resourcePath, String requestId) {
        Response response =
                given().log().all().
                        param("api_key", properties.getProperty("apiKey")).
                when().
                        get(resourcePath).
                then().
                        assertThat().statusCode(200).
                        and().contentType(ContentType.JSON).
                        and().extract().response();
        String responseString = response.asString();
        JsonPath js = new JsonPath(responseString);
        return js.get(requestId) ;
    }

    public String extractPostResponse(String requestToken,String resourcePath, String sessionId) {
        String sessionRequestBodyString = "{\n" +
                "  \"request_token\": \""+requestToken+"\"" +
                "}";

        Response response =
                given().log().all().
                        queryParam("api_key", properties.getProperty("apiKey")).
                        body(sessionRequestBodyString).
                        contentType(ContentType.JSON).
                when().
                        post(resourcePath).
                then().
                        assertThat().statusCode(200).
                        and().contentType(ContentType.JSON).
                        and().extract().response();
        String responseSessionId = response.asString();
        JsonPath js = new JsonPath(responseSessionId);
        return js.get(sessionId) ;
    }

    public void extractPostResponse(String resourcePath, String sessionId) {
        String addMovieRequest = "{\n" +
                "  \"media_id\": \""+properties.getProperty("mediaId")+"\"" +
                "}";

        Response response =
                given().log().all().
                        queryParam("api_key", properties.getProperty("apiKey")).
                        queryParam("session_id" ,sessionId).
                        pathParam("list_id",properties.getProperty("listId")).
                        body(addMovieRequest).
                        contentType(ContentType.JSON).
                when().
                        post(resourcePath).
                then().
                        and().contentType(ContentType.JSON).
                        and().extract().response();
        JsonPath js = new JsonPath(response.asString());
        System.out.println(js.get("status_code"));
        System.out.println(js.get("status_message")) ;
    }

        public int extractPostResponseSize(String resourcePath, String count) {
        Response getList =
                given().log().all().
                        param("api_key", properties.getProperty("apiKey")).
                        pathParam("list_id",properties.getProperty("listId")).
                when().
                        get(resourcePath).
                then().
                        assertThat().statusCode(200).
                        and().contentType(ContentType.JSON).
                        and().extract().response();
        String getListResponseString = getList.asString();
        JsonPath js = new JsonPath(getListResponseString);
        return js.get(count) ;
    }

}
