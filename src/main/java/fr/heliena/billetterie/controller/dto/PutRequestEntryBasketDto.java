package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PutRequestEntryBasketDto {

    private UUID id;
    private EntryBasketBilletRequestDto billet;
    private int quantity;

}
