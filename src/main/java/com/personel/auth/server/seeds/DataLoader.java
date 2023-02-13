package com.personel.auth.server.seeds;

import com.personel.auth.server.modeles.ERole;
import com.personel.auth.server.modeles.Role;
import com.personel.auth.server.modeles.User;
import com.personel.auth.server.repository.RoleRepository;
import com.personel.auth.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Override
    public void run(String... args) throws Exception {
        loadRoleData();
        loadUserData();
    }
    private void loadRoleData(){
        if(roleRepository.count() == 0){
            Role userRole = new Role(ERole.ROLE_USER);
            Role moderatorRole = new Role(ERole.ROLE_MODERATOR);
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(userRole);
            roleRepository.save(moderatorRole);
            roleRepository.save(adminRole);
        }
    }

    private void loadUserData(){
        if(userRepository.count() == 0){
            Role userRole = roleRepository
                    .findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            Role moderatorRole = roleRepository
                    .findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            Role adminRole = roleRepository
                    .findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            Set<Role> userOneRoles = new HashSet<>(){{
                add(userRole);
            }};
            Set<Role> userTwoRoles = new HashSet<>(){{
                add(userRole);
                add(moderatorRole);
            }};
            Set<Role> userThreeRoles = new HashSet<>(){{
                add(userRole);
                add(moderatorRole);
                add(adminRole);
            }};

            User userOne = new User("hosniboun", "hosni@gmail.com", encoder.encode("123456"));
            User userTwo = new User("hosniboun2", "hosni2@gmail.com", encoder.encode("123456"));
            User userThree = new User("hosniboun3", "hosni3@gmail.com", encoder.encode("123456"));

            userOne.setRoles(userOneRoles);
            userRepository.save(userOne);

            userTwo.setRoles(userTwoRoles);
            userRepository.save(userTwo);

            userThree.setRoles(userThreeRoles);
            userRepository.save(userThree);
        }
    }
}
