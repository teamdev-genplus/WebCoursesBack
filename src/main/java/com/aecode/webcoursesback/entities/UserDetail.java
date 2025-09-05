package com.aecode.webcoursesback.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "userdetail")
@SequenceGenerator(name = "userdetail_seq", sequenceName = "userdetail_sequence", allocationSize = 1)

public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userdetail_seq")
    private int detailsId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserProfile userProfile;

    @Column(length = 255)
    private String profilepicture;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthdate;

    @Column( length = 255)
    private String phoneNumber;
    @Column(length = 100)
    private String gender;
    @Column(length = 100)
    private String country;
    @Column(length = 100)
    private String city;
    @Column(length = 100)
    private String profession;
    @Column(length = 100)
    private String education;
    @Column(length = 100)
    private String linkedin;

}
