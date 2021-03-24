package fr.heliena.billetterie.controller.dto;

import fr.heliena.billetterie.model.Status;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResponseBasketDto {
    private UUID id;
    private Status status;
    private List<ResponseEntryBasketDto> entries;
}
