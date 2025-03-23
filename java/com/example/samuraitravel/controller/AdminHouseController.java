package com.example.samuraitravel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.service.HouseService;

@Controller
@RequestMapping("/admin/houses")
/*ルートパスの基準値を設定する今回のように設定すれば
 *各メソッドに共通のパス「/admin/houses」を繰り返し書かなくてもよい*/
public class AdminHouseController {
    private final HouseService houseService;

    public AdminHouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping
    //@PageableDefaultをつけることでページ情報のデフォルトを任意に設定できる 何ページ目か→何件/1P→並び変え基準→昇順/降順
    //引数に@RequestParamアノテーション リクエストパラメータの値をその引数にバインドする required属性は不可欠化どうか、falseにすることで検索を使わないときもエラー回避
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
            Model model)
{  
    	//ページネーションされたデータを取得するには引数としてpageableが必要　戻り値はpage型になる
    	Page<House> housePage;

    	//keywordパラメータが存在するかどうかで処理を分ける
        if (keyword != null && !keyword.isEmpty()) {
        	//ある時、あいまい検索
            housePage = houseService.findHousesByNameLike(keyword, pageable);
        } else {
        	//ない時、全件検索
            housePage = houseService.findAllHouses(pageable);
        } 

        //ビューにkeyword（文字列）を渡す この値をフォームの初期値に設定したり、ページリンクのパラメータに設定したりするため使用
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);

        return "admin/houses/index";
    }
    
    @GetMapping("/{id}")
    /*@PathVariableアノテーション URLの一部をその引数にバインドする
     *URLの一部を変数のように扱い、コントローラでその値を利用できる*/
    public String show(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        Optional<House> optionalHouse  = houseService.findHouseById(id);

        //民宿が存在しない場合の処理 その時は民宿一覧へリダイレクト
        if (optionalHouse.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

            return "redirect:/admin/houses";
        }

        //Optional<House>型ではエンティティの各フィールドへアクセスできない getめっそどでHouse型へ変換しビューへ渡す
        House house = optionalHouse.get();
        model.addAttribute("house", house);

        return "admin/houses/show";
    }    
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("houseRegisterForm", new HouseRegisterForm());

        return "admin/houses/register";
    }  
    
    @PostMapping("/create") //フォームに対してバリデーションを行い、エラーがあれば民宿登録ページを再表示 無ければcreateHouseメソッドを実行
    public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        if (bindingResult.hasErrors()) {
            model.addAttribute("houseRegisterForm", houseRegisterForm);

            return "admin/houses/register";
        }

        houseService.createHouse(houseRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");

        return "redirect:/admin/houses";
    }  
    
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        Optional<House> optionalHouse = houseService.findHouseById(id);

        if (optionalHouse.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

            return "redirect:/admin/houses";
        }

        House house = optionalHouse.get();
        //フォームをインスタンス化
        HouseEditForm houseEditForm = new HouseEditForm(house.getName(), null, house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());

        model.addAttribute("house", house);
        //生成したインスタンスをビューに渡す
        model.addAttribute("houseEditForm", houseEditForm);

        return "admin/houses/edit";
    } 
    
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated HouseEditForm houseEditForm,
                         BindingResult bindingResult,
                         @PathVariable(name = "id") Integer id,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        Optional<House> optionalHouse = houseService.findHouseById(id);

        if (optionalHouse.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

            return "redirect:/admin/houses";
        }

        House house = optionalHouse.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("house", house);
            model.addAttribute("houseEditForm", houseEditForm);

            return "admin/houses/edit";
        }

        houseService.updateHouse(houseEditForm, house);
        redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");

        return "redirect:/admin/houses";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<House> optionalHouse = houseService.findHouseById(id);

        if (optionalHouse.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");

            return "redirect:/admin/houses";
        }

        House house = optionalHouse.get();
        houseService.deleteHouse(house);
        redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");

        return "redirect:/admin/houses";
    }
}