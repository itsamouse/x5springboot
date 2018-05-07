package net.jingbo.x5springboot.api.dao;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017-9-21.
 */
public interface EcpDao {
    Date queryDate();

    Map queryX5TableNeedCancelOds();

    void doCancelOdsProcedure(Map map);
}
