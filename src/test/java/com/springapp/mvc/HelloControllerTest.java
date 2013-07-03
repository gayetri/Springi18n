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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

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

//----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Static Attributes---------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    private static final String LANGUAGE_KEY = "lang";
    private static final String COUNTRY_KEY = "country";
    private static final String KNOWN_MESSAGE_KEY_1 = "welcome.springmvc";
    private static final String KNOWN_MESSAGE_KEY_2 = "start.springmvc";
    private static final String KNOWN_MODEL_ATTR_1 = "message1";
    private static final String KNOWN_MODEL_ATTR_2 = "message2";
    private static final String KNOWN_I18N_MESSAGE = "Happy learning Spring MVC!";
    //----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Instance Attributes---------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    protected WebApplicationContext wac;
    private MockMvc mockMvc;
    private final List<LocaleInfo> localInfoList = new ArrayList<LocaleInfo>();

//----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Private Methods-----------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    /**
     * Setup data for testing.
     */
    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        setUpLocalesForTest();
    }

    /**
     * Test: The case when a "known" message key is set as a part of a "known" model attribute.
     *
     * @throws Exception
     */
    @Test
    public void testWelcomePageModelAttrsForDefaultLocale() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                .andReturn();
        final Locale actualLocaleSet = mvcResult.getResponse().getLocale();
        // Using java default locale didnt work because the mock was sending just the language, not the country ???
        final Locale expectedLocale = new Locale("en");
        assertEquals(expectedLocale, actualLocaleSet);
        final String actualModelAttr = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_1));
        assertEquals(KNOWN_I18N_MESSAGE, wac.getMessage(actualModelAttr, null, actualLocaleSet));
    }

    /**
     * Test: The case when a locale is sent with the request and the same is expected to be set.
     *
     * @throws Exception
     */
    @Test
    public void testWelcomePageForLocaleFromRequest() throws Exception {

        for (final LocaleInfo localeInfoObj : localInfoList) {
            final String language = localeInfoObj.getLanguage();
            final String country = localeInfoObj.getCountry();
            final MvcResult mvcResult = mockMvc.perform(get("/?lang=" + language + "_" + country))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                    .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2))
                    .andReturn();
            final Locale actualLocaleSet = mvcResult.getResponse().getLocale();
            final Locale expectedLocale = new Locale(language, country);
            assertEquals(expectedLocale, actualLocaleSet);

            final String actualModelAttr1 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_1));
            final String actualModelAttr2 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_2));
            final String message1 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_1));
            final String message2 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_2));
            assertEquals(message1, wac.getMessage(actualModelAttr1, null, actualLocaleSet));
            assertEquals(message2, wac.getMessage(actualModelAttr2, null, actualLocaleSet));
        }
    }

    /**
     * Test: The case when a locale is sent with the request and the same is expected to be set in cookies
     * and to be cleared when the next request does not have a request parameter for locale.
     *
     * @throws Exception
     */

    @Test
    public void testWelcomePageModelAttrsForLocaleFromRequestToCookie() throws Exception {
        //1. Obtain cookies
        final String language = "zh";
        final String country = "CN";
        final LocaleInfo localeInfoObj = new LocaleInfo(language, country);
        MvcResult mvcResult = mockMvc.perform(get("/?lang=" + language + "_" + country))
                .andExpect(status().isOk())
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2)).andReturn();
        Cookie[] cookies = mvcResult.getResponse().getCookies();
        assertEquals(CookieLocaleResolver.DEFAULT_COOKIE_NAME, cookies[0].getName());
        assertEquals(language + "_" + country, cookies[0].getValue());
        assertEquals("/", cookies[0].getPath());
        Locale actualLocaleSet = mvcResult.getResponse().getLocale();
        Locale expectedLocale = new Locale(language, country);
        assertEquals(expectedLocale, actualLocaleSet);

        //2. Reuse the cookies
        final MockHttpServletRequestBuilder newHttpRequest = MockMvcRequestBuilders.get("/");
        newHttpRequest.cookie(cookies[0]);
        mvcResult = mockMvc.perform(newHttpRequest)
                .andExpect(status().isOk())
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2))
                .andReturn();
        cookies = mvcResult.getResponse().getCookies();
        assertEquals(0, cookies.length);

        //3. Check locales set
        actualLocaleSet = mvcResult.getResponse().getLocale();
        expectedLocale = new Locale(language, country);
        assertEquals(expectedLocale, actualLocaleSet);
        final String actualModelAttr1 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_1));
        final String actualModelAttr2 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_2));
        final String message1 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_1));
        final String message2 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_2));

        assertEquals(message1, wac.getMessage(actualModelAttr1, null, new Locale(language, country)));
        assertEquals(message2, wac.getMessage(actualModelAttr2, null, new Locale(language, country)));
    }

    /**
     * Test: The case when a locale is sent with the request and the same is expected to be *set* in cookies
     * and to be *reset* when the next request does have a request parameter for locale.
     *
     * @throws Exception
     */
    @Test
    public void testWelcomePageModelAttrsForLocaleFromRequestToCookieAndOverwritingThat() throws Exception {

        //1. Obtain cookies
        LocaleInfo localeInfoObj = new LocaleInfo("zh", "CN");
        MvcResult mvcResult = mockMvc.perform(get("/?lang=" + localeInfoObj.getLanguage() + "_" +
                localeInfoObj.getCountry()))
                .andExpect(status().isOk())
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2)).andReturn();
        Cookie[] cookies = mvcResult.getResponse().getCookies();
        assertEquals(CookieLocaleResolver.DEFAULT_COOKIE_NAME, cookies[0].getName());
        assertEquals(localeInfoObj.getLanguage() + "_" + localeInfoObj.getCountry(), cookies[0].getValue());
        assertEquals("/", cookies[0].getPath());
        Locale actualLocaleSet = mvcResult.getResponse().getLocale();
        Locale expectedLocale = new Locale(localeInfoObj.getLanguage(), localeInfoObj.getCountry());
        assertEquals(expectedLocale, actualLocaleSet);

        //2. Reuse the cookies and also override them
        localeInfoObj = new LocaleInfo("en", "US");
        final MockHttpServletRequestBuilder newHttpRequest =
                MockMvcRequestBuilders.get("/?lang=" + localeInfoObj.getLanguage() + "_" + localeInfoObj.getCountry());
        newHttpRequest.cookie(cookies[0]);
        mvcResult = mockMvc.perform(newHttpRequest)
                .andExpect(status().isOk())
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2)).andReturn();
        cookies = mvcResult.getResponse().getCookies();
        assertEquals(CookieLocaleResolver.DEFAULT_COOKIE_NAME, cookies[0].getName());
        assertEquals(localeInfoObj.getLanguage() + "_" + localeInfoObj.getCountry(), cookies[0].getValue());
        assertEquals("/", cookies[0].getPath());

        //3. Check locales set
        actualLocaleSet = mvcResult.getResponse().getLocale();
        expectedLocale = new Locale(localeInfoObj.getLanguage(), localeInfoObj.getCountry());
        assertEquals(expectedLocale, actualLocaleSet);
        final String actualModelAttr1 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_1));
        final String actualModelAttr2 = ((String) mvcResult.getModelAndView().getModel().get(KNOWN_MODEL_ATTR_2));
        final String message1 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_1));
        final String message2 = ((String) localeInfoObj.getResourceBundle().get(KNOWN_MESSAGE_KEY_2));

        assertEquals(message1, wac.getMessage(actualModelAttr1, null,
                new Locale(localeInfoObj.getLanguage(), localeInfoObj.getCountry())));
        assertEquals(message2, wac.getMessage(actualModelAttr2, null,
                new Locale(localeInfoObj.getLanguage(), localeInfoObj.getCountry())));
    }

    /**
     * Test: The case when a locale is not sent with the request and the same is not expected to be *set* in cookies.
     *
     * @throws Exception
     */
    @Test
    public void testWelcomePageModelAttrsForDefaultLocaleNotSetInCookie() throws Exception {

            final MvcResult mvcResult = mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(KNOWN_MODEL_ATTR_1, KNOWN_MESSAGE_KEY_1))
                    .andExpect(model().attribute(KNOWN_MODEL_ATTR_2, KNOWN_MESSAGE_KEY_2)).andReturn();
            final Cookie[] cookies = mvcResult.getResponse().getCookies();
            assertEquals(0, cookies.length);

    }

//----------------------------------------------------------------------------------------------------------------------
//--------------------------------------------Private Methods-----------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    private void setUpLocalesForTest() {
        final List<Map<String, String>> listOfLocales = getLocales();

        for (final Map<String, String> localeListItem : listOfLocales) {
            final LocaleInfo localeInfo = new LocaleInfo(localeListItem.get(LANGUAGE_KEY),
                    localeListItem.get(COUNTRY_KEY));
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
}
