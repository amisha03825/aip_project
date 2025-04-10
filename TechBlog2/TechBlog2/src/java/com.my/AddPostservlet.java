package com.my;

import com.techblog.dao.PostDao;
import com.techblog.entities.Post;
import com.techblog.entities.User;
import com.techblog.helper.ConnectionProvider;
import com.techblog.helper.Helper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
public class AddPostservlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            int cid = Integer.parseInt(request.getParameter("cid"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String code = request.getParameter("code");
            Part part = request.getPart("pic");

            // Getting current user from session (FIXED attribute name)
            HttpSession s = request.getSession();
            User u = (User) s.getAttribute("current user");

            // Check if user is not null
            if (u == null) {
                out.println("User not logged in!");
                return;
            }

            int userId = u.getId();

            // Creating Post object
            Post p = new Post(title, content, code, part.getSubmittedFileName(), cid, userId);

            // Saving post using DAO
            PostDao dao = new PostDao(ConnectionProvider.getConnection());
            boolean success = dao.savePost(p);

            if (success) {
                // Save uploaded image
                String path = request.getServletContext().getRealPath("/") + "Blog_pics" + File.separator + p.getPpic();
                Helper.saveFile(part.getInputStream(), path);
                out.print("done");
            } else {
                out.print("error");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles post creation by logged-in user";
    }
}
