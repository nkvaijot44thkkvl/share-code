package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	//HttpリクエストのGetメソッドを対応付け(引数:対応付けするルートパスドメイン省略名)
		@GetMapping("/")
		 public String index() {			
			//呼び出したいビューをリターンで返す
	        return "index";
	    }
}
