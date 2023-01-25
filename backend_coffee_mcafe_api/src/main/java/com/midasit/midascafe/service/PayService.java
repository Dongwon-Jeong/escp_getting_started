package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;

public interface PayService {
    int payOrder(PayOrderRq payOrderRq);
}
