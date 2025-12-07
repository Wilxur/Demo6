package dao;

import pojo.Item;
import pojo.User;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemDAO extends BaseDAO {

    // 添加物品
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items(name, description, price, user_id, category, status, create_time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return update(sql,
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getUserId(),
                item.getCategory(),
                item.getStatus(),
                new java.sql.Timestamp(System.currentTimeMillis())) > 0;
    }

    // 更新物品
    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, price = ?, category = ?, status = ?, update_time = ? WHERE id = ? AND user_id = ?";
        return update(sql,
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.getStatus(),
                new java.sql.Timestamp(System.currentTimeMillis()),
                item.getId(),
                item.getUserId()) > 0;
    }

    // 删除物品（逻辑删除）
    public boolean deleteItem(Integer itemId, Integer userId) {
        String sql = "UPDATE items SET status = '0' WHERE id = ? AND user_id = ?";
        return update(sql, itemId, userId) > 0;
    }

    // 根据ID查询物品
    public Item getItemById(Integer id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        return queryForOne(Item.class, sql, id);
    }

    // 模糊搜索物品
    public List<Item> searchItems(String keyword) {
        String sql = "SELECT * FROM items WHERE status = '1' AND (name LIKE ? OR description LIKE ?) ORDER BY create_time DESC";
        return queryForList(Item.class, sql,
                "%" + keyword + "%",
                "%" + keyword + "%");
    }

    // 获取用户发布的物品
    public List<Item> getItemsByUserId(Integer userId) {
        String sql = "SELECT * FROM items WHERE user_id = ? ORDER BY create_time DESC";
        return queryForList(Item.class, sql, userId);
    }

    // 获取所有在售物品
    public List<Item> getAllItemsOnSale() {
        String sql = "SELECT i.*, u.username, u.phone, " +
                "ub.username as buyer_username, ub.phone as buyer_phone " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "LEFT JOIN users ub ON i.buyer_id = ub.id " +
                "WHERE i.status = '1' ORDER BY i.create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Item> items = new java.util.ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setUserId(rs.getInt("user_id"));
                item.setCategory(rs.getString("category"));
                item.setStatus(rs.getString("status"));
                item.setCreateTime(rs.getTimestamp("create_time"));
                item.setBuyerId(rs.getInt("buyer_id"));

                // 设置卖家信息
                User seller = new User();
                seller.setUsername(rs.getString("username"));
                seller.setPhone(rs.getString("phone"));
                item.setSeller(seller);

                // 设置买家信息（如果已售出）
                if (rs.getString("buyer_username") != null) {
                    User buyer = new User();
                    buyer.setUsername(rs.getString("buyer_username"));
                    buyer.setPhone(rs.getString("buyer_phone"));
                    item.setBuyer(buyer);
                }

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return items;
    }

    // 购买物品
    public boolean buyItem(Integer itemId, Integer buyerId) {
        String sql = "UPDATE items SET buyer_id = ?, status = '0' WHERE id = ? AND status = '1'";
        return update(sql, buyerId, itemId) > 0;
    }

    // 获取已购买的物品
    public List<Item> getBoughtItemsByUserId(Integer userId) {
        String sql = "SELECT i.*, u.username as seller_username, u.phone as seller_phone " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.buyer_id = ? AND i.status = '0' " +
                "ORDER BY i.create_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Item> items = new java.util.ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setUserId(rs.getInt("user_id"));
                item.setCategory(rs.getString("category"));
                item.setStatus(rs.getString("status"));
                item.setCreateTime(rs.getTimestamp("create_time"));
                item.setBuyerId(rs.getInt("buyer_id"));

                // 设置卖家信息
                User seller = new User();
                seller.setUsername(rs.getString("seller_username"));
                seller.setPhone(rs.getString("seller_phone"));
                item.setSeller(seller);

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return items;
    }
}