package com.example.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Role;

/* インターフェースの継承時には、2つの引数を指定
 * JpaRepository<エンティティのクラス型, 主キーのデータ型>*/
public interface RoleRepository extends JpaRepository<Role, Integer> {
	public Role findByName(String name);
}
