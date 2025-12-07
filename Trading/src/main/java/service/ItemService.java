package service;

import dao.ItemDAO;
import pojo.Item;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemService {
    private ItemDAO itemDAO = new ItemDAO();

    // 发布物品
    public boolean addItem(Item item) {
        return itemDAO.addItem(item);
    }

    // 修改物品
    public boolean updateItem(Item item) {
        return itemDAO.updateItem(item);
    }

    // 删除物品
    public boolean deleteItem(Integer itemId, Integer userId) {
        return itemDAO.deleteItem(itemId, userId);
    }

    // 搜索物品（模糊匹配）
    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllItemsOnSale();
        }
        return itemDAO.searchItems(keyword.trim());
    }

    // 获取所有在售物品
    public List<Item> getAllItemsOnSale() {
        return itemDAO.getAllItemsOnSale();
    }

    // 获取用户发布的物品
    public List<Item> getItemsByUserId(Integer userId) {
        return itemDAO.getItemsByUserId(userId);
    }

    // 根据ID获取物品
    public Item getItemById(Integer id) {
        return itemDAO.getItemById(id);
    }

    // 购买物品
    public boolean buyItem(Integer itemId, Integer buyerId) {
        return itemDAO.buyItem(itemId, buyerId);
    }

    // 获取已购买的物品
    public List<Item> getBoughtItemsByUserId(Integer userId) {
        return itemDAO.getBoughtItemsByUserId(userId);
    }
}