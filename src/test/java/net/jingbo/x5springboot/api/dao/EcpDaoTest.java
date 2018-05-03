package net.jingbo.x5springboot.api.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Administrator on 2018-4-26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcpDaoTest {
    @Autowired
    private EcpDao ecpDao;

    @Test
    public void testQuery(){
        System.out.println(ecpDao.queryDate());
    }

}
