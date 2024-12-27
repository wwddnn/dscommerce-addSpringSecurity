package com.devsuperior.dscommerce.repositories;

import java.util.List;
import java.util.Optional;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//metodo usando query nativa com sql raiz, para buscar no banco tanto o user quanto o role.
	//fizemos no projections alguns metodos, e como o carregamento padrao é lazy, pois é muitos para muitos, resolvemos criar essa consulta para buscar o role tambem no banco alem do user.
	@Query(nativeQuery = true, value = """
				SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
				FROM tb_user
				INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
				INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
				WHERE tb_user.email = :email
			""")
	List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

	//metodo para retornar o email para o usuario que esta logado
	Optional<User> findByEmail(String email);
}
