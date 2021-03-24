package fr.heliena.billetterie.controller.dto;

import lombok.Data;

@Data
public class PostRequestEntryBasketDto {

    private EntryBasketBilletRequestDto billet;
    private int quantity;

}
