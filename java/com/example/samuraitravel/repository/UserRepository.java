package com.example.samuraitravel.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.User;

/*リポジトリとは、データべースにアクセスし
 * CRUD処理を行うインターフェース(メソッド名・引数)*/
public interface UserRepository extends JpaRepository<User, Integer> {
	public User findByEmail(String email);
	
	/*リポジトリではfindBy甲Or乙等を定義することで
	 *SQLのANDやOR検索ができる 甲と乙には検索対象のカラム名
	 *LIKE区をつけるとあいまい検索ができる findBy甲Like*/
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}
