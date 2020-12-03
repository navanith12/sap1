package com.miraclesoft.datalake.user.message.request;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @NotBlank
    @Size(min=6, max = 100)
    private String password;


}
