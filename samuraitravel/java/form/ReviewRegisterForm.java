package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRegisterForm {	
	@Min(value = 1, message = "評価は1以上でなければなりません。")
	@Max(value = 5, message = "評価は5以下でなければなりません。")
    @NotNull(message = "評価は1～5のいずれかを選択してください。")
    private Integer score;

	@Size(max = 300, message = "コメントは300文字以内で入力してください。")
	@NotNull(message = "コメントは300文字以内で入力してください。")
	private String content;
}
