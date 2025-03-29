package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraitravel.entity.House;

/*リポジトリとは、データベースにアクセスし、CRUD処理を行うインターフェース*/
public interface HouseRepository extends JpaRepository<House, Integer> {
	//キーワードを使った独自のメソッドを追加することで、基本的なCRUD操作以外にも高度なクエリを実行できる
	//LIKEキーワードであいまい検索
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	/*idを降順に並びかえ、最初の1件を取得することで、最後に追加した民宿を取得する*/
	public House findFirstByOrderByIdDesc(); 
	//findBy○○○LessThanEqual() SQLのWHERE句のように○○○ <= 引数の値という条件で検索
	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
    public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
    public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
    public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
    public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<House> findAllByOrderByPriceAsc(Pageable pageable);   
    public List<House> findTop8ByOrderByCreatedAtDesc();
    
    /*「リレーション先のテーブルのデータ数を基準に並べ替える」という複雑なクエリは、キーワードでは実現できない
		そこで役立つのが、@Queryアノテーション
		@Queryアノテーションの引数にJPQL文を渡すことで、キーワードで実現できない複雑なクエリを実現
		JPQL:SQLがデータベースのテーブルを対象としたクエリ言語で、JPQLはエンティティを対象としたクエリ言語。SQLとほぼ同じ構文で記述*/
    @Query("SELECT h FROM House h LEFT JOIN h.reservations r GROUP BY h.id ORDER BY COUNT(r) DESC")
    /*Houseエンティティのインスタンスを取得
	  Houseエンティティのエイリアス（別名）としてhを指定（エイリアスを指定しておくことで、クエリの他の部分で使用できる）。
	  HouseエンティティとReservationエンティティの間で左外部結合を実行。Reservationエンティティのエイリアス（別名）としてrを指定。
	  Houseエンティティのidフィールドの値ごとにグループ化。これにより、各民宿の予約数をカウントできる。
	  Houseエンティティに関連付けられているReservationエンティティの数をカウント、降順（予約数が多い順）で並べ替え*/
    List<House> findAllByOrderByReservationCountDesc(Pageable pageable); 
    //pageableオブジェクトを設定しているが、これは取得するデータの数を制限できるようにするため
    //Pageableオブジェクトはページネーションを実装するだけでなく、取得するデータの数を制限するためにも使える
}