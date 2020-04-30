package com.guidodelbo.usercrud.io.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "address")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = -3549149214145831637L;

    @Id
    @GeneratedValue
    private long id;
    @Column(length = 30, nullable = false)
    private String addressId;
    @Column(length = 15, nullable = false)
    private String city;
    @Column(length = 15, nullable = false)
    private String country;
    @Column(length = 100, nullable = false)
    private String streetName;
    @Column(length = 7, nullable = false)
    private String postalCode;
    @Column(length = 10, nullable = false)
    private String type;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;
}
