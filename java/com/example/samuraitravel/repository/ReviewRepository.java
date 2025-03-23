package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	public Page<Review> findTop6ByHouseOrderByCreatedAtDesc(House house, Pageable pageable);
	public Page<Review> findByHouseAndUser(House house, Pageable pageable);
	public Page<Review> countByHouse(House house);
	public Page<Review> findByHouseOrderByCreatedAtDesc(House house);
}
