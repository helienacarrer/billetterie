package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResponseBilletDto {

    private UUID id;
    private String name;
    private Double price;
    private int totalQuantity;
    private int remainingQuantity;

}
