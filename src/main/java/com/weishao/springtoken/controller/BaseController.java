package com.weishao.springtoken.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回出口
 * @author tang
 *
 */
public class BaseController {
	
    protected Map<String,Object> success(Object data) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("errcode",0);
        map.put("errmsg","success");
        map.put("data", data);
        return map;
    }

    protected Map<String, Object> failed(long errno,String reason) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("errcode",errno);
        map.put("errmsg",reason);
        return map;
    }
}
