package controller;

import pojo.User;
import service.UserService;
import util.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UserServlet", value = "/user")
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            // 默认跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        switch (action) {
            case "register":
                register(request, response);
                break;
            case "login":
                login(request, response);
                break;
            case "logout":
                logout(request, response);
                break;
            case "profile":
                profile(request, response);
                break;
            case "update":
                updateProfile(request, response);
                break;
            default:
                request.setAttribute("msg", "无效的操作");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 用户注册
     */
    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        // 验证数据
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("msg", "用户名不能为空");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("msg", "密码不能为空");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("msg", "两次输入的密码不一致");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setEmail(email != null ? email.trim() : "");
        user.setPhone(phone != null ? phone.trim() : "");

        // 注册用户
        if (userService.register(user)) {
            request.setAttribute("msg", "注册成功，请登录！");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else {
            request.setAttribute("msg", "用户名已存在，请选择其他用户名！");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        }
    }

    /**
     * 用户登录
     */
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("msg", "请输入用户名");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("msg", "请输入密码");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        User user = userService.login(username.trim(), password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute(Constants.USER_SESSION_KEY, user);

            // 设置session过期时间（30分钟）
            session.setMaxInactiveInterval(30 * 60);

            // 登录成功，重定向到物品列表页面
            response.sendRedirect(request.getContextPath() + "/item?action=list");
        } else {
            request.setAttribute("msg", "用户名或密码错误！");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

    /**
     * 用户退出
     */
    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute(Constants.USER_SESSION_KEY);
        session.invalidate(); // 使session失效

        request.setAttribute("msg", "您已成功退出登录");
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    /**
     * 查看用户资料
     */
    private void profile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        // 获取最新的用户信息
        User currentUser = userService.getUserById(user.getId());
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }

    /**
     * 更新用户资料
     */
    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(Constants.USER_SESSION_KEY);

        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 获取当前用户信息
        User user = userService.getUserById(sessionUser.getId());

        // 更新基本信息
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }

        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }

        // 更新密码（如果提供了旧密码和新密码）
        if (oldPassword != null && !oldPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty()) {

            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("msg", "新密码和确认密码不一致");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
                return;
            }

            // TODO: 这里需要实现密码验证和更新逻辑
            // 由于原UserService没有提供密码更新功能，这里暂时跳过
            request.setAttribute("msg", "密码更新功能暂未实现");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
            return;
        }

        // TODO: 更新用户信息到数据库
        // 这里需要扩展UserService，添加updateUser方法

        request.setAttribute("msg", "资料更新成功");
        request.setAttribute("user", user);
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }
}