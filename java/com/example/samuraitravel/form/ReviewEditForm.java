package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewEditForm {
	@Size(min = 1, max = 5 ,message = "評価は1～5のいずれかを選択してください。")
    @NotNull(message = "評価は1～5のいずれかを選択してください。")
    private Integer score;

    @Max(value = 300 ,message = "コメントは300文字以内で入力してください。")
    @NotNull(message = "コメントは300文字以内で入力してください。")
    private String content;
}
