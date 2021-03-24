package fr.heliena.billetterie.controller.dto;

import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PostRequestBasketDto {

    @NotNull
    private Status status;

    // fixme dto
    private List<EntryBasket> entries;

}
