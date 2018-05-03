package net.jingbo.x5springboot.interceptor;

import net.jingbo.x5springboot.baas.data.Table;
import net.jingbo.x5springboot.baas.data.Transform;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018-3-13.
 */
@Intercepts(@Signature(method="handleResultSets", type=ResultSetHandler.class, args={Statement.class}))
public class Wex5ResultSetHandlerInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target=invocation.getTarget();
        if (target instanceof DefaultResultSetHandler){
            DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler)invocation.getTarget();
            Object[] args = invocation.getArgs();
            Statement stmt =  (Statement) args[0];
            MetaObject defaultResultSetHandlerMetaObject = SystemMetaObject.forObject(defaultResultSetHandler);
            MappedStatement mappedStatement = (MappedStatement)defaultResultSetHandlerMetaObject.getValue("mappedStatement");

            if(mappedStatement!=null && !StringUtils.isEmpty(mappedStatement.getId()) ){
                // bugfix :  .  点号是特殊字符，分隔时需带上双斜杠
                String mids[]=mappedStatement.getId().split("\\.");

                if(mids!=null && mids.length>0 && mids[mids.length-1].startsWith("queryX5Table")){
                    Configuration configuration=(Configuration)defaultResultSetHandlerMetaObject.getValue("configuration");
                    ResultSetWrapper rsw = getFirstResultSet(stmt, configuration);

                    ResultSet rs = rsw.getResultSet();
                    final Table table= Transform.resultSetToTable(rs,"",null);
                    table.setIDColumn("ID");

                    List list=new ArrayList();
                    list.add(Transform.tableToJson(table));
                    return  list ;
                }else if(mids!=null && mids.length>0 && mids[mids.length-1].startsWith("queryX5Tree")){
                    Configuration configuration=(Configuration)defaultResultSetHandlerMetaObject.getValue("configuration");
                    ResultSetWrapper rsw = getFirstResultSet(stmt, configuration);

                    ResultSet rs = rsw.getResultSet();
                    final Table table=Transform.resultSetToTable(rs,"",null);
                    table.setIDColumn("ID");

                    List list=new ArrayList();
                    list.add(Transform.tableToTreeJson(table, "PID"));
                    return  list ;
                }
            }

        }
        //如果没有进行拦截处理，则执行默认逻辑
        return invocation.proceed();
    }

    private ResultSetWrapper getFirstResultSet(Statement stmt, Configuration configuration) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            // move forward to get the first resultset in case the driver
            // doesn't return the resultset as the first result (HSQLDB 2.1)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    // no more results. Must be no resultset
                    break;
                }
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
