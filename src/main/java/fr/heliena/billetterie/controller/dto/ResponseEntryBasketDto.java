package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResponseEntryBasketDto {

    private UUID id;

    private ResponseBilletDto billet;

    private int quantity;
}
