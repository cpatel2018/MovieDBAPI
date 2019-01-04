package com.sofi.moviedb;

import com.sofi.AuthenticateUser;

import com.sofi.moviedb.support.ApiTester;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class MovieDBPostAPITests {
    private ApiTester apiTester;

    @BeforeTest
    public void setup() throws IOException {
        apiTester = new ApiTester("env.properties");
    }

    /* ADD DELETE MOVIE TO EXISTING LIST
    Step 1: Create Request Token. GET /authentication/token/new
    Step 2: Get the user to authorize the request token, https://www.themoviedb.org/authenticate/{REQUEST_TOKEN}
    Step 3: Create Session. POST /authentication/session/new
    Step 4: Get List item count before adding movie GET /list/{list_id}
    Step 5: Add a movie to a list.  POST list/{list_id}/add_item
    Step 6:  Remove a movie from a list. POST /list/{list_id}/remove_item
    Step 7: Get List item count after deleting movie. GET /list/{list_id} */

    @Test
    public void testAddDeleteMovieToExistingList() {
        String requestToken = createRequestToken();
        AuthenticateUser authenticateUser = new AuthenticateUser();
        try {
            authenticateUser.approveUser(requestToken);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        String sessionId = createSession(requestToken);
        int beforeListLength = getList();
        System.out.println("Before List length:"+beforeListLength);
        addMovie(sessionId);
        deleteMovie(sessionId);
        int afterListLength = getList();
        System.out.println("After List length:"+afterListLength);
        Assert.assertEquals(beforeListLength,afterListLength);
    }

    private String createRequestToken(){
        return apiTester.extractGetResponse("/authentication/token/new", "request_token");
    }

    private String createSession(String requestToken){
        return apiTester.extractPostResponse(requestToken,"/authentication/session/new", "session_id");
    }

    private void addMovie(String sessionId){
         apiTester.extractPostResponse("/list/{list_id}/add_item", sessionId);
    }

    private void deleteMovie(String sessionId){
        apiTester.extractPostResponse("/list/{list_id}/remove_item", sessionId);
    }

    private int getList(){
        return apiTester.extractPostResponseSize("/list/{list_id}","item_count");
    }
}
