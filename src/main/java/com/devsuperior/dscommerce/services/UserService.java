package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//essa implementacao fizemos assim pra nao ter o problema do lazy loading da jpa, ja que agora tem uma consulta personalizada no UserRepository para trazer os roles tambem.
		//retorna uma lista de projections, ja que a relacao user com roles é para muitos de ambos os lados
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		//caso a lista seja zero vou lancar a excecao
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}
		//caso a lista tenha usuario, entao vou instanciar o objeto user
		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		//pegou os objetos que estao na lista result, e filtrou somente o getRoleId e getAuthority para adicionar no medoto userRole
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		return user;
	}

	//metodo que pega o usuario logado
	protected User authenticated() {
		try {
			//busca usuario logado com base no contexto do token jwt
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
			String username = jwtPrincipal.getClaim("username");
			User user = repository.findByEmail(username).get();
			return user;
		}
		catch (Exception e) {
			throw new UsernameNotFoundException("Email not found");
		}
	}

	//metodo que retorna o usuario logado
	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User user = authenticated();
		return new UserDTO(user);
	}
}
