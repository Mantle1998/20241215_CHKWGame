package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Brand;
import com.example.demo.model.BrandRepository;
import com.example.demo.model.Item;
import com.example.demo.model.ItemRepository;

@Controller
public class HomePageController {
	
	 @Autowired
	    private ItemRepository itemRepository;
	 
	 @Autowired
	    private BrandRepository brandRepository;
	
	// 顯示商城首頁頁面 (http://localhost:8080/homePage)
    @GetMapping("/homePage")
    public String showhomePage() {
        return "homePage";
    }
    
    // 顯示商城搜尋頁面 (http://localhost:8080/itemSearch)
    @GetMapping("/itemSearch")
    public String showitemSearch() {
        return "itemSearch";
    }
    
    // 顯示商城商品頁面 (http://localhost:8080/itemDisplay)
    @GetMapping("/itemDisplay")
    public String showitemDisplay() {
        return "itemDisplay";
    }
    
    @GetMapping("/itemDisplay/{id}")
    public String showItemDisplay(@PathVariable("id") int itemId, Model model) {
        // 使用 Repository 獲取商品資料
        Item item = itemRepository.findById(itemId).orElse(null);

        if (item == null) {
            return "redirect:/homePage"; // 如果商品不存在，返回首頁
        }

        model.addAttribute("item", item); // 將商品資訊傳遞到前端
        return "itemDisplay";
    }
    
    
    //(http://localhost:8080/itemDisplay)
 // 取得所有品牌圖片和名稱，回傳 JSON 格式
    @GetMapping("/brands")
    @ResponseBody
    public List<BrandSimpleResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brand -> new BrandSimpleResponse(
                        brand.getBrandName(),
                        encodeImage(brand.getBrandPhoto())
                ))
                .collect(Collectors.toList());
    }

    // 將圖片轉為 Base64 編碼，並判斷 MIME 類型
    private String encodeImage(byte[] image) {
        if (image == null) {
            return null; // 如果圖片為空，返回 null
        }

        String mimeType = isPngFormat(image) ? "image/png" : "image/jpeg";
        return "data:" + mimeType + ";base64," + java.util.Base64.getEncoder().encodeToString(image);
    }

    // 判斷圖片是否為 PNG 格式
    private boolean isPngFormat(byte[] image) {
        return image.length > 4 &&
               image[0] == (byte) 0x89 &&
               image[1] == (byte) 0x50 &&
               image[2] == (byte) 0x4E &&
               image[3] == (byte) 0x47;
    }

    // 簡化品牌數據的 DTO
    private static class BrandSimpleResponse {
        private String brandName;
        private String brandPhoto;

        public BrandSimpleResponse(String brandName, String brandPhoto) {
            this.brandName = brandName;
            this.brandPhoto = brandPhoto;
        }

        // Getters
        public String getBrandName() {
            return brandName;
        }

        public String getBrandPhoto() {
            return brandPhoto;
        }
    }
    
    //(http://localhost:8080/items/latest15)
    // 取得最新 15 項商品，回傳 JSON 格式
    @GetMapping("/items/latest15")
    @ResponseBody
    public List<ItemSimpleResponse> getLatestItems() {
        return itemRepository.findTop15ByOrderByItemIdDesc().stream()
                .map(item -> new ItemSimpleResponse(
                        item.getItemId(),
                        item.getItemName(),
                        item.getItemPrice(),
                        item.getItemPhoto().isEmpty() ? null : encodeImage(item.getItemPhoto().get(0).getPhotoFile()) // 獲取第一張圖片
                ))
                .collect(Collectors.toList());
    }
    
    

    // 簡化商品數據的 DTO
    private static class ItemSimpleResponse {
        private int itemId;
        private String itemName;
        private BigDecimal itemPrice;
        private String itemPhoto;

        public ItemSimpleResponse(int itemId, String itemName, BigDecimal itemPrice, String itemPhoto) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.itemPhoto = itemPhoto;
        }

        // Getters
        public int getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public BigDecimal getItemPrice() {
            return itemPrice;
        }

        public String getItemPhoto() {
            return itemPhoto;
        }
    }
}