package fr.heliena.billetterie.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="entree_panier")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryBasket {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name="id_billet")
    private Billet billet;

    @Column(name="quantite")
    private int quantity;

}
