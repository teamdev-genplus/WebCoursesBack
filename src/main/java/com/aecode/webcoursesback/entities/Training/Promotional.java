package com.aecode.webcoursesback.entities.Training;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "promotional")
@SequenceGenerator(name = "promotional_seq", sequenceName = "promotional_sequence", allocationSize = 1)
public class Promotional {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promotional_seq")
    private Long id;
    private String urlimage;
    @Column( length = 255)
    private String urllink;
    private Boolean active;
    //PARA UN TEXTO
    @Column(columnDefinition = "TEXT")
    private String text;
}