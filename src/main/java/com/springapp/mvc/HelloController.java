package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller

public class HelloController {
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        model.addAttribute("message1", "welcome.springmvc");
        model.addAttribute("message2", "start.springmvc");
        return "hello";
    }
}