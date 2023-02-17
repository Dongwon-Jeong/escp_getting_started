package com.midasit.midascafe.controller;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dao.MenuDAO;
import com.midasit.midascafe.dto.ResponseData;
import com.midasit.midascafe.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


@Api(tags = "Root Controller")
@RestController
@RequiredArgsConstructor  // 생성자 주입
public class RootController {
    private final MenuDAO menuDAO;
    private final PayService payService;
    @Operation(summary = "졸음 방지용", description = "서버 졸음 방지")
    @GetMapping("/caffeine")
    public ResponseEntity<Void> caffeine() {
        return ResponseEntity.ok(null);
    }


    // 메뉴 추가를 위한 임시 메서드
    @Operation(summary = "메뉴 추가를 위한 임시 메서드", description = "메뉴 추가를 위한 임시 메서드")
    @PostMapping("/menu/database/init")
    public ResponseEntity<Void> initDb() throws IOException, ParseException {
        //ClassPathResource initDbResource = new ClassPathResource("init_db.json");
        JSONParser parser = new JSONParser();
        Reader reader;
        reader = new FileReader("src/main/resources/init_db.json");
        JSONObject initDBJson = (JSONObject) parser.parse(reader);
        JSONArray page = (JSONArray) ((JSONObject) initDBJson.get("PAGELIST")).get("PAGE");
        for(int type = 0; type < page.size(); type++) {
            JSONArray orderButtonComp = (JSONArray) ((JSONObject) ((JSONArray) ((JSONObject) ((JSONObject) page.get(type)).get("LISTCOMP")).get("LISTROW")).get(0)).get("ORDERBUTTONCOMP");
            for(int menuIdx = 0; menuIdx < orderButtonComp.size(); menuIdx++) {
                JSONObject menuObj = (JSONObject) orderButtonComp.get(menuIdx);
                String name = (String) menuObj.get("menutitle");
                String code = (String) menuObj.get("item_code");
                int unitPrice = ((Long) menuObj.get("price")).intValue();
                //menuDAO.registerMenu(name, code, unitPrice, type);
            }
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "음료 주문", description = "주문을 취합하여 음료를 주문합니다.")
    @PostMapping("/fake-pay")
    public ResponseEntity<String> fakePay(@RequestBody @Valid PayOrderRq payOrderRq) {
        ResponseData responseData = payService.fakePay(payOrderRq);
        int statusCode = responseData.getStatusCode();
        if (statusCode == 200) {
            return ResponseEntity.ok(responseData.getResponseData());
        } else if (statusCode == 404) {
            return new ResponseEntity<>(responseData.getResponseData() , HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("주문에 실패하였습니다.\n" + responseData.getResponseData(), HttpStatus.valueOf(statusCode));
        }
    }
}
