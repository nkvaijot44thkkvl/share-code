package com.example.samuraitravel.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.UserRepository;

@Service //アノテーションをつけてサービスを明示
public class UserDetailsServiceImpl implements UserDetailsService { //→このクラスの役割はUserDetailsImplクラスのインスタンス生成
	/*UserDetailsServiceImplがUserRepositoryを使用(=依存)しているので
     *コンストラクタインジェクションでDIの注入がなされている */
    private final UserRepository userRepository;

    //UserDetailsServiceインターフェースを実装
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override //抽象メソッドの具体的定義で上書き
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //このクラスの役割をメインで果たすメソッド
        try {
        	//フォームから送信されたメアドに一致するユーザの取得
            User user = userRepository.findByEmail(email);
            String userRoleName = user.getRole().getName();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            return new UserDetailsImpl(user, authorities);
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }
}
