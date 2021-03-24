package fr.heliena.billetterie.controller.dto;

import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class PutRequestBasketDto {

    @NotNull
    private UUID id;

    @NotNull
    private Status status;

    // fixme dto
    private List<EntryBasket> entries;

}
