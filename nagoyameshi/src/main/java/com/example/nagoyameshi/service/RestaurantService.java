package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}
	
	//すべての店舗をページングされた状態で取得する。
	public Page<Restaurant> findAllRestaurants(Pageable pageable){
		return restaurantRepository.findAll(pageable);
	}
	
	//指定されたキーワードを店舗名に含む店舗を、ページングされた状態で取得する。
	public Page<Restaurant> findRestaurantsByNameLike(String keyword, Pageable pageable){
		return restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
	}
	
	//指定したidを持つ店舗を取得する。
	public Optional<Restaurant> findRestaurantById(Integer id){
		return restaurantRepository.findById(id);
	}
	
	//店舗のレコード数を取得する。
	public long countRestaurants() {
		return restaurantRepository.count();
	}
	
	//idが最も大きい店舗を取得する。
	public Restaurant findFirstRestaurantByOrderByIdDesc() {
		return restaurantRepository.findFirstByOrderByIdDesc();
	}
	
	//フォームから送信された店舗情報をデータベースに登録する。
	@Transactional
	public void createRestaurant(RestaurantRegisterForm restaurantRegisterForm) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); //元のファイル名を取得するメソッド
            //UUIDを使いファイル名変更メソッド呼び出し
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            //Java Fileクラス提供のcopyメソッドでstorageフォルダ内にコピーするメソッド呼び出し
            copyImageFile(imageFile, filePath);
            restaurant.setImage(hashedImageName);
        }
		
		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setLowestPrice(restaurantRegisterForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantRegisterForm.getHighestPrice());
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());		
		restaurant.setOpeningTime(restaurantRegisterForm.getOpeningTime());
		restaurant.setClosingTime(restaurantRegisterForm.getClosingTime());
		restaurant.setSeatingCapacity(restaurantRegisterForm.getSeatingCapacity());	
		
		restaurantRepository.save(restaurant);
	}

	@Transactional
	public void updateRestaurant(RestaurantEditForm restaurantEditForm , Restaurant restaurant) {
		MultipartFile imageFile = restaurantEditForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            restaurant.setImage(hashedImageName);
        }
		
		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setLowestPrice(restaurantEditForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantEditForm.getHighestPrice());
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());		
		restaurant.setOpeningTime(restaurantEditForm.getOpeningTime());
		restaurant.setClosingTime(restaurantEditForm.getClosingTime());
		restaurant.setSeatingCapacity(restaurantEditForm.getSeatingCapacity());	
		
		restaurantRepository.save(restaurant);
	}
	
	@Transactional
	public void deleteRestaurant(Restaurant restaurant) {
		restaurantRepository.delete(restaurant);
	}
	
	 // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\."); //文字列を指定した区切り文字で分解する

        /*送信したファイルはUUIDによりファイル名を別名に変更される
         *ファイル名の重複を防ぐため　重複すると後から送信されたほうに上書きされるため*/
        for (int i = 0; i < fileNames.length - 1; i++) { //ファイル名の区切り文字で区切った配列の要素数分ループ
            fileNames[i] = UUID.randomUUID().toString();
        }

        String hashedFileName = String.join(".", fileNames); //指定した鵜切り文字で複数文字列を結合●●.**となる

        return hashedFileName;
    }
    
    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
    //価格が正しく設定されているかどうか（最高価格が最低価格以上かどうか）をチェックする。
    public boolean isValidPrices(Integer lowestPrice,Integer highestPrice) {
    	return lowestPrice <= highestPrice;
    }
    
    public boolean isValidBusinessHours(LocalTime openingTime,LocalTime closingTime) {
    	return closingTime.isAfter(openingTime);
    }
}
