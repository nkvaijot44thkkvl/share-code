package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;

	    @Column(name = "name")
	    private String name;

	    @Column(name = "furigana")
	    private String furigana;

	    @Column(name = "postal_code")
	    private String postalCode;

	    @Column(name = "address")
	    private String address;
	    
	    @Column(name = "phone_number")
	    private String phoneNumber;

	    @Column(name = "birthday")
	    private LocalDate birthday;

	    @Column(name = "occupation")
	    private String occupation;
	    
	    @Column(name = "email")
	    private String email;
	    
	    @Column(name = "password")
	    private String password;
	    
	    //多対一のフィールドにアノテーションをつけ、リレーションシップを表現している
	    @ManyToOne
	    @JoinColumn(name = "role_id")
	    private Role role;

	    @Column(name = "enabled")
	    private Boolean enabled;
	    
	    @Column(name = "stripe_customer_id ")
	    private String stripe_customer_id ;

	    /*カラムに挿入/更新できるかの初期値はtrueなので、指定が必要
	     *falseにするとデータベース側の設定がかかりデフォルト値が入る */
	    @Column(name = "created_at", insertable = false, updatable = false)
	    private Timestamp createdAt;

	    @Column(name = "updated_at", insertable = false, updatable = false)
	    private Timestamp updatedAt;
}
