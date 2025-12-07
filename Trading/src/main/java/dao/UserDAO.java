package dao;


import pojo.User;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends BaseDAO {

    // 添加用户
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, email, phone, create_time) VALUES(?, ?, ?, ?, ?)";
        return update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                new java.sql.Timestamp(System.currentTimeMillis())) > 0;
    }

    // 根据用户名查询用户
    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, password, email, phone, create_time FROM users WHERE username = ?";
        return queryForOne(User.class, sql, username);
    }

    // 根据ID查询用户
    public User getUserById(Integer id) {
        String sql = "SELECT id, username, password, email, phone, create_time FROM users WHERE id = ?";
        return queryForOne(User.class, sql, id);
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return false;
    }
}