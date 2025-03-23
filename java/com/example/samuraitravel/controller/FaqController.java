package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.service.FaqService;

@Controller
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    /**
     * FAQページを表示するメソッド。
     * @param keyword 検索キーワード（オプション）
     * @param page ページ番号（デフォルトは0）
     * @param model ビューに渡すデータ
     * @return FAQページのビュー名
     */

    @GetMapping("/faqs")
    public String index(@RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        Model model) {
    	
    	Pageable pageable = PageRequest.of(page, 5);
    	Page<?>  faqs;

        // キーワードが存在する場合は検索を実行
        if (keyword != null && !keyword.isEmpty()) {
            faqs = faqService.findAllFaqs(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            // キーワードが無ければ全FAQを取得
            faqs = faqService.getAllFaqs(pageable);
        }

        // FAQデータと検索キーワードをビューに渡す
        model.addAttribute("faqs", faqs);

        // FAQページのビューを返す
        return "user/faq";
    }
}
