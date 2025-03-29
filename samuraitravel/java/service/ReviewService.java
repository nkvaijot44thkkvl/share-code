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
            throw new EntityNotFoundException("ログインユーザーが見つかりません");
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
		Optional<House> house = houseRepository.findById(house_id);
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
       
        review.setUser(user);
        review.setScore(reviewRegisterForm.getScore());
        review.setContent(reviewRegisterForm.getContent());
        
        return reviewRepository.save(review);
	}
	
	//レビュー編集ページ用のフォームクラスからのデータをもとに、既存のレビューを更新する
	@Transactional
	public Review updateReview(Review review , ReviewEditForm reviewEditForm){
		/*review.setHouse(reviewEditForm.getHouseId());
		review.setUser(reviewEditForm.getFuhouserigana());
		review.setScore(reviewEditForm.getPostalCode());
		review.setContent(reviewEditForm.getAddress());
		
	    userRepository.save(review);*/
	    
	    // 1. 編集対象のレビューを取得
	    /*Review review = reviewRepository.findById(reviewId)
	            .orElseThrow(() -> new EntityNotFoundException("レビューが見つかりません"));

	    // 2. ログインユーザーが投稿者かどうかチェック
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();
	    if (!review.().getEmail().equals(email)) {
	        throw new AccessDeniedException("このレビューを編集する権限がありません");
	    }*/
		
		//もしかしたら@PathVariable(name = "id") Integer idを引数に追加、宿のURL変数へ変更？

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
            throw new EntityNotFoundException("ログインユーザーが見つかりません");
        }
        
        Optional <House> optionalHouse = houseRepository.findById(house_id);
        House house = optionalHouse.get();
		return reviewRepository.findByHouseAndUser(house, user).isPresent();
	}
    
}