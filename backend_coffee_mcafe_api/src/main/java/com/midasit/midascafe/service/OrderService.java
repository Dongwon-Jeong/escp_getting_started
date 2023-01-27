package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterOrderRq;

public interface OrderService {
    int registerOrder(RegisterOrderRq registerOrderRq);
    int deleteOrder(String phone);
    boolean hasOrder(String phone);
}
