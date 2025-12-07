package controller;

// OrderServlet.java


import pojo.Order;
import pojo.User;
import pojo.Item;
import service.OrderService;
import service.ItemService;
import util.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "OrderServlet", value = "/order")
public class OrderServlet extends HttpServlet {
    private OrderService orderService = new OrderService();
    private ItemService itemService = new ItemService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/item?action=list");
            return;
        }

        switch (action) {
            case "buy":
                buyItem(request, response);
                break;
            case "create":
                createOrder(request, response);
                break;
            case "confirm":
                confirmOrder(request, response);
                break;
            case "myOrders":
                myOrders(request, response);
                break;
            case "mySales":
                mySales(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/item?action=list");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    // 跳转到购买页面
    private void buyItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/item?action=list");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            Item item = itemService.getItemById(itemId);

            if (item == null) {
                request.setAttribute("msg", "物品不存在");
                response.sendRedirect(request.getContextPath() + "/item?action=list");
                return;
            }

            // 检查物品是否在售
            if (!Constants.ITEM_STATUS_ON_SALE.equals(item.getStatus())) {
                request.setAttribute("msg", "该物品已售出");
                response.sendRedirect(request.getContextPath() + "/item?action=list");
                return;
            }

            // 检查不能购买自己的物品
            if (item.getUserId().equals(user.getId())) {
                request.setAttribute("msg", "不能购买自己发布的物品");
                response.sendRedirect(request.getContextPath() + "/item?action=list");
                return;
            }

            request.setAttribute("item", item);
            request.getRequestDispatcher("/views/buy.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "物品ID格式不正确");
            response.sendRedirect(request.getContextPath() + "/item?action=list");
        }
    }

    // 创建订单
    private void createOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User buyer = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (buyer == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr == null || itemIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/item?action=list");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            Item item = itemService.getItemById(itemId);

            if (item == null) {
                request.setAttribute("msg", "物品不存在");
                response.sendRedirect(request.getContextPath() + "/item?action=list");
                return;
            }

            // 获取购买信息
            String contactInfo = request.getParameter("contactInfo");
            String note = request.getParameter("note");

            if (contactInfo == null || contactInfo.trim().isEmpty()) {
                request.setAttribute("msg", "联系方式不能为空");
                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/buy.jsp").forward(request, response);
                return;
            }

            // 创建订单
            Order order = new Order();
            order.setItemId(itemId);
            order.setBuyerId(buyer.getId());
            order.setSellerId(item.getUserId());
            order.setPrice(item.getPrice());
            order.setContactInfo(contactInfo.trim());
            order.setNote(note != null ? note.trim() : "");
            order.setStatus("pending");

            if (orderService.createOrder(order)) {
                // 更新物品状态为已售出
                itemService.buyItem(itemId, buyer.getId());

                request.setAttribute("msg", "购买成功，请等待卖家联系您！");
                request.getRequestDispatcher("/views/buySuccess.jsp").forward(request, response);
            } else {
                request.setAttribute("msg", "购买失败，请稍后重试");
                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/buy.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "物品ID格式不正确");
            response.sendRedirect(request.getContextPath() + "/item?action=list");
        }
    }

    // 确认订单（卖家确认）
    private void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/order?action=mySales");
            return;
        }

        try {
            Integer orderId = Integer.parseInt(orderIdStr);
            Order order = orderService.getOrderById(orderId);

            if (order == null || !order.getSellerId().equals(user.getId())) {
                request.setAttribute("msg", "订单不存在或您没有权限操作");
                response.sendRedirect(request.getContextPath() + "/order?action=mySales");
                return;
            }

            if (orderService.updateOrderStatus(orderId, "completed")) {
                request.setAttribute("msg", "订单已确认完成");
            } else {
                request.setAttribute("msg", "订单确认失败");
            }

            response.sendRedirect(request.getContextPath() + "/order?action=mySales");
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "订单ID格式不正确");
            response.sendRedirect(request.getContextPath() + "/order?action=mySales");
        }
    }

    // 我的订单（作为买家）
    private void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        request.setAttribute("orders", orderService.getOrdersByBuyerId(user.getId()));
        request.getRequestDispatcher("/views/myOrders.jsp").forward(request, response);
    }

    // 我的销售（作为卖家）
    private void mySales(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        request.setAttribute("orders", orderService.getOrdersBySellerId(user.getId()));
        request.getRequestDispatcher("/views/mySales.jsp").forward(request, response);
    }
}
