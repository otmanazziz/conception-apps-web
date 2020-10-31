<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<aside class="menu">
    <p><strong>Menu</strong></p>
    <p>
        <a href="presence">Accueil</a><br>
        <a href="presence?contenu=saisie_passage">Nouveau passage</a><br>
        <a href="presence?contenu=passages">Mes passages</a>
    </p>
    <c:if test="${sessionScope.user.admin}">
        <p>
            <em><a href="admin">Interface d'administration</a></em><br>
        </p>
    </c:if>
    <p>
        <a href="Deco">Déconnexion</a>
    </p>
</aside>
