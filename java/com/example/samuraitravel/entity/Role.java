package com.example.samuraitravel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//エンティティアノテーションでエンティティとして機能させる
@Entity
//アノテーションで対応付けるテーブルを指定
@Table(name = "roles")
//Lombokが提供するDataアノテーションでゲッターやセッターを自動生成
@Data
public class Role {
		//主キーを指定
	   @Id
	   //自動採番を指定し有効化
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   //各フィールドに対応付けるカラム名を指定
	   @Column(name = "id")
	   private Integer id;
	    
	   //各フィールドに対応付けるカラム名を指定
	   @Column(name = "name")
	   private String name;  

}
