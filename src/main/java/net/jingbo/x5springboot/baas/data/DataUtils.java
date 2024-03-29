package net.jingbo.x5springboot.baas.data;

import com.alibaba.fastjson.JSONObject;
import net.jingbo.x5springboot.baas.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class DataUtils {
    protected static Logger logger = Logger.getLogger(DataUtils.class);

    @Autowired
     DataSource ecDataSource;

    private static DataSource staticDataSource;

    @PostConstruct
    public void init(){
        logger.info(String.format("PostConstruct 注入数据源 " ));
        staticDataSource=ecDataSource;
    }

    /**
     * 获取数据源连接
     *
     * @return
     * @throws SQLException
     * @throws NamingException
     */
    public static Connection getConnection() {
        InitialContext initCtx;
        try {
//            initCtx = new InitialContext();
//            DataSource ds = (DataSource) initCtx.lookup(name);

            DataSource ds = staticDataSource ;
            logger.info(String.format("获取数据源连接 " ));
            Connection conn = ds.getConnection();
            return conn;
        } catch (Exception e) {
            String msg = "获取数据库 连接失败，可能原因：数据库已经关闭或者数据源配置错误";
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    /**
     * SQL数据查询，按ResultSet的列定义转换并返回Table，支持分页。应从前端传入完整列定义（Baas.getDataColumns(data)）， 以解决oracle等数据库不区分date、time、datetime，导致的数据格式转换问题；兼容了以前只传入列名字符串（data.getColumnIDs()）的写法，但是已不再推荐。
     *
     * @param conn
     * @param sql
     * @param params  SQL中问号对应的参数值，按顺序匹配
     * @param columns 列定义
     * @param offset  偏移行，null则不分页
     * @param limit   行数，null则不分页
     * @return
     * @throws SQLException
     */
    public static Table queryData(Connection conn, String sql, List<Object> params, Object columns, Integer offset, Integer limit) {
        if (limit != null && offset != null) {
            if (isMysql(conn)) {
                sql += " LIMIT " + offset + "," + limit;
            } else if (isOracle(conn)) {
                sql = String.format("SELECT * FROM (SELECT rownum no___, A___.* FROM (%s) A___ WHERE rownum <= %d) WHERE no___ > %d", sql, offset + limit, offset);
            }
        }
//		System.out.println(sql);

        if (logger.isDebugEnabled())
            logger.debug("queryData SQL:" + sql + ", params:" + params);

        PreparedStatement pstat;
        try {
            pstat = conn.prepareStatement(sql);
            try {
                if (params != null) {
                    for (int i = 0, len = params.size(); i < len; i++) {
                        pstat.setObject(i + 1, params.get(i));
                    }
                }
                ResultSet rs = pstat.executeQuery();
                if (limit != null && offset != null && !isMysql(conn) && !isOracle(conn)) {
                    for (int i = 0; i < offset; i++) {
                        rs.next();
                    }
                }
                Table table = null;
                if (columns instanceof JSONObject) {
                    table = Transform.createTableByColumnsDefine((JSONObject) columns);
                } else {
                    table = Transform.createTableByResultSet(rs, (String) columns);
                }
                Transform.loadRowsFromResultSet(table, rs, limit);
                return table;
            } finally {
                pstat.close();
            }
        } catch (SQLException e) {
            String msg = "SQL执行失败，SQL:" + sql;
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    /**
     * 单表数据查询，按ResultSet的列定义转换返回Table，支持分页，当偏移行等于零时自动返回总行数。应从前端传入完整列定义（Baas.getDataColumns(data)）， 以解决oracle等数据库不区分date、time、datetime，导致的数据格式转换问题；兼容了以前只传入列名字符串（data.getColumnIDs()）的写法，但是已不再推荐。
     *
     * @param conn
     * @param tableName 表名
     * @param columns   列定义
     * @param filters   过滤条件列表
     * @param orderBy   SQL的orderBy部分
     * @param params    SQL中问号对应的参数值列表
     * @param offset    偏移行
     * @param limit     行数
     * @return
     * @throws SQLException
     */
    public static Table queryData(Connection conn, String tableName, Object columns, List<String> filters, String orderBy,
                                  List<Object> params, Integer offset, Integer limit) {
        String format = "SELECT %s FROM %s %s %s ";

        String where = (filters != null && filters.size() > 0) ? " WHERE " + DataUtils.arrayJoin(filters.toArray(), "(%s)", " AND ") : "";
        orderBy = !Utils.isEmptyString(orderBy) ? " ORDER BY " + orderBy : "";

        String sql = String.format(format, "*", tableName, where, orderBy);
        Table table = null;
        table = queryData(conn, sql.toString(), params, columns, offset, limit);
        if (offset != null && offset.equals(0)) {
            String sqlTotal = String.format(format, "COUNT(*)", tableName, where, "");
            Object total = DataUtils.getValueBySQL(conn, sqlTotal.toString(), params);
            table.setTotal(Integer.parseInt(total.toString()));
        }
        return table;
    }

    private static boolean isMysql(Connection conn) {
        try {
            return conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf("mysql") != -1;
        } catch (SQLException e) {
            String msg = "判断数据库类型失败！";
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    private static boolean isOracle(Connection conn) {
        try {
            return conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1;
        } catch (SQLException e) {
            String msg = "判断数据库类型失败！";
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    /**
     * 执行SQL查询，返回第一行第一列的值
     *
     * @param conn
     * @param sql
     * @param params SQL中问号对应的参数值，按顺序匹配
     * @return
     * @throws SQLException
     */
    public static Object getValueBySQL(Connection conn, String sql, List<Object> params) {
        try {
            if (logger.isDebugEnabled())
                logger.debug("getValueBySQL SQL:" + sql + ", params:" + params);

            PreparedStatement pstat = conn.prepareStatement(sql);
            try {
                if (params != null) {
                    for (int i = 0, len = params.size(); i < len; i++) {
                        pstat.setObject(i + 1, params.get(i));
                    }
                }
                ResultSet rs = pstat.executeQuery();
                if (rs.next()) {
                    return rs.getObject(1);
                } else {
                    return null;
                }
            } finally {
                pstat.close();
            }
        } catch (SQLException e) {
            String msg = "SQL执行失败，SQL:" + sql + ", params:" + params;
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    /**
     * 保存Table数据，自动产生并执行基于where key规则的增删改SQL语句
     *
     * @param conn
     * @param table
     * @throws SQLException
     */
    public static void saveData(Connection conn, Table table) {
        saveData(conn, table, "");
    }

    /**
     * 保存Table数据，并指定列范围
     *
     * @param conn
     * @param table
     * @param columns 列范围
     * @throws SQLException
     */
    public static void saveData(Connection conn, Table table, String columns) {
        saveData(conn, table, Utils.isEmptyString(columns) ? null : Arrays.asList(columns.split(",")));
    }

    /**
     * 保存Table数据，并指定列范围
     *
     * @param conn
     * @param table
     * @param columns 列范围
     * @throws SQLException
     */
    public static void saveData(Connection conn, Table table, Collection<String> columns) {
        if (columns == null) {
            columns = new ArrayList<String>();
            columns.addAll(table.getColumnNames());
        }
        String idColumn = table.getIDColumn();

        PreparedStatement newStat = null;
        String newSQL = createNewSQL(table, columns);
        try {
            newStat = conn.prepareStatement(newSQL);
            for (Row row : table.getRows(RowState.NEW)) {
                int i = 1;
                for (String column : columns) {
                    newStat.setObject(i, row.getValue(column));
                    i++;
                }
                if (logger.isDebugEnabled())
                    logger.debug("saveData:" + newStat.toString());
                newStat.execute();
            }
        } catch (SQLException e) {
            String msg = "SQL执行失败，" + newStat.toString();
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
        PreparedStatement editStat = null;
        String updateSQL = createUpdateSQL(table, columns);
        try {
            editStat = conn.prepareStatement(updateSQL);
            for (Row row : table.getRows(RowState.EDIT)) {
                int i = 1;
                for (String column : columns) {
                    editStat.setObject(i, row.getValue(column));
                    i++;
                }
                editStat.setObject(columns.size() + 1, row.isChanged(idColumn) ? row.getOldValue(table.getIDColumn()) : row.getValue(idColumn));
                if (logger.isDebugEnabled())
                    logger.debug("saveData:" + editStat.toString());
                editStat.execute();
            }
        } catch (SQLException e) {
            String msg = "SQL执行失败，" + editStat.toString();
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
        PreparedStatement deleteStat = null;
        String deleteSQL = createDeleteSQL(table);
        try {
            deleteStat = conn.prepareStatement(deleteSQL);
            for (Row row : table.getRows(RowState.DELETE)) {
                deleteStat.setObject(1, row.isChanged(idColumn) ? row.getOldValue(table.getIDColumn()) : row.getValue(idColumn));
                if (logger.isDebugEnabled())
                    logger.debug("saveData:" + deleteStat.toString());
                deleteStat.execute();
            }
        } catch (SQLException e) {
            String msg = "SQL执行失败，" + deleteStat.toString();
            throw new net.jingbo.x5springboot.baas.data.sql.SQLException(msg, e);
        }
    }

    private static String createNewSQL(Table table, Collection<String> columns) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + table.getTableName());
        sql.append(" (" + arrayJoin(columns.toArray(), "%s", ",") + ") ");
        sql.append(" VALUES (" + arrayJoin(columns.toArray(), "?", ",") + ") ");
        return sql.toString();
    }

    private static String createUpdateSQL(Table table, Collection<String> columns) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE " + table.getTableName());
        sql.append(" SET " + arrayJoin(columns.toArray(), "%s=?", ",") + " ");
        sql.append(" WHERE " + table.getIDColumn() + "=? ");
        return sql.toString();
    }

    private static String createDeleteSQL(Table table) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM " + table.getTableName());
        sql.append(" WHERE " + table.getIDColumn() + "=? ");
        return sql.toString();
    }

    /**
     * 将一个数组连接为格式化字符串
     *
     * @param objects
     * @param format    对数组元素的格式化，例如：'%s'为每个数组元素增加单引号、(%s = ?)将每个数组元素格式化为括号中等于问号
     * @param separator 分隔符，例如：,、OR、AND
     * @return
     */
    public static String arrayJoin(Object[] objects, String format, String separator) {
        StringBuffer buf = new StringBuffer();
        for (Object o : objects) {
            if (buf.length() > 0) {
                buf.append(separator);
            }
            buf.append(String.format(format, o.toString()));
        }
        return buf.toString();
    }

    public static void writeJsonToResponse(ServletResponse paramServletResponse, JSONObject paramJSONObject)
            throws IOException {
        paramServletResponse.setContentType("text/html;charset=utf-8");
        paramServletResponse.getWriter().write(paramJSONObject.toJSONString());
    }

    public static void writeTableToResponse(ServletResponse paramServletResponse, Table paramTable)
            throws IOException {
        JSONObject localJSONObject = Transform.tableToJson(paramTable);
        writeJsonToResponse(paramServletResponse, localJSONObject);
    }
}
