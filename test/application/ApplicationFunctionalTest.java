package application;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationFunctionalTest extends WithApplication {
    @Test
    public void testJavascriptRoutes() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/assets/javascripts/jsroutes.js");

        Result result = Helpers.route(app, request);

        assertThat(result.status()).isEqualTo(Http.Status.OK);
        assertThat(result.contentType()).isNotNull();
        assertThat(result.contentType().get()).isEqualToIgnoringCase("text/javascript");
    }

    @Test
    public void testMessages() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method("GET")
                .uri("/assets/javascripts/messages.js");

        Result result = Helpers.route(app, request);

        assertThat(result.status()).isEqualTo(Http.Status.OK);
        assertThat(result.contentType()).isNotNull();
        assertThat(result.contentType().get()).isEqualToIgnoringCase("text/javascript");
        assertThat(Helpers.contentAsString(result)).contains("application.name");
    }
}
