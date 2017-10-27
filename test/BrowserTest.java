import org.junit.Test;
import play.Application;
import play.Logger;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class BrowserTest extends WithBrowser {

    protected Application provideApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    protected TestBrowser provideBrowser(int port) {
        return Helpers.testBrowser(port);
    }

    /**
     * add your integration test here
     * in this example we just check if the welcome page is being shown
     */
    @Test
    public void test() {
        browser.goTo("http://localhost:" + play.api.test.Helpers.testServerPort());
        Logger.debug(browser.pageSource());
        assertTrue(browser.pageSource().contains("TestController"));
    }

}
