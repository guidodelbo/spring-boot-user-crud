package com.guidodelbo.usercrud;

import com.guidodelbo.usercrud.io.entity.AuthorityEntity;
import com.guidodelbo.usercrud.io.entity.RoleEntity;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import com.guidodelbo.usercrud.io.repository.AuthorityRepository;
import com.guidodelbo.usercrud.io.repository.RoleRepository;
import com.guidodelbo.usercrud.io.repository.UserRepository;
import com.guidodelbo.usercrud.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;

@Component
public class InitialUserSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity userRole = createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
        RoleEntity adminRole = createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        if(adminRole == null)
            return;

        UserEntity adminUser = userRepository.findByEmail("test@test.com");

        if (adminUser == null) {

            adminUser = new UserEntity();
            adminUser.setName("Guido");
            adminUser.setSurname("Delbo");
            adminUser.setEmail("test@test.com");
            adminUser.setEmailVerificationStatus(true);
            adminUser.setUserId(utils.generateUserId(30));
            adminUser.setEncryptedPassword(passwordEncoder.encode("123"));
            adminUser.setRoles(Arrays.asList(adminRole));

            userRepository.save(adminUser);
        }
    }

    @Transactional
    private AuthorityEntity createAuthority(String name) {

        AuthorityEntity authority = authorityRepository.findByName(name);

        if(authority == null){
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }

        return authority;
    }

    @Transactional
    private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities){

        RoleEntity role = roleRepository.findByName(name);

        if(role == null){
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }

        return role;
    }
}
