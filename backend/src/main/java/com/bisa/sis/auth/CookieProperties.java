package com.bisa.sis.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {

    /** False in local dev (plain HTTP), true in any deployed environment. */
    private boolean secure = true;

    /** "Lax" when frontend and backend share a domain, "None" when they don't (requires secure=true). */
    private String sameSite = "Lax";

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getSameSite() {
        return sameSite;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }
}
