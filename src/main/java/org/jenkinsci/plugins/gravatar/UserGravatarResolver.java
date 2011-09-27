package org.jenkinsci.plugins.gravatar;

import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;

import hudson.Extension;
import hudson.model.User;
import hudson.tasks.UserAvatarResolver;
import hudson.tasks.Mailer;
import hudson.tasks.Mailer.UserProperty;

@Extension
public class UserGravatarResolver extends UserAvatarResolver {
    @Override
    public String findAvatarFor(User user, int width, int height) {
        UserProperty mailProperty = user.getProperty(Mailer.UserProperty.class);
        if (mailProperty != null) {
            String email = mailProperty.getAddress();
            if (email != null) {
                String gravatarImageURL = new Gravatar()
                    .setSize(Math.max(width,  height))
                    .setHttps(true)
                    .setStandardDefaultImage(DefaultImage.HTTP_404)
                    .getUrl(email);
                return gravatarImageURL;
            }
        }
        return null;
    }
}
