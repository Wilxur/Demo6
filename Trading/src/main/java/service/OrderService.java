package service;

// OrderService.java


import dao.OrderDAO;
import pojo.Order;
import java.util.List;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();

    // 创建订单
    public boolean createOrder(Order order) {
        return orderDAO.addOrder(order);
    }

    // 更新订单状态
    public boolean updateOrderStatus(Integer orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }

    // 根据ID获取订单
    public Order getOrderById(Integer id) {
        return orderDAO.getOrderById(id);
    }

    // 获取用户作为买家的订单
    public List<Order> getOrdersByBuyerId(Integer buyerId) {
        return orderDAO.getOrdersByBuyerId(buyerId);
    }

    // 获取用户作为卖家的订单
    public List<Order> getOrdersBySellerId(Integer sellerId) {
        return orderDAO.getOrdersBySellerId(sellerId);
    }

    // 获取订单详情
    public Order getOrderDetail(Integer orderId) {
        return orderDAO.getOrderDetail(orderId);
    }
}