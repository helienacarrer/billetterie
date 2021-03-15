package fr.heliena.billetterie.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity //dire que c'est une table
@Table(name = "paniers")
@Data //faire les getter et setter
@AllArgsConstructor //créer un constructeur pas vide, donc celui par défaut disparait
@NoArgsConstructor //or jpa a besoin d'un constructeur vide

public class Basket {

    @Id
    private UUID id;

    @NotNull
    @Column(name = "statut_panier")
    private Status status;

}
