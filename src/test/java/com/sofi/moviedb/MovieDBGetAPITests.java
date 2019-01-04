package com.sofi.moviedb;
/*
Web API Technical Assessment
        The Movie DB project (www.themoviedb.org) is a free community built database of movie and TV meta data.
        TMDB also provides a free API web service that provides movie buffs with a wealth of information and details regarding their favorite movies
        and allows the information to be integrated with both commercial and open source media related applications.
        One of my personal favorites web applications and large consumer of TDMB data is the Plex Media Server https://www.plex.tv/

        The APIs are all REST and return JSON data.
        Visit https://developers.themoviedb.org/3/getting- started for detailed documentation regarding the API.
        Your assignment should you choose to accept it is to pretend as though you were writing automated test cases on a new release of the TMDB API.

        The requirements for the assessment are as follows:
        1. Use Java (preferred) or Python and pick whatever test framework you’re most familiar
        with. (Code generators or tools like Postman don’t count)
        2. Check your code into a free account on GitHub or Bitbucket and send us the link to the
        repo.
        3. You’ll need to create a free developer API key that is required to use the API.
        4. Don’t worry about sending us the key or checking it into a repo but instead
        parameterize the key so I can plug in my own key.
        5. Identify at least a dozen tests you would write. Feel free to pick any endpoint.
        6. Stub out all those tests with functions and using comments in the code write TODOs for
        the name of the tests and 1-2 sentences describing what the test will do and why
        7. Implement 3-4 of the tests with actual runnable code
        8. Tests should be runnable right out of the box. (i.e. I should be able clone your repo and
        run the tests)
        9. Don’t forget to include instructions on how to execute the tests!
        10. Hard-coding of configuration or environment will get marks against you
        11. Tests should use good OOP principles and have some abstraction in the testing layout so
        that they could be a base/template for implementing many automated API tests against
        the movie db API web service.
        12. Tests should create libraries/helper classes and base classes to increase maintainability
        and readability
        13. Bonus points are given for non-happy path test scenarios and more creative approaches

        14. ** Design and layout counts: layout the test code so that the tests are abstracted (for maintainability and readability).
        Pay attention to what libraries/helper classes and base classes would he abstracted to achieve this goal.
*/

import com.sofi.moviedb.support.ApiTester;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

public class MovieDBGetAPITests {

    private ApiTester apiTester;

    @BeforeTest
    public void setup() throws IOException {
        apiTester = new ApiTester("env.properties");
    }

    //Get Movie List : GET /genre/movie/list , Get the list of official genres for movies.
    @Test
    public void  testMovieList() {
        apiTester.testApi("/genre/movie/list", 200, "genres[0].name", "Action");
    }

    //Get TV List : GET /genre/tv/list , Get the list of official genres for TV.
    @Test
    public void  testTvList() {
        apiTester.testApi("/genre/tv/list", 200, "genres[10].name", "Reality");
    }

    //GET /movie/popular
   // Get a list of the current popular movies on TMDb. This list updates daily.
    @Test
    public void testPopularMovie() {
        apiTester.testApi("/movie/popular", 200, "results[0].title", "Aquaman");
    }

    //GET  tv/{tv_id}/videos
    //Get the videos that have been added to a TV show.
    @DataProvider(name="tvVideos")
    public Object[][] createTestDataTvVideos() {
        return new Object[][] {
                {"2190-south-park","New South Park opening 2013"},
                {"1434-family-guy","Family Guy - Trailer"},
                {"1402-the-walking-dead","The Walking Dead Trailer"}
        };
    }
    @Test(dataProvider="tvVideos")
    public void testTvVideos(String tv_id , String videoName) {
        Map<String, String> hmap = new HashMap<String, String>();
        hmap.put("tv_id",tv_id);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("results[0].name",videoName);
        apiTester.testApi("/tv/{tv_id}/videos" , 200,hmap,bodyMap);
    }

    //Data For to Get the TV season details by id.
    ///tv/{tv_id}/season/{season_number}
    @DataProvider(name="seasons")
    public Object[][] createTestDataSeasons() {
        return new Object[][] {
                {"1423-ray-donovan","6","12"},
                {"12609-dragon-ball","1","153"},
                {"49464-inu-to-hasami-wa-tsukaiyou","1","12"}
        };
    }
    @Test(dataProvider="seasons")
    public void testTvSeasons(String tv_id , String season ,String noOfEpisodes) {
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("tv_id",tv_id);
        hMap.put("season_number",season);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("season_number",Integer.parseInt(season));
        bodyMap.put("episodes.size()",Integer.parseInt(noOfEpisodes));
        apiTester.testApi("/tv/{tv_id}/season/{season_number}" , 200,hMap,bodyMap);
    }

    //Data to Get the TV episode details by id.
    //GET /tv/{tv_id}/season/{season_number}/episode/{episode_number}
    @DataProvider(name="seasonsAndEpisodes")
    public Object[][] createTestDataSeasonsAndEpisodes() {
        return new Object[][] {
                {"44217-vikings","5" ,"2" ,"The Departed Part Two"},
                {"42009-black-mirror","4","6","Black Museum"},
                {"1418-the-big-bang-theory","12","4","The Tam Turbulence"}
        };
    }
    @Test(dataProvider="seasonsAndEpisodes")
    public void testTvSeasonAndEpisode(String tv_id , String season , String episode, String episodeName) {
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("tv_id",tv_id);
        hMap.put("season_number",season);
        hMap.put("episode_number",episode);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("season_number",Integer.parseInt(season));
        bodyMap.put("episode_number",Integer.parseInt(episode));
        bodyMap.put("name",episodeName);
        apiTester.testApi("/tv/{tv_id}/season/{season_number}/episode/{episode_number}" , 200,hMap,bodyMap);
    }


    //Data to Get Person Details using id
    //GET /person/{person_id}
    @DataProvider(name="people")
    public Object[][] createTestDataForPeople() {
        return new Object[][] {
                {"72129","Jennifer Lawrence"},
                {"85","Johnny Depp"},
                {"192","Morgan Freeman"}
        };
    }
    @Test(dataProvider="people")
    public void testPeople(String personId, String personName) {
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("person_id",personId);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("id",Integer.parseInt(personId));
        bodyMap.put("name",personName);
        apiTester.testApi("/person/{person_id}" , 200,hMap,bodyMap);
    }


    //Reviews : This test method gets the reviews for a particular movie.
    //Response is validated by comparing ID of the review and Name of the author/reviewer
    //GET /review/{review_id}
    @DataProvider(name="movieReviews")
    public Object[][] createTestDataForMovieReviews() {
        return new Object[][] {
                {"5bd9bd9e0e0a26508600f398","Gimly"},
                {"514334c319c29576d21bb565","Andres Gomez"},
                {"5437ac33c3a3686ede001c86","Andres Gomez"}
        };
    }
    @Test(dataProvider="movieReviews")
    public void  testReviews(String review_id , String reviewer) {
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("review_id",review_id);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("id",review_id);
        bodyMap.put("author",reviewer);
        apiTester.testApi("/review/{review_id}" , 200,hMap,bodyMap);
    }

    //Get TV Certifications
    //GET /certification/tv/list
    @Test
    public void  testTvCertificates() {
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("certifications.size()",18);
        apiTester.testApi("/certification/tv/list", 200,bodyMap );
    }

    //Get Movie Certifications
    //GET /certification/movie/list
    //Endpoint https://api.themoviedb.org/3/certification/movie/list?api_key=<<api_key>>
    @Test
    public void  testMovieCertificates() {
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("certifications.size()",24);
        apiTester.testApi("/certification/movie/list", 200,bodyMap );
    }

    //Get External IDs
    // GET /person/{person_id}/external_ids
    @DataProvider(name="peopleExternalIds")
    public Object[][] createTestDataForPeopleExternalIds() {
        return new Object[][] {
                {"18918","therock","DwayneJohnson","therock"},
                {"14386","beyonce","beyonce","beyonce"},
                {"500","tomcruise","officialtomcruise","tomcruise"}
        };
    }
    @Test(dataProvider="peopleExternalIds")
    public void  testExternalIdForPerson(String personId , String twitterId , String facebookId , String instagramId) {
        Map<String, String> hMap = new HashMap<String, String>();
        hMap.put("person_id",personId);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("id",Integer.parseInt(personId));
        bodyMap.put("twitter_id",twitterId);
        bodyMap.put("facebook_id",facebookId);
        bodyMap.put("instagram_id",instagramId);
        apiTester.testApi("/person/{person_id}/external_ids" , 200,hMap,bodyMap);
    }


    //GET /discover/movie
    //Following API call returns Actors's highest grossing rated 'R' movies
    @DataProvider(name="discoverMovieData")
    public Object[][] createDiscoverMovieData() {
        return new Object[][] {
                {"US","R","revenue.desc","3896","Schindler's List","The Grey"},
                {"US","PG13","revenue.desc","500","Mission: Impossible - Fallout","The Last Samurai"},
                {"US","PG","revenue.desc","1245","The Jungle Book","Marvel: 75 Years, From Pulp to Pop!"}
        };
    }
    @Test(dataProvider="discoverMovieData")
    public void testDiscoverMovie(String certification_country , String certification , String revenue , String actor , String movie0 , String movie6 ) {
        Map<String, String> qMap = new HashMap<String, String>();
        qMap.put("certification_country",certification_country);
        qMap.put("certification",certification);
        qMap.put("sort_by",revenue);
        qMap.put("with_cast",actor);
        Map<String, Object> bodyMap = new HashMap<String, Object>();
        bodyMap.put("results[0].title",movie0);
        bodyMap.put("results[6].title",movie6);
        apiTester.testApi("/discover/movie" , 200,null,bodyMap,qMap);
    }


}

