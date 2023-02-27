package com.midasit.midascafe.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.midasit.midascafe.dto.Member;
import com.midasit.midascafe.dto.Order;
import com.midasit.midascafe.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor  // 생성자 주입
public class OrderDAOImpl implements OrderDAO {
    private final CommonDAO commonDAO;
    private final CommonService commonService;
    private final static String URL = "https://crudapi.co.uk/api/v1/order";
    private final static String URI = "/order";

    @Override
    public ResponseEntity<String> registerOrder(Order order) {
        List<JsonNode> orderList = new ArrayList<>();
        ObjectNode data = commonService.getObjectMapper()
                .valueToTree(order);
        data.remove("_uuid");
        orderList.add(data);
        JsonNode body = commonService.getObjectMapper().valueToTree(orderList);
        ResponseEntity<String> responseEntity = commonDAO.postRequest(URI, body, String.class).block();
        return responseEntity;
    }

    @Override
    public ResponseEntity<Order> getOrder(String uuid) {
        String uri = new StringBuilder(URI).append("/").append(uuid).toString();
        return commonDAO.getRequest(uri, Order.class).block();
    }

    @Override
    public List<Order> getOrderList() {
        return commonDAO.getItemList(URI, Order.class);
    }

    @Override
    public int deleteOrder(JSONArray orderJsonArray) {
        return commonDAO.deleteItems(URL, orderJsonArray);
    }

    @Override
    public HttpStatus deleteOrder(String orderId) {
        String uri = new StringBuilder(URI).append("/").append(orderId).toString();
        return commonDAO.deleteRequest(uri, String.class).block().getStatusCode();
    }

    @Override
    public HttpStatus deleteOrders(JsonNode body) {
        return commonDAO.deleteRequestWithBody(URI, body, String.class)
                .block()
                .getStatusCode();
    }

}
