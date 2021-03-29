package fr.heliena.billetterie.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity //dire que c'est une table
@Table(name = "billets")
@Data //faire les getter et setter
@AllArgsConstructor //créer un constructeur pas vide, donc celui par défaut disparait
@NoArgsConstructor //or jpa a besoin d'un constructeur vide
public class Billet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @NotBlank
    @Column(name = "nom")
    private String name;

    @Min(0)
    @Column(name = "prix")
    private Double price;

    @Min(0)
    @Column(name = "quantite_totale")
    private int totalQuantity;

    @Min(0)
    @Column(name = "quantite_restante")
    private int remainingQuantity;

}
