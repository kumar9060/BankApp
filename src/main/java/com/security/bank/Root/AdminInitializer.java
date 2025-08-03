package com.security.bank.Root;

import com.security.bank.config.BootstrapAdminProperties;
import com.security.bank.entity.Role;
import com.security.bank.entity.User;
import com.security.bank.repository.RoleRepository;
import com.security.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BootstrapAdminProperties adminProperties;

    @PostConstruct
    public void initAdminUser(){

        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MANAGER", "ROLE_SUPPORT");
        roleNames.forEach(this:: createRoleIfNotExists);

//        Role role = roleRepository.findByRoleName("ROLE_ADMIN").
//                orElseGet(()->{
//                    Role newRole = new Role();
//                    newRole.setRoleName("ROLE_ADMIN");
//                    return roleRepository.save(newRole);
//                });
//
//        Role customerRole  = roleRepository.findByRoleName("ROLE_CUSTOMER").
//                orElseGet(()->{
//                    Role newRole = new Role();
//                    newRole.setRoleName("ROLE_CUSTOMER");
//                    return roleRepository.save(newRole);
//                });

        //Fetch admin role
        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow();

        //bootstrap (default) admin user
        if(userRepository.findByUsername(adminProperties.getUsername()).isEmpty()){
            User user = new User();
            user.setUsername(adminProperties.getUsername());
            user.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
            user.setRoles(adminRole);

            userRepository.save(user);
            System.out.println("✔ Default admin user created at startup.");
        }
        else{
            System.out.println("ℹ Default admin already exists.");
        }

    }

    private void createRoleIfNotExists(String roleName){
        roleRepository.findByRoleName(roleName).orElseGet(()->{
            Role role = new Role();
            role.setRoleName(roleName);
            return roleRepository.save(role);
        });
    }
}
