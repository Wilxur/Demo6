package controller;

import pojo.Item;
import pojo.User;
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

@WebServlet(name = "ItemServlet", value = "/item")
public class ItemServlet extends HttpServlet {
    private ItemService itemService = new ItemService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            // 默认跳转到物品列表
            listItems(request, response);
            return;
        }

        switch (action) {
            case "add":
                addItem(request, response);
                break;
            case "update":
                updateItem(request, response);
                break;
            case "delete":
                deleteItem(request, response);
                break;
            case "search":
                searchItems(request, response);
                break;
            case "list":
                listItems(request, response);
                break;
            case "myItems":
                myItems(request, response);
                break;
            case "detail":
                itemDetail(request, response);
                break;
            case "editForm":
                editForm(request, response);
                break;
            case "addForm":
                addForm(request, response);
                break;
            case "boughtItems":
                boughtItems(request, response);
                break;
            default:
                request.setAttribute("msg", "无效的操作");
                listItems(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 跳转到添加物品表单
     */
    private void addForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
    }

    /**
     * 发布物品
     */
    private void addItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String category = request.getParameter("category");

        // 验证输入
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("msg", "物品名称不能为空");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("price", priceStr);
            request.setAttribute("category", category);
            request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
            return;
        }

        if (priceStr == null || priceStr.trim().isEmpty()) {
            request.setAttribute("msg", "价格不能为空");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("price", priceStr);
            request.setAttribute("category", category);
            request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("msg", "价格必须大于0");
                request.setAttribute("name", name);
                request.setAttribute("description", description);
                request.setAttribute("price", priceStr);
                request.setAttribute("category", category);
                request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
                return;
            }

            Item item = new Item();
            item.setName(name.trim());
            item.setDescription(description != null ? description.trim() : "");
            item.setPrice(price);
            item.setUserId(user.getId());
            item.setCategory(category != null ? category.trim() : "其他");
            item.setStatus(Constants.ITEM_STATUS_ON_SALE);

            if (itemService.addItem(item)) {
                request.setAttribute("msg", "物品发布成功！");
                request.setAttribute("name", "");
                request.setAttribute("description", "");
                request.setAttribute("price", "");
                request.setAttribute("category", "其他");
                request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
            } else {
                request.setAttribute("msg", "物品发布失败，请稍后重试");
                request.setAttribute("name", name);
                request.setAttribute("description", description);
                request.setAttribute("price", priceStr);
                request.setAttribute("category", category);
                request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "价格格式不正确");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("price", priceStr);
            request.setAttribute("category", category);
            request.getRequestDispatcher("/views/itemAdd.jsp").forward(request, response);
        }
    }

    /**
     * 跳转到编辑物品表单
     */
    private void editForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("msg", "物品ID不能为空");
            myItems(request, response);
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            Item item = itemService.getItemById(id);

            if (item == null) {
                request.setAttribute("msg", "物品不存在");
                myItems(request, response);
                return;
            }

            // 检查用户是否有权限编辑此物品
            if (!item.getUserId().equals(user.getId())) {
                request.setAttribute("msg", "您没有权限编辑此物品");
                listItems(request, response);
                return;
            }

            request.setAttribute("item", item);
            request.getRequestDispatcher("/views/itemEdit.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "物品ID格式不正确");
            myItems(request, response);
        }
    }

    /**
     * 修改物品
     */
    private void updateItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String category = request.getParameter("category");
        String status = request.getParameter("status");

        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("msg", "物品ID不能为空");
            myItems(request, response);
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);

            // 检查物品是否存在且属于当前用户
            Item existingItem = itemService.getItemById(id);
            if (existingItem == null || !existingItem.getUserId().equals(user.getId())) {
                request.setAttribute("msg", "物品不存在或您没有权限修改");
                myItems(request, response);
                return;
            }

            // 验证输入
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("msg", "物品名称不能为空");
                request.setAttribute("item", existingItem);
                request.getRequestDispatcher("/views/itemEdit.jsp").forward(request, response);
                return;
            }

            if (priceStr == null || priceStr.trim().isEmpty()) {
                request.setAttribute("msg", "价格不能为空");
                existingItem.setName(name.trim());
                existingItem.setDescription(description != null ? description.trim() : "");
                request.setAttribute("item", existingItem);
                request.getRequestDispatcher("/views/itemEdit.jsp").forward(request, response);
                return;
            }

            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("msg", "价格必须大于0");
                existingItem.setName(name.trim());
                existingItem.setDescription(description != null ? description.trim() : "");
                request.setAttribute("item", existingItem);
                request.getRequestDispatcher("/views/itemEdit.jsp").forward(request, response);
                return;
            }

            Item item = new Item();
            item.setId(id);
            item.setName(name.trim());
            item.setDescription(description != null ? description.trim() : "");
            item.setPrice(price);
            item.setUserId(user.getId());
            item.setCategory(category != null ? category.trim() : "其他");
            item.setStatus(status != null ? status : Constants.ITEM_STATUS_ON_SALE);

            if (itemService.updateItem(item)) {
                request.setAttribute("msg", "物品修改成功！");
                myItems(request, response);
            } else {
                request.setAttribute("msg", "物品修改失败，请稍后重试");
                request.setAttribute("item", item);
                request.getRequestDispatcher("/views/itemEdit.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "价格格式不正确");
            myItems(request, response);
        }
    }

    /**
     * 删除物品
     */
    private void deleteItem(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("msg", "物品ID不能为空");
            myItems(request, response);
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);

            // 检查物品是否存在且属于当前用户
            Item item = itemService.getItemById(id);
            if (item == null || !item.getUserId().equals(user.getId())) {
                request.setAttribute("msg", "物品不存在或您没有权限删除");
                myItems(request, response);
                return;
            }

            if (itemService.deleteItem(id, user.getId())) {
                request.setAttribute("msg", "物品删除成功！");
            } else {
                request.setAttribute("msg", "物品删除失败，请稍后重试");
            }

            myItems(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "物品ID格式不正确");
            myItems(request, response);
        }
    }

    /**
     * 搜索物品（模糊匹配）
     */
    private void searchItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            listItems(request, response);
            return;
        }

        request.setAttribute("items", itemService.searchItems(keyword.trim()));
        request.setAttribute("keyword", keyword.trim());
        request.setAttribute("searchResultTitle", "搜索结果");
        request.getRequestDispatcher("/views/itemList.jsp").forward(request, response);
    }

    /**
     * 查看所有物品
     */
    private void listItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("items", itemService.getAllItemsOnSale());
        request.setAttribute("searchResultTitle", "所有二手物品");
        request.getRequestDispatcher("/views/itemList.jsp").forward(request, response);
    }

    /**
     * 查看我的物品
     */
    private void myItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("items", itemService.getItemsByUserId(user.getId()));
        request.setAttribute("searchResultTitle", "我的物品");
        request.getRequestDispatcher("/views/itemMy.jsp").forward(request, response);
    }

    /**
     * 查看已购买的物品
     */
    private void boughtItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            request.setAttribute("msg", "请先登录");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("items", itemService.getBoughtItemsByUserId(user.getId()));
        request.getRequestDispatcher("/views/itemBought.jsp").forward(request, response);
    }

    /**
     * 查看物品详情
     */
    private void itemDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("msg", "物品ID不能为空");
            listItems(request, response);
            return;
        }

        try {
            Integer id = Integer.parseInt(idStr);
            Item item = itemService.getItemById(id);

            if (item == null) {
                request.setAttribute("msg", "物品不存在或已被删除");
                listItems(request, response);
                return;
            }

            request.setAttribute("item", item);
            request.getRequestDispatcher("/views/itemDetail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("msg", "物品ID格式不正确");
            listItems(request, response);
        }
    }
}