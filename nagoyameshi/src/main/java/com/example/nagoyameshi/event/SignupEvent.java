package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.User;

import lombok.Getter;

@Getter
public class SignupEvent extends ApplicationEvent {
	/*会員登録したユーザーの情報（Userオブジェクト）
	  リクエストを受けたURL（https://ドメイン名/signup）
	  保持*/
    private User user;
    private String requestUrl;

    public SignupEvent(Object source, User user, String requestUrl) {
        /*スーパー(親)クラスのコンストラクタを呼び出しイベントの発生源を渡す
         * イベントソースはPublisherクラスのインスタンス*/
    	super(source);

        this.user = user;
        this.requestUrl = requestUrl;
    }

}