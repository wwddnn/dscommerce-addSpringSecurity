package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    //esse metodo vai validar se é a propria pessoa 'self' que logou e que esta consultando o pedido, ou se é o admin. só eles podem consultar o pedido feito.
    public void validateSelfOrAdmin(long userId) {
        //peguei o usuario que esta logado e autenticado e chamei ele de 'me'
        User me = userService.authenticated();
        //se o usuario me, nao possui o role_admin,
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
