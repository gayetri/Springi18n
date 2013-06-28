package com.springapp.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * User: gbhattac
 * Date: 6/27/13
 * Time: 4:58 PM
 */
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
class HelloControllerTestSpec extends Specification {

    @Autowired
    WebApplicationContext wac;

    @Shared
    MockMvc mockMvc = webAppContextSetup(this.wac).build();

    def "GET REQUEST execution"() {
        def MvcResult mvcResult
        given:
        null != mockMvc
        when:
        mvcResult = mockMvc.perform(get("/"))
        then:
        HttpServletResponse.SC_OK == mvcResult.getResponse().getStatus()
    }
}