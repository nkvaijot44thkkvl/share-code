package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.service.HouseService;

//アノテーションをつけてコントローラの定義
@Controller
public class HomeController {
    private final HouseService houseService;

    public HomeController(HouseService houseService) {
        this.houseService = houseService;
    } 
	//HttpリクエストのGetメソッドを対応付け(引数:対応付けするルートパスドメイン省略名)
	@GetMapping("/")
	 public String index(Model model) {
		 List<House> newHouses = houseService.findTop8HousesByOrderByCreatedAtDesc();
	        List<House> popularHouses = houseService.findTop3HousesByOrderByReservationCountDesc();
	        model.addAttribute("newHouses", newHouses);
	        model.addAttribute("popularHouses", popularHouses); 
		//呼び出したいビューをリターンで返す
        return "index";
    }
}

/*@GetMapping("/")…アプリのトップページにGetメソッドで訪問されたときに
 * 当該メソッド(今回はindex)が実行される*/