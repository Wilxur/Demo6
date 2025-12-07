package dao;



import util.DBUtil;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {

    // 通用更新操作（增删改）
    public int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    // 通用查询操作（返回单个对象）
    public <T> T queryForOne(Class<T> clazz, String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToObject(rs, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    // 通用查询操作（返回列表）
    public <T> List<T> queryForList(Class<T> clazz, String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToObject(rs, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    // 将ResultSet映射到Java对象
    private <T> T mapResultSetToObject(ResultSet rs, Class<T> clazz) throws Exception {
        T obj = clazz.newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object columnValue = rs.getObject(i);

            Field field = null;
            try {
                field = clazz.getDeclaredField(columnName);
            } catch (NoSuchFieldException e) {
                // 尝试驼峰命名转换
                field = clazz.getDeclaredField(convertColumnToField(columnName));
            }

            if (field != null && columnValue != null) {
                field.setAccessible(true);
                field.set(obj, columnValue);
            }
        }
        return obj;
    }

    // 数据库列名转换为Java字段名（下划线转驼峰）
    private String convertColumnToField(String columnName) {
        String[] parts = columnName.split("_");
        StringBuilder fieldName = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            fieldName.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1).toLowerCase());
        }
        return fieldName.toString();
    }
}