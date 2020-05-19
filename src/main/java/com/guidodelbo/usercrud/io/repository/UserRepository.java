package com.guidodelbo.usercrud.io.repository;

import com.guidodelbo.usercrud.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

/*
    @Query(value = "SELECT * FROM user u WHERE u.EMAIL_VERIFICATION_STATUS='true'",
            countQuery = "SELECT count(*) FROM user u WHERE u.EMAIL_VERIFICATION_STATUS='true'",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

    @Query(value = "SELECT * FROM user u WHERE u.NAME=?1", nativeQuery = true)
    List<UserEntity> findUserByName(String firstName);

    @Query(value = "SELECT * FROM user u WHERE u.SURNAME=:lastName", nativeQuery = true)
    List<UserEntity> findUserBySurname(@Param("lastName") String lastName);

    @Query(value="SELECT * FROM user u WHERE u.NAME LIKE %:keyword% or u.SURNAME LIKE %:keyword%",nativeQuery=true)
    List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);

    @Query(value="SELECT u.NAME, u.SURNAME FROM user u WHERE u.NAME LIKE %:keyword% or u.SURNAME LIKE %:keyword%",nativeQuery=true)
    List<Object[]> findUserNameAndSurnameByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="UPDATE user u set u.EMAIL_VERIFICATION_STATUS=:emailVerificationStatus WHERE u.user_id=:userId", nativeQuery=true)
    void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userId") String userId);

    // JPQL:

    @Query("SELECT user FROM UserEntity user WHERE user.userId =:userId")
    UserEntity findUserEntityByUserId(@Param("userId") String userId);

    @Query("SELECT user.name, user.surname FROM UserEntity user WHERE user.userId =:userId")
    List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.emailVerificationStatus =:emailVerificationStatus WHERE u.userId = :userId")
    void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
            @Param("userId") String userId);
*/
}
