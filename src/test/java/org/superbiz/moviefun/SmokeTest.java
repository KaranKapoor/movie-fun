package org.superbiz.moviefun;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.superbiz.moviefun.bootify.App;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = DEFINED_PORT)
public class SmokeTest {

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();

        String homePage = restTemplate.getForObject(url("/"), String.class);

        assertThat(homePage, containsString("Please select one of the following links:"));

        String setupPage = restTemplate.getForObject(url("/setup"), String.class);

        assertThat(setupPage, containsString("Wedding Crashers"));
        assertThat(setupPage, containsString("Starsky & Hutch"));
        assertThat(setupPage, containsString("Shanghai Knights"));
        assertThat(setupPage, containsString("I-Spy"));
        assertThat(setupPage, containsString("The Royal Tenenbaums"));

        String movieFunPage = restTemplate.getForObject(url("/moviefun"), String.class);

        assertThat(movieFunPage, containsString("Wedding Crashers"));
        assertThat(movieFunPage, containsString("David Dobkin"));
    }

    private String url(String path) {
        String baseUrl = "http://localhost:8080/";
        String envUrl = System.getenv("MOVIE_FUN_URL");

        if (envUrl != null && !envUrl.isEmpty()) {
            baseUrl = envUrl;
        }

        return baseUrl + path;
    }
}
