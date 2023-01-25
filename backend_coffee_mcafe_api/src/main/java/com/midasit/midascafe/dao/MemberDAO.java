package com.midasit.midascafe.dao;

import com.midasit.midascafe.dto.PostResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface MemberDAO {
    PostResponse registerMember(String phone, String name, String cellId);
    JSONObject getMemberById(String uuid);
    String getCellIdByPhone(String phone);
    String getNameByPhone(String phone);
    JSONArray getMembers();
}
