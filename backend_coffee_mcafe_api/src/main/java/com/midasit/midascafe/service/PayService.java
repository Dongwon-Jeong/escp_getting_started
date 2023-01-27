package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.PayOrderRq;
import com.midasit.midascafe.dto.ResponseData;

public interface PayService {
    ResponseData payOrder(PayOrderRq payOrderRq);
}
