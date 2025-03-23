package com.example.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.entity.VerificationToken;
import com.example.samuraitravel.event.SignupEventPublisher;
import com.example.samuraitravel.form.SignupForm;
import com.example.samuraitravel.service.UserService;
import com.example.samuraitravel.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) { 
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }
	/*ログイン画面のルートパスを/loginにしているが
	 * SpringBoot Securityデフォルトのログインページのルートも/login
	 * そのため特段設定しないとSpringBootのログインページが表示される
	 * WebSecurityConfigを設定・反映させると独自のログインページになる*/
	@GetMapping("/login")
    public String login() {
        return "auth/login";
    }
	
	/*フォームクラスのインスタンスをビューへ渡す
	 * フォームの各入力項目と各フィールドをバインドする*/
	@GetMapping("/signup")
	/*Modelクラスを使いビューへデータを渡す
	 *1.メソッドにModel型引数を指定
	 *2.メソッド内でaddAttribute(ビュー側から参照する変数名,ビューに渡すデータ)メソッドを使用 */
    public String signup(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        //当該htmlでsignupFormの変数を使用することでコントローラーから渡されるSignFormクラスのインスタンスを参照する
        return "auth/signup";
    }
	
	/*フォームの入力内容をHTTPリクエストのpostメソッドを送信
	 * 送信先("/signup")には、@PostMappingアノテーションをつける*/
	@PostMapping("/signup")
	/*引数に@ModelAttributeアノテーションをつける
	 *フォームから送信されたデータ(フォームクラスインスタンス)を引数にバインド
	 *@Validatedをつけることで
	 *バリデーションの対象にすることができる
	 *Binding Resultはバリデーション結果を保持するインターフェース*/
    public String signup(@ModelAttribute @Validated SignupForm signupForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest,
                         Model model)
    {
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
        	/*フォームクラスのアノテーションで設定したバリデーションのエラー内容以外にも
             * サービスクラスに登録した独自のエラーを加えることができる
             * FieldErrorクラスのインスタンスを作成し、引数で渡す
             * 1.エラーを格納するオブジェクト
             * 2.エラーを発生させるフィールド
             * 3.エラーメッセージ*/
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }

        /*BindingResultが提供するhasErrors()メソッドでエラーの有無をチェックできる
         * エラーがあれば会員登録ページを表示*/
        if (bindingResult.hasErrors()) {
            model.addAttribute("signupForm", signupForm);

            return "auth/signup";
        }

        /*エラーがなければリダイレクト
         * サービスクラスに定義したcreateUser()メソッドを実行
         * 引数はフォームクラスのインスタンス
        userService.createUser(signupForm);*/
        /*ダイレクト先にデータを渡したい時はredirectAttributes型をメソッドに着ける
         * addFlashAttribute()メソッドを使えばリダイレクト先に渡せる
        redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");*/
        User createdUser = userService.createUser(signupForm);
        /*引数でHttpServletRequestオブジェクトを受け取る
         *HTTPリクエストに関するさまざまな情報を提供するインターフェース
         *今回はリクエストURLを受け取る*/
        String requestUrl = new String(httpServletRequest.getRequestURL());
        /*SignupEventPublisherクラスに定義したメソッドを発行
        ユーザーの会員登録が完了したタイミングでイベントを発行
        引数1.会員登録したユーザーの Userオブジェクト,2.String型のリクエストURL*/
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl); 
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");        
        
        /*1.リダイレクト先から参照する変数名
         *2.リダイレクト先に渡すデータ
         *このデータは渡した後に削除されるので、1回しか使わないデータに用いる*/
        return "redirect:/";
    }  
	 @GetMapping("/signup/verify")
	 /*引数に@RequestParamアノテーションをつける
	  *リクエストパラメータ(HTTPリクエストに含まれるデータ)の値をその引数にバインドできる
	  *次の属性を設定できる name属性:リクエストパラメータ名
	  *required属性:そのパラメータが必須かどうか defaultValue属性:パラメータの値指定なし、空の場合のデフォルト値*/
	    public String verify(@RequestParam(name = "token") String token, Model model) {
	        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
	        
	        // トークンが存在すれば、会員を有効にする
	        if (verificationToken != null) {
	            User user = verificationToken.getUser();  
	            userService.enableUser(user);
	            String successMessage = "会員登録が完了しました。";
	            model.addAttribute("successMessage", successMessage);            
	        } else {
	            String errorMessage = "トークンが無効です。";
	            model.addAttribute("errorMessage", errorMessage);
	        }
	        
	        return "auth/verify";         
	    }    
}
