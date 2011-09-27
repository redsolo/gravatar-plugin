package org.jenkinsci.plugins.gravatar;

import hudson.model.User;
import hudson.tasks.Mailer;

import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class UserGravatarResolverIntegrationTest extends HudsonTestCase {
    
    private WebClient wc;

    public void setUp() throws Exception {
        super.setUp();
        wc = createWebClient();
        wc.setJavaScriptEnabled(false);
    }
    
    public void testUserWithoutEmailAddressUsesDefaultImage() throws Exception {
        User.get("user-no-email", true);
        assertAllImageLoadSuccessfully(wc.goTo("/people"));
        assertAllImageLoadSuccessfully(wc.goTo("/user/user-no-email"));
    }
    
    public void testNonExistingGravatarUsesDefaultImage() throws Exception {
        User user = User.get("user", true);
        user.addProperty(new Mailer.UserProperty("MyEmailAddress@example.com"));
        
        assertAllImageLoadSuccessfully(wc.goTo("/people"));
        assertAllImageLoadSuccessfully(wc.goTo("/user/user"));

        HtmlElement element = wc.goTo("/user/user").getElementById("main-panel").getElementsByTagName("img").get(0);
        assertThat(element.getAttribute("src"), endsWith("user.png"));
    }

    public void testGravatarIsUsedForUser() throws Exception {
        User user = User.get("user-e", true);
        user.addProperty(new Mailer.UserProperty("eramfelt@gmail.com"));
        
        assertAllImageLoadSuccessfully(wc.goTo("/people"));
        assertAllImageLoadSuccessfully(wc.goTo("/user/user-e"));

        HtmlElement element = wc.goTo("/user/user-e").getElementById("main-panel").getElementsByTagName("img").get(0);
        assertThat(element.getAttribute("src"), startsWith("http://www.gravatar.com"));
    }
}