package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import com.guidodelbo.usercrud.io.repository.UserRepository;
import com.guidodelbo.usercrud.shared.Utils;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceImplTest {

    @Value("${test.userserviceimpl.nombre.user}")
    public String msgUser;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "hhty57ehfy";
    String encryptedPassword = "74hghd8474jf";

    UserEntity userEntity;

    @BeforeEach
    void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Guido");
        userEntity.setSurname("Delbo");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("7htnfhr758");
    }

    @Test
    final void getUserTest() {

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Guido", userDto.getName());
    }

    @Test
    final void getUserUsernameNotFoundExceptionTest() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UserServiceException.class,

                () -> {
                    userService.getUser("test@test.com");
                }
        );
    }

    @Test
    final void testSaveUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.findByEmail(eq("Guido"))).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("hgfnghtyrir884");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto);

        UserDto userDto = new UserDto();
        userDto.setAddresses(addresses);

        UserDto storedUserDetails = userService.saveUser(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getName(), storedUserDetails.getName(), "testeando nombre de usuario");
        assertEquals(userEntity.getSurname(), storedUserDetails.getSurname());
        assertNotNull(storedUserDetails.getUserId());
//        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
//        verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
//        verify(bCryptPasswordEncoder, times(1)).encode("12345678");
//        verify(userRepository,times(1)).save(any(UserEntity.class));
    }


}