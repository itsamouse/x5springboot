package net.jingbo.x5springboot.api.controller;

import com.alibaba.fastjson.JSONObject;
import net.jingbo.x5springboot.api.dao.EcpDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
