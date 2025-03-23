package com.example.samuraitravel.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.samuraitravel.entity.User;

public class UserDetailsImpl implements UserDetails {
    private final User user;
    private final Collection<GrantedAuthority> authorities;

    //UserDetailsインターフェースを実装したクラス ユーザー情報を保持する役割
    public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    // ハッシュ化済みのパスワードを返す
    @Override //UserDetailsの抽象メソッドを上書きし具体的処理を作成
    public String getPassword() {
        return user.getPassword();
    }

    // ログイン時に利用するユーザー名（メールアドレス）を返す
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // ロールのコレクションを返す
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	//<? extends A>の書き方はAまたはそのサブタイプ全てということ
    	//親クラスを継承した子クラスやインターフェースを実装したクラスが当てはまる
    	//GrantedAuthorityのインターフェースを実装したクラスと子クラスのオブジェクトのコレクションのこと
    	//GrantedAuthorityはユーザーの権限を示すインターフェースSpringSecurityが提供
        return authorities;
    }

    // アカウントが期限切れでなければtrueを返す
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // ユーザーがロックされていなければtrueを返す
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // ユーザーのパスワードが期限切れでなければtrueを返す
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // ユーザーが有効であればtrueを返す
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
