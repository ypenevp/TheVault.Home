package com.lords.server.home.entity;

import com.lords.server.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "home_members")
public class HomeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
