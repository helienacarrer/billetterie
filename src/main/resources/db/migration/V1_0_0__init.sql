CREATE TABLE billets
(
    id                UUID PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    prix              FLOAT        NOT NULL,
    quantite_totale   INT          NOT NULL,
    quantite_restante INT          NOT NULL
);
