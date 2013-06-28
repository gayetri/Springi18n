package com.springapp.mvc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")

public class HelloControllerTest {

    private static String LANGUAGE_KEY = "lang";
    private static String COUNTRY_KEY = "country";
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    protected WebApplicationContext wac;
    private MockMvc mockMvc;
    private List<LocaleInfo> localInfoList = new ArrayList<LocaleInfo>();

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        setUpLocalesForTest();
    }

    private void setUpLocalesForTest() {
        final List<Map<String, String>> listOfLocales = getLocales();

        for (final Map<String, String> localeListItem : listOfLocales) {
            final LocaleInfo localeInfo = new LocaleInfo(localeListItem.get(COUNTRY_KEY), localeListItem.get(LANGUAGE_KEY));
            localInfoList.add(localeInfo);
        }
    }

    private List<Map<String, String>> getLocales() {
        final List<Map<String, String>> listOfLocales = new ArrayList<Map<String, String>>();
        putItems(listOfLocales, "en", "US");
        putItems(listOfLocales, "zh", "CN");
        return listOfLocales;
    }

    private void putItems(final List<Map<String, String>> listOfLocales, final String language, final String country) {

        final Map<String, String> item = new HashMap<String, String>();
        item.put(LANGUAGE_KEY, language);
        item.put(COUNTRY_KEY, country);
        listOfLocales.add(item);
    }

    @Test
    public void testWelcomePageModelAttrsForDefaultLocale() throws Exception {

        for (final LocaleInfo localeInfoObj : localInfoList) {
            final MvcResult mvcResult = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("message", "welcome.springmvc"))
                    .andReturn();
            final Locale actualLocaleSet = mvcResult.getResponse().getLocale();
            assertEquals("Happy learning Spring MVC", wac.getMessage("welcome.springmvc", null, actualLocaleSet));
        }
    }

    @Test
    public void testWelcomePageModelAttrsForLocaleFromRequest() throws Exception {

        for (final LocaleInfo localeInfoObj : localInfoList) {
            final String language = localeInfoObj.getLanguage();
            final MvcResult mvcResult = mockMvc.perform(get("/?lang=" + language))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("message", "welcome.springmvc"))
                    .andReturn();
            final Locale actualLocaleSet = mvcResult.getResponse().getLocale();
            final Locale expectedLocale = new Locale(language);
            System.out.println("Locale is " + actualLocaleSet.getLanguage());
            assertEquals(expectedLocale, actualLocaleSet);
            assertEquals("Happy learning Spring MVC", wac.getMessage("welcome.springmvc", null, expectedLocale));
        }
    }

    @Test
    public void testWelcomePageModelAttrsForLocaleFromRequestToCookie() throws Exception {

        for (final LocaleInfo localeInfoObj : localInfoList) {
            final String language = localeInfoObj.getLanguage();
            final MvcResult mvcResult = mockMvc.perform(get("/?lang=" + language))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("message", "welcome.springmvc"))
                    .andReturn();
            final Cookie[] cookies = mvcResult.getResponse().getCookies();
            assertEquals("org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE", cookies[0].getName());
            assertEquals(language, cookies[0].getValue());
            assertEquals("/", cookies[0].getPath());
        }
    }

    @Test
    public void testWelcomePageModelAttrsForLocaleFromCookie() throws Exception {

        for (final LocaleInfo localeInfoObj : localInfoList) {
            final String language = localeInfoObj.getLanguage();
            final MvcResult mvcResult = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("message", "welcome.springmvc"))
                    .andReturn();
            final Cookie[] cookies = mvcResult.getResponse().getCookies();
            assertEquals(0, cookies.length);
        }
    }
}
