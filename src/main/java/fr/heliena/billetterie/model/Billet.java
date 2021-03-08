package fr.heliena.billetterie.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity //dire que c'est une table
@Table(name = "billets")
@Data //faire les getter et setter
@AllArgsConstructor //créer un constructeur pas vide, donc celui par défaut disparait
@NoArgsConstructor //or jpa a besoin d'un constructeur vide

public class Billet {

    @Id
    private UUID id;

    @Column(name = "nom")
    private String name;

    @Column(name = "prix")
    private Double price;

    @Column(name = "quantite_totale")
    private int totalQuantity;

    @Column(name = "quantite_restante")
    private int remainingQuantity;

}
