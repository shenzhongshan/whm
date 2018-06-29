package com.crfsdi.whm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.model.Role;
import com.crfsdi.whm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person applicationUser = userRepository.findByUsername(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        List<GrantedAuthority> roles= new ArrayList<GrantedAuthority>();
        List<Role> oroles = applicationUser.getRoles();
        if(oroles != null && !oroles.isEmpty()) {
            for(Role r : oroles) {
            	roles.add(new GrantedAuthority() {
					private static final long serialVersionUID = -3810503261737518421L;

					@Override
					public String getAuthority() {
						return r.getName();
					}
            		
            	});
            }
        }

        return new User(applicationUser.getUsername(), applicationUser.getPassword(), roles);
    }
}
