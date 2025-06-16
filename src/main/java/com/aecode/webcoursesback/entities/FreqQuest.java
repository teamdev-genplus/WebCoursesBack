package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "freqquests")
@SequenceGenerator(name = "freqquest_seq", sequenceName = "freqquest_sequence", allocationSize = 1)
public class FreqQuest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "freqquest_seq")
    private Long freqquestId;

    @Column(length = 255)
    private String questionText;

    @Column(length = 255)
    private String answerText;

}
