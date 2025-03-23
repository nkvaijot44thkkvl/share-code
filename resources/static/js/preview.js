const imageInput = document.getElementById('imageFile');
const imagePreview = document.getElementById('imagePreview'); //div要素

//idにimageFileが指定されるHTML要素の値が変更される度…change 実行
imageInput.addEventListener('change', () => {
 if (imageInput.files[0]) {
   let fileReader = new FileReader();
   //無事に読み込まれたらidにimagePreviewが指定されているHTMLに読み込まれた画像ファイル表示のためimg属性を挿入
   fileReader.onload = () => {
     imagePreview.innerHTML = `<img src="${fileReader.result}" class="mb-3">`;
   }
   fileReader.readAsDataURL(imageInput.files[0]);
 } else {
	 //なにもない時はHTMLの中身を空にする
   imagePreview.innerHTML = '';
 }
})