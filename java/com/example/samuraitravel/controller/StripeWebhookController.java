package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.samuraitravel.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@Controller
public class StripeWebhookController {
    private final StripeService stripeService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    //Webhookの署名シークレットを取得:Webhookイベントの通知先とコントローラの@PostMappingアノテーションの引数を一致させる必要がある
    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {        
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("Webhookの署名シークレットが正しくありません。");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // "checkout.session.completed"イベントを受信
        //イベントの種類が"checkout.session.completed"と等しいとき、StripeServiceクラスのprocessSessionCompleted()メソッドを呼び出す
        if ("checkout.session.completed".equals(event.getType())) {
            stripeService.processSessionCompleted(event);
        }

        //予約一覧ページへのリダイレクトはStripe側で実行
        //このwebhook()メソッドの役割は「イベント通知を受信し、予約情報をデータベースに登録すること」のみ
        return new ResponseEntity<>("Success", HttpStatus.OK);
        //最後はビューの表示やリダイレクトの代わりにHTTPステータスコード（HTTPレスポンスに付与される3桁の数値）を返す
    }
}