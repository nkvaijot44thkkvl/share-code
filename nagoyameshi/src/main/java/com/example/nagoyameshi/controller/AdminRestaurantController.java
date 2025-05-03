package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	private final RestaurantService restaurantService;
	
	public AdminRestaurantController(RestaurantService restaurantService) {
		this.restaurantService = restaurantService;
	}
	
	@GetMapping
	public String index(@RequestParam(name = "keyword",required = false) String keyword,
			@PageableDefault(page = 0,size=15,sort="id",direction=Direction.ASC) Pageable pageable,
			Model model) {	
		
		Page<Restaurant> restaurantPage;
		
		if(keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantService.findRestaurantsByNameLike(keyword,pageable);
		}else {
			restaurantPage = restaurantService.findAllRestaurants(pageable);
		}
		
		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		
		return "admin/restaurants/index";
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id,RedirectAttributes redirectAttributes,Model model) {
		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
		
		if (optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage","店舗が存在しません。");
			return "redirect:/admin/restaurants";
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		model.addAttribute("restaurant",restaurant);
		return "admin/restaurants/show";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
		return "admin/restaurants/register";
	}
	
	@PostMapping("/create") //フォームに対してバリデーションを行い、エラーがあれば民宿登録ページを再表示 無ければcreateHouseメソッドを実行
    public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        if (bindingResult.hasErrors()) {
            model.addAttribute("houseRegisterForm", restaurantRegisterForm);

            return "admin/restaurants/register";
        }

        restaurantService.createRestaurant(restaurantRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");

        return "redirect:/admin/restaurants";
    }  
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name ="id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
		
		if(optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage","店舗が存在しません。");
			return "redirect:/admin/restaurants";
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		RestaurantEditForm restaurantEditForm = new RestaurantEditForm(restaurant.getName(),null,restaurant.getDescription(),restaurant.getLowestPrice(),restaurant.getHighestPrice(),restaurant.getPostalCode(),restaurant.getAddress(),restaurant.getOpeningTime(),restaurant.getClosingTime(),restaurant.getSeatingCapacity());
		model.addAttribute("restaurant",restaurant);
		model.addAttribute("restaurantEditForm",restaurantEditForm);
		return "admin/restaurants/edit";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm,
			BindingResult bindingResult,
			@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectedAttributes,
			Model model) 
	{
		 System.out.println("★★ Controller: update メソッドに入りました ★★");
		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
		
		if(optionalRestaurant.isEmpty()) {
			redirectedAttributes.addFlashAttribute("errorMessage","店舗が存在しません。");
			return "redirect:/admin/restaurants";
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("restaurant",restaurant);
			model.addAttribute("restaurantEditForm",restaurantEditForm);
			
			return "admin/restaurants/edit";
		}
		
		restaurantService.updateRestaurant(restaurantEditForm, restaurant);
		redirectedAttributes.addFlashAttribute("successMessage","店舗を編集しました。");
		
		return "redirect:/admin/restaurants";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		Optional<Restaurant> optionalRestaurant = restaurantService.findRestaurantById(id);
		
		if(optionalRestaurant.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage","店舗が存在しません。");
			return "redirect:/admin/restaurants";
		}
		
		Restaurant restaurant = optionalRestaurant.get();
		restaurantService.deleteRestaurant(restaurant);
		redirectAttributes.addFlashAttribute("successMessage","店舗を削除しました。");
		
		return "redirect:/admin/restaurants";
	}
}
