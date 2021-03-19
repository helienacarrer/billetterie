package fr.heliena.billetterie.controller.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PutRequestBilletDto {

    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @Min(0)
    private Double price;

    @Min(0)
    private int totalQuantity;

}
