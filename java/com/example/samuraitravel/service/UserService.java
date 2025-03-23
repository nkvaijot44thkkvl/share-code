package com.example.samuraitravel.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Role;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service //アノテーションをつけサービスとして機能させる
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional //アノテーションをつけトランザクションを機能させる
    public User createUser(SignupForm signupForm) {
    	//エンティティ(Userクラス)をインスタンス化する
        User user = new User();
        /*roleフィールドへRoleオブジェクトをセットする
         *会員ページからの登録は一般ユーザーのみと想定しているのでGENERAL*/
        Role role = roleRepository.findByName("ROLE_GENERAL");

        /*引数で受け取ったフォームクラスのインスタンスの各フィールドの値を
         *セッターでインスタンス化した各エンティティの各フィールドへセットする */
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false); //認証済みか判定するときに使用する

        return userRepository.save(user); //saveメソッドでエンティティをデータベースへ保存する
    }
    
    @Transactional
    public void updateUser(UserEditForm userEditForm, User user) {
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        userRepository.save(user);
    }   
    
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
    	/*アドレスが存在しているかデータベースを検索
    	 *存在していればUserオブジェクトが代入、無ければnullが代入*/
        User user = userRepository.findByEmail(email);
        /*userがnullではない、アドレスが登録済みかチェック
         *nullでなければ返すということ*/
        return user != null;
    }
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
    	/*equals()で文字列同士を比較する
    	 *一致すればtrue、不一致ならfalse
    	 *その真偽値を返す*/
        return password.equals(passwordConfirmation);
    }
    
    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
    	//一致するデータが見つかれば、そのデータに紐づいたユーザーのenableカラムの値をtrueにする
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm, User user) {
        return !userEditForm.getEmail().equals(user.getEmail());
    }

    // 指定したメールアドレスを持つユーザーを取得する
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }  
    
    // すべてのユーザーをページングされた状態で取得する
    //自動的に実装されているので自分で定義する必要はない
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 指定されたキーワードを氏名またはフリガナに含むユーザーを、ページングされた状態で取得する
    public Page<User> findUsersByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable) {
    	//リポジトリの関数を呼び出している？
        return userRepository.findByNameLikeOrFuriganaLike("%" + nameKeyword + "%", "%" + furiganaKeyword + "%", pageable);
    }  
    
 // 指定したidを持つユーザーを取得する
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }    
}
