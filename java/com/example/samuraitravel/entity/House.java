package com.example.samuraitravel.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/*エンティティとは、テーブルの1行をオブジェクトとして表現するためのクラス*/
@Entity
@Table(name = "houses")
@Data
@ToString(exclude = {"reservations", "reviews"}) //循環を回避、双方向の参照からreservationsフィールド,reviewsフィールドを除外

public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    //一対多のフィールドに@OneToManyアノテーション
    /* mappedBy属性を設定して双方向の参照
     * @OneToManyアノテーションをつけただけでは、「どのエンティティと紐づければよいか」をSpring Boot側で判断できない
     * mappedBy属性にカラム名をつけて、その指定をする
     * fetch = FetchType.EAGER エンティティを読み込むときに紐づいたデータを読み取る
     * cascade = CascadeType.REMOVE 親エンティティを消すと子エンティティも消す 宿を消すと、予約も消すということ*/
    @OneToMany(mappedBy = "house", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    //List<相手のエンティティのクラス型>型でフィールドを定義
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "house", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Review> reviews;
}