package com.example.samuraitravel.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

/*DTO…データを運ぶオブジェクト
 * 
 *データの受け渡しに必要な最低限のフィールドのみ定義 ユーザーに改ざんされるリスクもあるため*/
@Data
@AllArgsConstructor
public class ReservationDTO {
    private Integer houseId;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private Integer numberOfPeople;

    private Integer amount;
}