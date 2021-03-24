package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EntryBasketBilletRequestDto {

    @NotNull
    private UUID id;

}
