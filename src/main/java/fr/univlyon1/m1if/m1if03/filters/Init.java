package fr.univlyon1.m1if.m1if03.filters;

import fr.univlyon1.m1if.m1if03.classes.GestionPassages;
import fr.univlyon1.m1if.m1if03.classes.Salle;
import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.dtos.SalleDTO;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import java.util.HashMap;
import java.util.Map;

@WebFilter(filterName = "Init")
public class Init extends HttpFilter {
    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();

        Map<String, User> users = new HashMap<>();
        Map<String, Salle> salles = new HashMap<>();
        GestionPassages gestionPassages = new GestionPassages();

        context.setAttribute("users", users);
        context.setAttribute("salles", salles);
        context.setAttribute("passages", gestionPassages);
    }
}