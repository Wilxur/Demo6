package dao;



import pojo.Order;
import pojo.User;
import pojo.Item;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends BaseDAO {

    // 创建订单
    public boolean addOrder(Order order) {
        String sql = "INSERT INTO orders(item_id, buyer_id, seller_id, price, contact_info, note) VALUES(?, ?, ?, ?, ?, ?)";
        return update(sql,
                order.getItemId(),
                order.getBuyerId(),
                order.getSellerId(),
                order.getPrice(),
                order.getContactInfo(),
                order.getNote()) > 0;
    }

    // 更新订单状态
    public boolean updateOrderStatus(Integer orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        return update(sql, status, orderId) > 0;
    }

    // 根据ID获取订单
    public Order getOrderById(Integer id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        return queryForOne(Order.class, sql, id);
    }

    // 获取用户作为买家的订单
    public List<Order> getOrdersByBuyerId(Integer buyerId) {
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY order_time DESC";
        return queryForList(Order.class, sql, buyerId);
    }

    // 获取用户作为卖家的订单
    public List<Order> getOrdersBySellerId(Integer sellerId) {
        String sql = "SELECT * FROM orders WHERE seller_id = ? ORDER BY order_time DESC";
        return queryForList(Order.class, sql, sellerId);
    }

    // 获取物品的订单
    public List<Order> getOrdersByItemId(Integer itemId) {
        String sql = "SELECT * FROM orders WHERE item_id = ? ORDER BY order_time DESC";
        return queryForList(Order.class, sql, itemId);
    }

    // 获取订单详情，包括关联的物品和用户信息
    public Order getOrderDetail(Integer orderId) {
        String sql = "SELECT o.*, i.name as item_name, i.description as item_description, " +
                "u1.username as buyer_username, u1.phone as buyer_phone, " +
                "u2.username as seller_username, u2.phone as seller_phone " +
                "FROM orders o " +
                "LEFT JOIN items i ON o.item_id = i.id " +
                "LEFT JOIN users u1 ON o.buyer_id = u1.id " +
                "LEFT JOIN users u2 ON o.seller_id = u2.id " +
                "WHERE o.id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Order order = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setItemId(rs.getInt("item_id"));
                order.setBuyerId(rs.getInt("buyer_id"));
                order.setSellerId(rs.getInt("seller_id"));
                order.setPrice(rs.getBigDecimal("price"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setStatus(rs.getString("status"));
                order.setContactInfo(rs.getString("contact_info"));
                order.setNote(rs.getString("note"));

                // 设置关联对象
                Item item = new Item();
                item.setName(rs.getString("item_name"));
                item.setDescription(rs.getString("item_description"));
                order.setItem(item);

                User buyer = new User();
                buyer.setUsername(rs.getString("buyer_username"));
                buyer.setPhone(rs.getString("buyer_phone"));
                order.setBuyer(buyer);

                User seller = new User();
                seller.setUsername(rs.getString("seller_username"));
                seller.setPhone(rs.getString("seller_phone"));
                order.setSeller(seller);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return order;
    }
}
