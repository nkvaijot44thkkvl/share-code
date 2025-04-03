package com.example.samuraitravel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses/{id}/reviews")
public class ReviewController {
	 private final ReviewService reviewService;
	 private final HouseService houseService;
	 
	    public ReviewController(ReviewService reviewService,HouseService houseService) {
	        this.reviewService = reviewService;
	        this.houseService = houseService;
	    }
	    
	    @GetMapping
	    public String index(
	    		@PathVariable(name = "id") Integer id,
	    		@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
	    		Model model,
	    		RedirectAttributes redirectAttributes)
	    {
	    	
	    	pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());// ページネーション情報（1ページ目、10件表示）
	    	
	    	Optional<House> optionalHouse  = houseService.findHouseById(id);

	        if (optionalHouse.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

	            return "redirect:/houses";
	        }

	        House house = optionalHouse.get();

	        Page<Review> reviewPage = reviewService.findReviewsByHouseOrderByCreatedAtDesc(id, pageable);
	        System.out.println(reviewPage);
	        model.addAttribute("reviewPage", reviewPage); 
	        model.addAttribute("house", house); //ビューへモデルデータ受け渡し　もしかしたらカラムごとに分解するかも？
	       
	        // **ログインしている場合のみユーザー情報を追加**
	        if (isAuthenticated()) {
	            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	            model.addAttribute("hasUserAlreadyReviewed", reviewService.hasUserAlreadyReviewed(id));
	            model.addAttribute("user", userDetails);
	        }
	        
	        return "reviews/index";
	        }
	    
	    @PostMapping("/create") 
	    public String create (
	    		@PathVariable(name = "id") Integer id,
	    		@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
	            BindingResult bindingResult,
	            RedirectAttributes redirectAttributes,
	            Model model) {
	    	
	    	
	    	Optional<House> optionalHouse  = houseService.findHouseById(id);

	        if (optionalHouse.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

	            return "redirect:/houses";
	        }

	        House house = optionalHouse.get();
	        System.out.println("house_idは次のとおり：" + house.getId());
	        model.addAttribute("house", house);
	        
	    	 if (bindingResult.hasErrors()) {
	             model.addAttribute("reviewRegisterForm", reviewRegisterForm);
	             return "reviews/register";
	         }

	         reviewService.createReview(id ,reviewRegisterForm);
	         redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
	    	
	    	return  "redirect:/houses/{id}/reviews";
	    }
	    
	    
	    @GetMapping("/register") 
	    public String register(
	    		@PathVariable(name = "id") Integer id,
	    		Model model,
	    		RedirectAttributes redirectAttributes) {
	    	
	    	model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
	    	Optional<House> optionalHouse  = houseService.findHouseById(id);

	        if (optionalHouse.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

	            return "redirect:/houses";
	        }

	        House house = optionalHouse.get();
	        model.addAttribute("house", house);
	        
	        if (!model.containsAttribute("reviewRegisterForm")) {
	            model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
	        }
	        
	    	return "reviews/register"; ///houses/__${house.id}__/reviews/create
	    	 }
	    
		@GetMapping("{review_id}/edit") 
		    public String edit(
		    		@ModelAttribute ReviewEditForm reviewEditForm,
		    		   BindingResult bindingResult,
	                   @PathVariable(name = "review_id") Integer review_id,
	                   @PathVariable(name = "id") Integer id,
	                   RedirectAttributes redirectAttributes,
	                   Model model) {
			/*
	                   */
			Optional<House> optionalHouse  = houseService.findHouseById(id);
			 //String email = SecurityContextHolder.getContext().getAuthentication().getName();
		    // User user = userRepository.findByEmail(email);
			 
	        if (optionalHouse.isEmpty()) {
	            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

	            return "redirect:/houses";
	        }
	        
	        Optional<Review> optionalReview = reviewService.findReviewById(review_id);
	        if (optionalReview.isEmpty()) {
	            return "redirect:/houses/" + id;
	        }

	        House house = optionalHouse.get();
	        Review review = optionalReview.get();
	        model.addAttribute("house", house);
	        model.addAttribute("review", review);
	        System.out.println("review_id :" + review_id +"house_id :"+ id);
	        System.out.println("Review Content from DB: " + review.getContent());
	   
	        //if (!model.containsAttribute("reviewEditForm")) {
	           //ReviewEditForm reviewEditForm = new ReviewEditForm();
	            //model.addAttribute("reviewEditForm", new ReviewEditForm());
	        	reviewEditForm.setReivewId(review_id);
	            reviewEditForm.setHouseId(house.getId());  // house_id をセット
	            reviewEditForm.setScore(review.getScore());  // 過去の評価をセット
	            reviewEditForm.setContent(review.getContent());  // 過去のコメントをセット
	            model.addAttribute("reviewEditForm", reviewEditForm);
	            System.out.println("reviewEditForm: " + reviewEditForm);
	        //}
	        
	        return "reviews/edit";
		     }
		
		@PostMapping("/{review_id}/update")
		    public String update(
		    		@ModelAttribute @Validated ReviewEditForm reviewEditForm,
                    BindingResult bindingResult,
                    @PathVariable(name = "id") Integer id,
                    @PathVariable(name = "review_id") Integer review_id,
                    RedirectAttributes redirectAttributes,
                    Model model) {
			 System.out.println("PostMapping受信し、update()へ入りました");
			  // Debugging: ここで houseId の値を確認
			    System.out.println("Before setting houseId, reviewEditForm: " + reviewEditForm);

			    // houseId が null の場合、明示的にセット
			    if (reviewEditForm.getHouseId() == null) {
			        reviewEditForm.setHouseId(id);
			    }

			    // review_id が null の場合、明示的にセット
			    if (reviewEditForm.getReviewId() == null) {
			        reviewEditForm.setReivewId(review_id);
			    }

			    System.out.println("After setting houseId, reviewEditForm: " + reviewEditForm);
			    
		    	 Optional<Review> optionalReview  = reviewService.findReviewByHouseAndUser(id);

		        if (optionalReview.isEmpty()) {
		            redirectAttributes.addFlashAttribute("errorMessage", "編集できるレビューが存在しません。");
		            System.out.println("optionalReview.isEmpty()通過 ");
		            return "redirect:/houses/reviews/edit"; 
		        }

		        Review review = optionalReview.get();
		        
		        if (bindingResult.hasErrors()) {
		            model.addAttribute("review", review);
		            model.addAttribute("reviewEditForm", reviewEditForm);
		            System.out.println("bindingResult.hasErrors()通過 ");
		            return "redirect:/houses/reviews/edit";
		        }		        
		        
		        redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		        System.out.println("updateReview(id,reviewEditForm) :" + reviewEditForm);
		        reviewService.updateReview(id,reviewEditForm); 
		        return "redirect:/houses/{id}/reviews";
		    }
		 
		@PostMapping("/{reviewId}/delete")
		    public String delete(@PathVariable(name = "id") Integer house_id,  @PathVariable(name = "review_id") Integer review_id, RedirectAttributes redirectAttributes) {
			 Optional<Review> optionalReview = reviewService.findReviewById(review_id);

		        if (optionalReview.isEmpty()) {
		            redirectAttributes.addFlashAttribute("errorMessage", "レビューが存在しません。");

		            return "redirect:/houses/reviews";
		        }

		        Review review = optionalReview.get();
		        reviewService.deleteReview(review);
		        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

		        return "redirect:/houses/reviews";
		 }
		// ログイン状態を確認するメソッド
	    public boolean isAuthenticated() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	    }
		}
