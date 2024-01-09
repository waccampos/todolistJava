package com.aula.rocketseat.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.aula.rocketseat.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws
                                    ServletException,
                                    IOException {
        var serveletpath = request.getServletPath();

        if (serveletpath.startsWith("/tasks/")) {

            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);

            String[] list = authString.split(":");
            var username = list[0];
            var password = list[1];

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                var passwordverify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordverify.verified){
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                }else {
                    response.sendError(401,"usuario sem autorização");
                }

            }
        }else {
            filterChain.doFilter(request, response);
        }
    }
}
