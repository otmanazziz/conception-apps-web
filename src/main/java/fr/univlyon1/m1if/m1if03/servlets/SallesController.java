package fr.univlyon1.m1if.m1if03.servlets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univlyon1.m1if.m1if03.classes.Salle;
import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.dtos.SalleDTO;

import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.Map;

import static fr.univlyon1.m1if.m1if03.utils.ParseURI.parseUri;

@WebServlet(name = "SallesControlller", urlPatterns = {"/salles", "/salles/*"})
public class SallesController extends HttpServlet {

    Map<String, Salle> salles;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        salles = (Map<String, Salle>) config.getServletContext().getAttribute("salles");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> uri = parseUri(req.getRequestURI(), "salles");
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");

        if (uri.size() == 0){
            /**
             * Renvoie les URI des salles
             * Code 200: OK
             * Code 401: Utilisateur non authentifié
             * Code 403: Utilisateur non administrateur
             */
            System.out.println(resp.getHeaderNames());
            if (req.getHeader("accept").contains("application/json")){
                //JSON
                PrintWriter out = resp.getWriter();
                out.write("[\n");
                for (Map.Entry<String, Salle> salle: salles.entrySet()){
                    out.write("\"http://localhost:8080" + req.getRequestURI() + "/" + salle.getValue().getNom() + "\"\n");
                }
                out.write("]");
                out.close();

            } else if (req.getHeader("accept").contains("text/html")){
                //HTML
                req.setAttribute("page", "salles");
                requestDispatcherAdmin(req, resp);
            }
            resp.setStatus(200);
        } else if (uri.size() == 1){
            /**
             * Renvoie la représentation d'une salle
             * Code 200: OK
             * Code 401: Utilisateur non authentifié
             * Code 403: Utilisateur non administrateur
             * Code 404: Salle non trouvée
             */
            Salle salle;
            salle = salles.get(uri.get(0));
            if (salle == null){
                resp.sendError(404, "Salle non trouvée");
                return;
            }

            if (req.getHeader("accept").contains("application/json")){
                //JSON
                PrintWriter out = resp.getWriter();
                out.write("{\n");
                out.write("\"nomSalle\": \"" + salle.getNom() + "\",\n");
                out.write("\"capacite\": " + salle.getCapacite() + ",\n");
                out.write("\"presents\": " + salle.getPresents() + "\",\n");
                out.write("\"saturee\": " + (salle.getSaturee()? "true": "false") + "\n");
                out.write("}");
                out.close();

            } else if (req.getHeader("accept").contains("text/html")){
                //HTML
                req.setAttribute("page", "salle");
                req.setAttribute("salle", salle);
                requestDispatcherAdmin(req, resp);
            }
            resp.setStatus(200);

        } else if (uri.size() == 2){
            if (uri.get(1).equals("passages")){
                /**
                 * Renvoie les URI des passages dans la salle demandée
                 * Code 303: redirection vers la liste des passages dans la salle
                 *      URL:
                 * Code 401: Utilisateur non authentifié
                 * Code 403: Utilisateur non administrateur
                 */
                resp.setStatus(301);
                resp.setHeader("Location", "/tp4_war/passages/bySalle/" + uri.get(0));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> uri = parseUri(req.getRequestURI(), "salles");
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");

        if (uri.size() == 0){
            /**
             * Crée une nouvelle salle
             * Code 201: Salle crée (location: URL de la salle crée)
             * Code 400: Paramètres de requêtes non acceptables
             * Code 401: Utilisateur non authentifié
             * Code 403: Utilisateur non administrateur
             */
            if (req.getHeader("accept").contains("application/json")){
                SalleDTO salleDTO = new ObjectMapper().readValue(req.getReader(), SalleDTO.class);

                String json = new ObjectMapper().writeValueAsString(salleDTO);
                System.out.println(json);

            } else if (req.getHeader("accept").contains("text/html")){
                String action = req.getParameter("action");

                if (action != null && action.equals("Ajouter")){
                    String nomSalle = req.getParameter("nomSalle");

                    if (nomSalle != null && !nomSalle.equals("")){
                        Salle salle = salles.get(nomSalle);
                        if (salle != null){
                            resp.sendError(400, "This room already exists.");
                            return;
                        } else {
                            salle = new Salle(nomSalle);
                            salles.put(req.getParameter("nomSalle"), salle);
                            resp.setHeader("Location", "http://localhost:8080/tp4_war/salles/" + salle.getNom());
                        }
                    } else {
                        resp.sendError(400, "A room should have a name.");
                        return;
                    }
                } else {
                    resp.sendError(400, "You didn't choose 'Add' button.");
                    return;
                }
                resp.setStatus(201);
            }

            /*
               if (req.getParameter("nomSalle") != null){
                   Salle salle = salles.get(req.getParameter("nomSalle"));
                    String action = req.getParameter("action");

                    switch (action){
                        case "Ajouter":
                            if (salle != null){
                                resp.sendError(400, "La salle existe déjà");
                                return;
                            } else {
                                salles.put(req.getParameter("nomSalle"), new Salle(req.getParameter("nomSalle")));
                            }
                            break;
                        case "Modifier":
                            try {
                                salle.setCapacite(Integer.parseInt(req.getParameter("capacite")));
                            } catch (NumberFormatException e) {
                                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La capacité d'une salle doit être un nombre entier.");
                                return;
                            }
                            break;
                        case "Supprimer":
                            if (salle == null){
                                resp.sendError(400, "Salle non trouvée");
                                return;
                            } else {
                                salles.remove(req.getParameter("nomSalle"));
                            }
                            break;
                    }
                    req.setAttribute("page", "salles");
                } else {
                    resp.sendError(400, "Paramètres de requête non acceptables");
                    return;
                }

                 */
        }

        //doGet(req, resp);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> uri = parseUri(req.getRequestURI(), "salles");

        if (uri.size() == 1){
            /**
             * Supprime une salle spécifiée
             * Code 204: Salle supprimée
             * Code 401: Utilisateur non authentifié
             * Code 403: Utilisateur non administrateur
             * Code 404: Salle non trouvée
             */
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> uri = parseUri(req.getRequestURI(), "salles");

        if (uri.size() == 1){
            /**
             * Met à jour la salle (seulement sa capacité) ou la crée
             * Code 204: Salle crée ou modifiée
             * Code 400: Paramètres de requête non acceptables
             * Code 401: Utilisateur non authentifié
             * Code 403: Utilisateur non administrateur
             */
        }
    }


    private void requestDispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/interface.jsp")
            .include(req, resp);
    }

    private void requestDispatcherAdmin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/interface_admin.jsp")
                .include(req, resp);
    }

}
