package com.example.samuraitravel.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    
    public ReviewService(ReviewRepository reviewRepository ,HouseRepository houseRepository, UserRepository userRepository) {
    	this.reviewRepository = reviewRepository;
    	this.houseRepository = houseRepository;
    	this.userRepository = userRepository;
    }
    
    //指定したIDを持つレビューを取得する
    public Optional<Review> findReviewById(Integer id){
    	 Optional<Review> review = reviewRepository.findById(id);
    	 return review;
    }
    
    //指定した民宿のレビューを作成日時が新しい順に6件取得する
	public List<Review> findTop6ReviewsByHouseOrderByCreatedAtDesc(Integer house_id){
		 //return reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house_id);
		 
		 // Houseエンティティを取得
		    Optional<House> optionalHouse = houseRepository.findById(house_id);
		    
		    // Houseが存在する場合
		    if (optionalHouse.isPresent()) {
		        // Houseオブジェクトを渡してレビューを取得
		    	House house = optionalHouse.get();
		        return reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);
		    }
		    
		    // Houseが見つからない場合、空のリストを返す
		    return Collections.emptyList();
	}
	
	//指定した民宿とユーザーのレビューを取得する
	public Optional<Review> findReviewByHouseAndUser(Integer house_id) {
		 // 1. ログインユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("ログインユーザーが見つかりません;findReviewByHouseAndUser");
        }
        
       Optional <House> optionalHouse = houseRepository.findById(house_id);
       House house = optionalHouse.get();
		return reviewRepository.findByHouseAndUser(house,user);
	}

	//指定した民宿のレビュー件数を取得する
	public long countReviewsByHouse(Integer house_id){
		//countReviewsByHouseに渡された引数IDに一致するHouseエンティティを取得		
		Optional<House> optionalHouse = houseRepository.findById(house_id);
		
		 // 一致するHouseが存在する場合
	    if (optionalHouse.isPresent()) {
	        // Houseオブジェクトを渡してレビュー件数を取得
	    	House house = optionalHouse.get();
	        return reviewRepository.countByHouse(house);
	    }
	    
	    // Houseが見つからない場合、0
	    return 0;
	}
	
	//指定した民宿のすべてのレビューを作成日時が新しい順に並べ替え、ページングされた状態で取得する
	public Page<Review> findReviewsByHouseOrderByCreatedAtDesc(Integer house_id, Pageable pageable){
		System.out.println("house_id " + house_id);
		Optional<House> house = houseRepository.findById(house_id);
		if (house.isEmpty()) {
	        System.out.println("Houseオブジェクトが空。");// Houseが存在しない場合、空のページを返す
	    }
		// Houseオブジェクトを渡してレビュー件数を取得
	    return reviewRepository.findByHouseOrderByCreatedAtDesc(house.get(),pageable);
	   
	}
	
	//レビュー投稿ページ用のフォームクラスからのデータをもとに、新しいレビューを登録する
	@Transactional
	public Review createReview(Integer house_id,ReviewRegisterForm reviewRegisterForm){
        Review review = new Review();
        // 1. ログインユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Optional <House> optionalHouse = houseRepository.findById(house_id);
        House house = optionalHouse.get();

        review.setUser(user);
        review.setHouse(house);
        review.setScore(reviewRegisterForm.getScore());
        review.setContent(reviewRegisterForm.getContent());
        
        return reviewRepository.save(review);
	}
	
	//レビュー編集ページ用のフォームクラスからのデータをもとに、既存のレビューを更新する
	@Transactional
	public Review updateReview(Integer house_id, ReviewEditForm reviewEditForm){
		  Review review = new Review();
	        // 1. ログインユーザーを取得
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        User user = userRepository.findByEmail(email);
	        Optional <House> optionalHouse = houseRepository.findById(house_id);
	        House house = optionalHouse.get();

	        review.setUser(user);
	        review.setHouse(house);
	        review.setScore(reviewEditForm.getScore());
	        review.setContent(reviewEditForm.getContent());
	    // 3. レビューの内容を更新
	    review.setScore(reviewEditForm.getScore());
	    review.setContent(reviewEditForm.getContent());

	    return reviewRepository.save(review);
		
	}
	
	//指定したレビューを削除する
	public void deleteReview(Review review){
		reviewRepository.delete(review);
	}
	
	//指定したユーザーが、指定した民宿のレビューをすでに投稿済みかどうかをチェックする
	public boolean hasUserAlreadyReviewed(Integer house_id){
		 // 1. ログインユーザーを取得
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("ログインユーザーが見つかりません:hasUserAlreadyReviewed");
        }
        
        Optional <House> optionalHouse = houseRepository.findById(house_id);
        House house = optionalHouse.get();
		return reviewRepository.findByHouseAndUser(house, user).isPresent();
	}
    
}