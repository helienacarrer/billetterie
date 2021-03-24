package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class PostRequestBilletDto {

    @NotBlank
    private String name;

    @Min(0)
    private double price;

    @Min(0)
    private int totalQuantity;

}
