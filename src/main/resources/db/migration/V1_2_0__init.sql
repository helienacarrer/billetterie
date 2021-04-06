CREATE TABLE entree_panier
(
    id        UUID PRIMARY KEY,
    id_panier UUID NOT NULL,
    id_billet UUID NOT NULL,
    quantite  INT  NOT NULL,
    CONSTRAINT fk_panier
        FOREIGN KEY (id_panier)
            REFERENCES paniers (id),
    CONSTRAINT fk_billet
        FOREIGN KEY (id_billet)
            REFERENCES billets (id)
);