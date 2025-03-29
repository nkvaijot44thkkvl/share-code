package com.example.samuraitravel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.service.ReviewService;
@Controller
@RequestMapping("/houses/{id}/reviews")
public class ReviewController {
	 private final ReviewService reviewService;

	    public ReviewController(ReviewService reviewService) {
	        this.reviewService = reviewService;
	    }
	    
	    @GetMapping("/index")
	    public String index(
	    		@PathVariable(name = "id") Integer id,
	    		@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
	    		Model model)
	    {	    
	       /* model.addAttribute("housePage", housePage);
	        model.addAttribute("keyword", keyword);
	        model.addAttribute("area", area);
	        model.addAttribute("price", price);
	        model.addAttribute("order", order);*/
	        
	        pageable = PageRequest.of(0, 10); // ページネーション情報（1ページ目、10件表示）

	        Page<Review> reviewPage = reviewService.findReviewsByHouseOrderByCreatedAtDesc(id, pageable);
	        model.addAttribute("reviewPage", reviewPage); //ビューへモデルデータ受け渡し　もしかしたらカラムごとに分解するかも？
	        return "reviews/index";
	        }
	    
	    @PostMapping("/create") 
	    public String create (
	    		@PathVariable(name = "id") Integer id,
	    		@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
	            BindingResult bindingResult,
	            RedirectAttributes redirectAttributes,
	            Model model) {
	    	
	    	 if (bindingResult.hasErrors()) {
	             model.addAttribute("houseRegisterForm", reviewRegisterForm);

	             return "/houses/{id}/reviews/register";
	         }

	         reviewService.createReview(id ,reviewRegisterForm);
	         redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
	    	
	    	return  "redirect:/houses/{id}/reviews";
	    }
	    
	    
	    @GetMapping("/register") 
	    public String register(Model model) {	    
	    	model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());

	    	return "reviews/register";
	    	 }
	    
		@GetMapping("/edit") 
		    public String edit(@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
                    BindingResult bindingResult,
                    RedirectAttributes redirectAttributes,
                    Model model,
                    @PathVariable(name = "id") Integer id) {
			if (bindingResult.hasErrors()) {
	            model.addAttribute("reviewRegisterForm", reviewRegisterForm);

	            return "houses/reviews/register";
	        }

			reviewService.createReview(id,reviewRegisterForm);
	        redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");

	        return "redirect:/houses/reviews/index";
		    	 }
		
		@GetMapping("/update")
		    public String update(
		    		@ModelAttribute @Validated ReviewEditForm reviewEditForm,
                    BindingResult bindingResult,
                    @PathVariable(name = "id") Integer id,
                    RedirectAttributes redirectAttributes,
                    Model model) {
					 
		    	 Optional<Review> optionalReview  = reviewService.findReviewByHouseAndUser(id);

		        if (optionalReview.isEmpty()) {
		            redirectAttributes.addFlashAttribute("errorMessage", "編集できるレビューが存在しません。");

		            return "redirect:/houses/reviews/edit"; 
		        }

		        Review review = optionalReview.get();
		        
		        if (bindingResult.hasErrors()) {
		            model.addAttribute("review", review);
		            model.addAttribute("reviewEditForm", reviewEditForm);

		            return "redirect:/houses/reviews/edit";
		        }
		        
		        reviewService.updateReview(review,reviewEditForm); //Review review , ReviewEditForm reviewEditForm
		        redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");

		        return "redirect:/houses/reviews";
		    }
		 
		@PostMapping("/delete")
		    public String delete(@PathVariable(name = "id") Integer house_id,Integer user_id, RedirectAttributes redirectAttributes) {
			 Optional<Review> optionalReview = reviewService.findReviewByHouseAndUser(house_id);

		        if (optionalReview.isEmpty()) {
		            redirectAttributes.addFlashAttribute("errorMessage", "レビューが存在しません。");

		            return "redirect:/houses/reviews";
		        }

		        Review review = optionalReview.get();
		        reviewService.deleteReview(review);
		        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

		        return "redirect:/houses/reviews";
		 }
		}
