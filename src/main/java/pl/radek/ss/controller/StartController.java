package pl.radek.ss.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.radek.ss.domain.EventType;

import java.util.ArrayList;
import java.util.Date;

@Controller
public class StartController
{
    @GetMapping(value = "/")
    public String schedule() {
        return "start";
    }
}
