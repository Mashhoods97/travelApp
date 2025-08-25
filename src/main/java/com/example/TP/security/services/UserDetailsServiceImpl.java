package com.example.TP.security.services;

import com.example.TP.model.User;
import com.example.TP.repository.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {
//    @Autowired
//    UserService userService;
    @Autowired
    private UserRepo userRepo;

//    @Autowired
//    RoleService roleService;

//    @Autowired
//    PrivilegeService privilegeService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }

}
