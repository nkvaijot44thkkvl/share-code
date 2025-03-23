package com.example.samuraitravel.event;

import org.springframework.context.ApplicationEvent;

import com.example.samuraitravel.entity.User;

import lombok.Getter;

/*イベントクラスはApplicationEventクラスを継承して作成
 *ApplicationEventクラスはイベント作成するための基本的クラスで
 *イベントの発生源を保持する
 *Getterアノテーションをつけるとゲッターの定義がされる
 *Eventクラスはイベントに関する情報を保持するので
 *Listerクラスから情報を取得できるようにする*/
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
