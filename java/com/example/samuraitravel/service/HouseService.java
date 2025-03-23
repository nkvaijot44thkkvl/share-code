package com.example.samuraitravel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;

/*コントローラーとサービスの役割が混ざらないために
 *コントローラーからはサービスの呼び出しをします。そのサービスを通じてリポジトリへアクセスすることで
 *プログラムの可読性を向上させることができる*/
@Service
public class HouseService {
    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

 // すべての民宿をページングされた状態で取得する
    public Page<House> findAllHouses(Pageable pageable) {
        return houseRepository.findAll(pageable);
    }   
    
    // 指定されたキーワードを民宿名に含む民宿を、ページングされた状態で取得する
    public Page<House> findHousesByNameLike(String keyword, Pageable pageable) {
        return houseRepository.findByNameLike("%" + keyword + "%", pageable);
    }  
    
    // 指定したidを持つ民宿を取得する
    /*Optional nullを持つ可能性のあるオブジェクトを安全・便利に扱う方法 nullでも大丈夫*/
    public Optional<House> findHouseById(Integer id) {
        return houseRepository.findById(id);
    } 
    
    // 民宿のレコード数を取得する テスト前後のレコード数を調べ登録の成功失敗を調べるため
    public long countHouses() {
        return houseRepository.count(); //JpaReporitoryインターフェースを継承すると使用できるので定義しなくてよい
    }    
    
    // idが最も大きい民宿を取得する
    public House findFirstHouseByOrderByIdDesc() {
        return houseRepository.findFirstByOrderByIdDesc();
    }
    
    //実際の処理を記述する場所であるサービスで、メソッドの呼び出しをする
    // 指定されたキーワードを民宿名または住所に含む民宿を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable) {
        return houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + nameKeyword + "%", "%" + addressKeyword + "%", pageable);
    }

    // 指定されたキーワードを民宿名または住所に含む民宿を宿泊料金が安い順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable) {
        return houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + nameKeyword + "%", "%" + addressKeyword + "%", pageable);
    }

    // 指定されたキーワードを住所に含む民宿を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable) {
        return houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
    }

    // 指定されたキーワードを住所に含む民宿を宿泊料金が安い順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByAddressLikeOrderByPriceAsc(String area, Pageable pageable) {
        return houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
    }

    // 指定された宿泊料金以下の民宿を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable) {
        return houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
    }

    // 指定された宿泊料金以下の民宿を宿泊料金が安い順に並べ替え、ページングされた状態で取得する
    public Page<House> findHousesByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable) {
        return houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
    }

    // すべての民宿を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<House> findAllHousesByOrderByCreatedAtDesc(Pageable pageable) {
        return houseRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // すべての民宿を宿泊料金が安い順に並べ替え、ページングされた状態で取得する
    public Page<House> findAllHousesByOrderByPriceAsc(Pageable pageable) {
        return houseRepository.findAllByOrderByPriceAsc(pageable);
    } 
    
    // 作成日時が新しい順に8件の民宿を取得する
    public List<House> findTop8HousesByOrderByCreatedAtDesc() {
        return houseRepository.findTop8ByOrderByCreatedAtDesc();
    }

    // 予約数が多い順に3件の民宿を取得する
    public List<House> findTop3HousesByOrderByReservationCountDesc() {
        return houseRepository.findAllByOrderByReservationCountDesc(PageRequest.of(0, 3));
    }  
    
    @Transactional
    public void createHouse(HouseRegisterForm houseRegisterForm) {
        House house = new House();
        MultipartFile imageFile = houseRegisterForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); //元のファイル名を取得するメソッド
            //UUIDを使いファイル名変更メソッド呼び出し
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            //Java Fileクラス提供のcopyメソッドでstorageフォルダ内にコピーするメソッド呼び出し
            copyImageFile(imageFile, filePath);
            house.setImageName(hashedImageName);
        }

        house.setName(houseRegisterForm.getName());
        house.setDescription(houseRegisterForm.getDescription());
        house.setPrice(houseRegisterForm.getPrice());
        house.setCapacity(houseRegisterForm.getCapacity());
        house.setPostalCode(houseRegisterForm.getPostalCode());
        house.setAddress(houseRegisterForm.getAddress());
        house.setPhoneNumber(houseRegisterForm.getPhoneNumber());

        houseRepository.save(house);
    }
    
    @Transactional
    //ほぼcreateHouseと同じだが、第二引数で更新対象のエンティティを受け取る点が違う
    public void updateHouse(HouseEditForm houseEditForm, House house) {
        MultipartFile imageFile = houseEditForm.getImageFile();

        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            house.setImageName(hashedImageName);
        }

        house.setName(houseEditForm.getName());
        house.setDescription(houseEditForm.getDescription());
        house.setPrice(houseEditForm.getPrice());
        house.setCapacity(houseEditForm.getCapacity());
        house.setPostalCode(houseEditForm.getPostalCode());
        house.setAddress(houseEditForm.getAddress());
        house.setPhoneNumber(houseEditForm.getPhoneNumber());

        houseRepository.save(house);
    }    

    @Transactional
    public void deleteHouse(House house) {
        houseRepository.delete(house);
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
}