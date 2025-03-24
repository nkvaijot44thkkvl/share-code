package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	public Review findTop6ByHouseOrderByCreatedAtDesc(Integer house_id);
	public Review findByHouseAndUser(Integer house_id,Integer user_id);
    public Review countByHouse(Integer house_id);
	public Review findByHouseOrderByCreatedAtDesc(Integer house_id);
}
