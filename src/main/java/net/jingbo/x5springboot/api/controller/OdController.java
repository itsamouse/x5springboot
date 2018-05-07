package net.jingbo.x5springboot.api.controller;

import com.alibaba.fastjson.JSONObject;
import net.jingbo.x5springboot.api.dao.EcpDao;
import net.jingbo.x5springboot.api.dto.OdCancelDto;
import net.jingbo.x5springboot.api.dto.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-5-4.
 */
@RestController
@RequestMapping("/my")
public class OdController {
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    EcpDao ecpDao;

    @PostMapping("/queryX5TableNeedCancelOds")
    public Map queryX5TableNeedCancelOds(Map<String,Object> params){
        logger.info(String.format("打印入参：%s",JSONObject.toJSONString(params,true)));
        Map map=ecpDao.queryX5TableNeedCancelOds();
        logger.info(String.format("crler返回值：%s",JSONObject.toJSONString(map,true)));
        return map;
    }

    @PostMapping("/doCancelOds")
    public Object doCancelOds(@RequestBody OdCancelDto odCancelDto){
        logger.info(String.format("入参：%s",JSONObject.toJSONString(odCancelDto,true)));
        Map map=new HashMap();
        map.put("p_handle_type","LINE");
        map.put("p_order_number",odCancelDto.getOd_number());
        map.put("p_line_number",odCancelDto.getLine_number());
        map.put("p_reason",odCancelDto.getCancel_reason());

        ecpDao.doCancelOdsProcedure(map);
        logger.info(String.format("记录取消返回信息：%s",JSONObject.toJSONString(map)));
        return new SimpleResponse(map.get("p_ret_code"),map.get("p_ret_msg"));
    }

}
