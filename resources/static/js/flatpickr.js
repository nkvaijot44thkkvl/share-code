let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3); //3か月後の日付を取得する

// Flatpickrのインスタンスを生成する Flatpickr=JavaScript製のライブラリ
//id属性に"fromCheckinDateToCheckoutDate"が設定されたHTML要素（入力フォーム）に対し、Flatpickrのインスタンスを生成
flatpickr('#fromCheckinDateToCheckoutDate', {
 mode: "range", //カレンダーのモード
 locale: 'ja', //カレンダーの言語
 minDate: 'today', //カレンダーで選択できる最小の日付
 maxDate: maxDate, //カレンダーで選択できる最大の日付
 onClose: function(selectedDates, dateStr, instance) { //カレンダーを閉じたときの処理
 //選択した日付のオブジェクト,選択した日付の文字列,Flatpickrのインスタンス
   const dates = dateStr.split(" から "); //" から "で文字列を分割、要素数が2の配列（例：'2024-08-01'と'2024-08-05'）に変換
   if (dates.length === 2) { //チェックインとチェックアウトが別日
     document.querySelector("input[name='checkinDate']").value = dates[0];
     document.querySelector("input[name='checkoutDate']").value = dates[1];
   } else { //チェックインとチェックアウトが同日 フロント側でチェックインとアウトを分けるとバックエンド側が楽になる
     document.querySelector("input[name='checkinDate']").value = '';
     document.querySelector("input[name='checkoutDate']").value = '';
   }
 }
});