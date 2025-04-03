package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewEditForm {
	private Integer house_id;  // house_id を格納するフィールド
	private Integer id;
	
	@Min(value = 1, message = "評価は1以上でなければなりません。")
	@Max(value = 5, message = "評価は5以下でなければなりません。")
	@NotNull(message = "評価は1～5のいずれかを選択してください。")
    private Integer score;

	@Size(max = 300, message = "コメントは300文字以内で入力してください。")
	@NotBlank(message = "コメントは必須です。")
	private String content;
	
    // getter & setter
    public Integer getHouseId() {
        return house_id;
    }

    public void setHouseId(Integer house_id) {
        this.house_id = house_id;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getReviewId() {
        return house_id;
    }

    public void setReivewId(Integer id) {
        this.id = id;
    }
}
