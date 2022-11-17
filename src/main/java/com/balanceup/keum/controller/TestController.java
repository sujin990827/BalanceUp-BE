package com.balanceup.keum.controller;

import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController {

	@GetMapping({"", "/"})
	public String index() {
		return "index";
	}

	@GetMapping({"/loginSuccess"})
	public String login(@RequestParam(name = "token") String token, Model model) {
		model.addAttribute("token", token);
		return "loginSuccess";
	}
	@GetMapping({"/user/home"})
	public String home() {
		return "userhome";
	}



}
