package com.guidodelbo.usercrud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_tbl")
public class User {

    @Id
    @GeneratedValue
    private int id;

    @NotNull(message = "Please provide a DNI")
    // validar con regex
    private int dni;

    @NotEmpty(message = "Please provide a name")
    private String name;

    @NotEmpty(message = "Please provide a surname")
    private String surname;

    @NotNull(message = "Please provide an age")
    @Min(1)
    private int age;

}
