package fr.heliena.billetterie.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity //dire que c'est une table
@Table(name = "paniers")
@Data //faire les getter et setter
@AllArgsConstructor //créer un constructeur pas vide, donc celui par défaut disparait
@NoArgsConstructor //or jpa a besoin d'un constructeur vide
public class Basket {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @NotNull
    @Column(name = "statut_panier")
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_panier", nullable = false)
    private List<EntryBasket> entries;

}
