package com.example.samuraitravel.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	/*@DateTimeFormatアノテーション
	 *pattern属性に指定したフォーマットの文字列をLocalDate型など日時型のデータに変換
	 *yyyy-MM-ddなので2000-01-01はTrue 2000年1月1日はエラー*/
    @NotNull(message = "チェックイン日を選択してください。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkinDate;

    @NotNull(message = "チェックアウト日を選択してください。")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    @NotNull(message = "宿泊人数を入力してください。")
    @Min(value = 1, message = "宿泊人数は1人以上に設定してください。")
    private Integer numberOfPeople;
}