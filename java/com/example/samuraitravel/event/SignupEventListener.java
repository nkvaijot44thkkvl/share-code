package com.example.samuraitravel.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.service.VerificationTokenService;

/*ListenerクラスのインスタンスがDIコンテナに登録されるようにする
 *こうすると後の＠EventListenerのメソッドを自動的に検出、実行する*/
@Component
public class SignupEventListener {
    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender javaMailSender;

    public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
        this.verificationTokenService = verificationTokenService;
        this.javaMailSender = mailSender;
    }

    /*Listenerクラスにイベント発生時に実行するメソッドに対して設定する
     *どのEventかを設定する　今回はsignupEvent*/
    @EventListener
    private void onSignupEvent(SignupEvent signupEvent) {
        User user = signupEvent.getUser();
        /*トークンはほぼ重複しない一意IDのこと
         *ユーザーIDとともにデータベースに保存する
         *生成したトークンをメール認証用にURLにパラメータとして埋め込み
         *アクセスされたときにデータベースの値と一致するか認証する*/
        String token = UUID.randomUUID().toString();
        verificationTokenService.create(user, token);

        String senderAddress = "springboot.samuraitravel@example.com";
        String recipientAddress = user.getEmail();
        String subject = "メール認証";
        String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
        String message = "以下のリンクをクリックして会員登録を完了してください。";

        /*SimpleMailMessageクラスを使ってメール内容を作成
         *javaMailSenderインターフェースのsend()メソッドに前述のSimpleMailMessageオブジェクトを渡す*/
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderAddress);
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + confirmationUrl);
        /* javaMailSenderインターフェースを使ってメールを送信*/
        javaMailSender.send(mailMessage);
    }
}