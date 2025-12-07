package service;



import dao.UserDAO;
import pojo.User;
import util.MD5Util;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    // 用户注册
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDAO.isUsernameExists(user.getUsername())) {
            return false;
        }

        // 加密密码
        user.setPassword(MD5Util.encrypt(user.getPassword()));

        // 保存用户
        return userDAO.addUser(user);
    }

    // 用户登录
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user != null && user.getPassword().equals(MD5Util.encrypt(password))) {
            // 返回用户对象（不包含密码）
            User result = new User();
            result.setId(user.getId());
            result.setUsername(user.getUsername());
            result.setEmail(user.getEmail());
            result.setPhone(user.getPhone());
            result.setCreateTime(user.getCreateTime());
            return result;
        }
        return null;
    }

    // 获取用户信息
    public User getUserById(Integer id) {
        return userDAO.getUserById(id);
    }
}