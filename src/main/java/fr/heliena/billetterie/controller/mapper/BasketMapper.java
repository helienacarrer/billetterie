package fr.heliena.billetterie.controller.mapper;

import fr.heliena.billetterie.controller.dto.*;
import fr.heliena.billetterie.model.Basket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BasketMapper {

    // transforme le basket en dto
    ResponseBasketDto toDto(Basket basket);

    List<ResponseBasketDto> toDto(List<Basket> baskets);

    Basket toModel(PostRequestBasketDto dto);

    Basket toModel(PutRequestBasketDto dto);
}
