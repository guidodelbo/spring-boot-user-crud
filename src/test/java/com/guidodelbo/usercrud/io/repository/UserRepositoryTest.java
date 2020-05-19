package com.guidodelbo.usercrud.io.repository;

import com.guidodelbo.usercrud.io.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    final void findAllUsersWithConfirmedEmailAddressTest() {
/*        Pageable pageableRequest = PageRequest.of(0, 2);
        Page<UserEntity> userEntities = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
        assertNotNull(userEntities);*/
    }
}
