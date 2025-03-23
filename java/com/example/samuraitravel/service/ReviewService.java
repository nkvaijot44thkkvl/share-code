package com.example.samuraitravel.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;

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
    public Page<Review> findReviewById(){
    	return reviewRepository.findByHouseAndUser(Integer id);
    }
    
    //指定した民宿のレビューを作成日時が新しい順に6件取得する
	public Page<Review> findTop6ReviewsByHouseOrderByCreatedAtDesc(){
		 return reviewRepository.findTop6ByHouseOrderByCreatedAtDesc();
	}
	
	//指定した民宿とユーザーのレビューを取得する
	public Page<Review> findReviewByHouseAndUser(){
		return reviewRepository.findByHouseAndUser();
	}
	
	//指定した民宿のレビュー件数を取得する
	public Page<Review> countReviewsByHouse(){
		return reviewRepository.countByHouse();
	}
	
	//指定した民宿のすべてのレビューを作成日時が新しい順に並べ替え、ページングされた状態で取得する
	public Page<Review> findReviewsByHouseOrderByCreatedAtDesc(){
		return reviewRepository.findByHouseOrderByCreatedAtDesc();
	}
	
	//レビュー投稿ページ用のフォームクラスからのデータをもとに、新しいレビューを登録する
	public Page<Review> createReview(ReviewRepository reviewRepository){
        Review review = new Review();

        review.setScore(reviewRepository.getScore());
        review.setContent(reviewRepository.getContent());
        
        reviewRepository.save(review);
	}
	
	//レビュー編集ページ用のフォームクラスからのデータをもとに、既存のレビューを更新する
	public Page<Review> updateReview(){
		return reviewRepository.findByHouseAndUser();
	}
	
	//指定したレビューを削除する
	public Page<Review> deleteReview(){
		return reviewRepository.findByHouseAndUser();
	}
	
	//指定したユーザーが、指定した民宿のレビューをすでに投稿済みかどうかをチェックする
	public Page<Review> hasUserAlreadyReviewed(){
		return reviewRepository.findByHouseAndUser();
	}
    
}
