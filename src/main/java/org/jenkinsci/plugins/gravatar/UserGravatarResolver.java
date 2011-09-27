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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.bripkens.gravatar.Gravatar;

import hudson.Extension;
import hudson.model.User;
import hudson.tasks.UserAvatarResolver;
import hudson.tasks.Mailer;
import hudson.tasks.Mailer.UserProperty;

/**
 * UserAvatarResolver that returns gravatar image urls for Jenkins users.
 */
@Extension
public class UserGravatarResolver extends UserAvatarResolver {
    
    private GravatarImageURLVerifier gravatarImageURLVerifier;
    private Map<String, String> cachedGravatarImageURLs;

    public UserGravatarResolver() {
        this(new GravatarImageURLVerifier());
    }
    
    public UserGravatarResolver(GravatarImageURLVerifier urlVerifier) {
        gravatarImageURLVerifier = urlVerifier;
        cachedGravatarImageURLs = new HashMap<String, String>();
    }

    @Override
    public String findAvatarFor(User user, int width, int height) {
        UserProperty mailProperty = user.getProperty(Mailer.UserProperty.class);
        if (mailProperty != null) {
            String email = mailProperty.getAddress();
            if (email != null) {
                return getGravatarUrlFor(email, width, height);
            }
        }
        return null;
    }

    String getGravatarUrlFor(String email, int width, int height) {
        if (cachedGravatarImageURLs.containsKey(email)) {
            return cachedGravatarImageURLs.get(email);
        }
        String gravatarImageURL = new Gravatar()
            .setSize(Math.max(width,  height))
            .getUrl(email);
        if (gravatarImageURLVerifier.verify(email)) {
            cachedGravatarImageURLs.put(email, gravatarImageURL);
            return gravatarImageURL;
        }
        return null;
    }
    
    private static final Logger LOGGER = Logger.getLogger(UserGravatarResolver.class.getName());
}
