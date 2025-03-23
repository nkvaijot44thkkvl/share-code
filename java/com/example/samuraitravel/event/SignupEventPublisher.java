package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.entity.User;

/*イベントを発生させたい処理につけるアノテーション
 * DIコンテナに登録して依存性の注入を準備*/
@Component
public class SignupEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /*イベントを発行
     * 引数に発行したいイベントのインスタンスを渡す
     * publishEvent(自分自身のインスタンス,発行したいイベントクラス)*/
    public void publishSignupEvent(User user, String requestUrl) {
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}