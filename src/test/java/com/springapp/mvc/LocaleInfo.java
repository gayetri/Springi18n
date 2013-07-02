package com.springapp.mvc;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: gbhattac
 * Date: 6/27/13
 * Time: 10:28 AM
 */
public class LocaleInfo {

//----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Static Attributes---------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    private static final Logger LOGGER = Logger.getLogger(LocaleInfo.class);

    //----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Instance Attributes-------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
    private String country;
    private String language;
    private Properties resourceBundle;

//----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Public Methods -----------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------


    public LocaleInfo(final String language, final String country) {
        this.country = country;
        this.language = language;
        resourceBundle = loadResourceBundle();
    }

    public Properties getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(final Properties resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    //----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Private Methods ----------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
    private Properties loadResourceBundle() {
        final Properties prop = new Properties();
        InputStream in = null;
        try {
            //in = new FileInputStream(new File("C:/Work/projects/New/src/main/resources" + "/welcome_" +
            //      getLanguage() + "_" + getCountry() + ".properties"));

            in = this.getClass().getClassLoader()
                    .getResourceAsStream("welcome_" +
                            getLanguage() + "_" + getCountry() + ".properties");
            prop.load(in);
        }
        catch (IOException ie) {
            LOGGER.error(ie);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ie) {
                // Silence
                LOGGER.warn(ie);
            }
        }
        return prop;
    }
}
