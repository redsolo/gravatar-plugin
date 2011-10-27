/*
 * The MIT License
 * 
 * Copyright (c) 2011, Erik Ramfelt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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