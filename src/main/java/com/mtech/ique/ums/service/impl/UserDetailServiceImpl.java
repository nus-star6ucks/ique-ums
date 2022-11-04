package com.mtech.ique.ums.service.impl;

import com.mtech.ique.ums.model.entity.User;
import com.mtech.ique.ums.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

  @Autowired private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (null == user) {
      throw new UsernameNotFoundException(String.format("''%s does not exist.", username));
    }
    return org.springframework.security.core.userdetails.User.withUsername(username)
        .password(user.getPassword())
        .authorities(user.getUserType())
        .build();
  }
}
