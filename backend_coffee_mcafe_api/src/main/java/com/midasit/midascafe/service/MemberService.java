package com.midasit.midascafe.service;

import com.midasit.midascafe.controller.rqrs.RegisterMemberRq;

import java.util.List;

public interface MemberService {
    int registerMember(RegisterMemberRq registerMemberRq);
    int deleteMember(String phone);
}
