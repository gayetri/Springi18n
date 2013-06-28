package com.springapp.mvc;

import java.util.Properties;

/**
 * User: gbhattac
 * Date: 6/27/13
 * Time: 10:28 AM
 */
public class LocaleInfo {

    private String country;
    private String language;
    private Properties resourceBundle;

    public LocaleInfo(String country, String language) {
        this.country = country;
        this.language = language;
    }

    public Properties getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(Properties resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}
