package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.exception.UserServiceException;
import com.guidodelbo.usercrud.io.entity.PasswordResetTokenEntity;
import com.guidodelbo.usercrud.io.entity.RoleEntity;
import com.guidodelbo.usercrud.io.entity.UserEntity;
import com.guidodelbo.usercrud.io.repository.PasswordResetTokenRepository;
import com.guidodelbo.usercrud.io.repository.RoleRepository;
import com.guidodelbo.usercrud.io.repository.UserRepository;
import com.guidodelbo.usercrud.security.UserPrincipal;
import com.guidodelbo.usercrud.service.UserService;
import com.guidodelbo.usercrud.shared.AmazonSES;
import com.guidodelbo.usercrud.shared.Utils;
import com.guidodelbo.usercrud.shared.dto.AddressDto;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import com.guidodelbo.usercrud.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AmazonSES amazonSES;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final SchedulingServiceImpl schedulingService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder, AmazonSES amazonSES, PasswordResetTokenRepository passwordResetTokenRepository, SchedulingServiceImpl schedulingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.amazonSES = amazonSES;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.schedulingService = schedulingService;
    }

    @Override
    public UserDto saveUser(UserDto user) {

        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

        List<AddressDto> addresses = user.getAddresses();

        IntStream.range(0, addresses.size()).forEach(i -> {
            AddressDto address = addresses.get(i);
            address.setUserDetails(user);
            address.setAddressId(utils.generateAddressId(30));
            addresses.set(i, address);
        });

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        Collection<RoleEntity> roleEntities = new HashSet<>();

        user.getRoles().forEach(role -> {
            RoleEntity roleEntity = roleRepository.findByName(role);
            if(roleEntity != null)
                roleEntities.add(roleEntity);
        });

        userEntity.setRoles(roleEntities);

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

//        amazonSES.verifyEmail(returnValue);
        schedulingService.scheduleEmail(returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException("User with ID: " + userId + " was not found.");

        return new ModelMapper().map(userEntity, UserDto.class);
    }

    // Spring uses this method to retrieve user name and password when needed
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        return new UserPrincipal(userEntity);
    }

    @Override
    public void deleteUser(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        // Here depends on business logic either to check or not for empty name/surname
        userEntity.setName(user.getName());
        userEntity.setSurname(user.getSurname());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        return new ModelMapper().map(updatedUserDetails, UserDto.class);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {

        ModelMapper modelMapper = new ModelMapper();
        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0)
            page -= 1;

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);

        List<UserEntity> users = usersPage.getContent();

        users.forEach(userEntity -> {
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            returnValue.add(userDto);

        });

        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if (userEntity != null) {

            boolean hasTokenExpired = Utils.hasTokenExpired(token);

            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue = new AmazonSES().sendPasswordResetRequest(
                userEntity.getName(),
                userEntity.getEmail(),
                token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {

        boolean returnValue = false;

        if (Utils.hasTokenExpired(token))
            return returnValue;

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null)
            return returnValue;

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // Verify if password was saved successfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword))
            returnValue = true;

        // Remove Password Reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }
}
