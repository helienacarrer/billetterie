package fr.heliena.billetterie.controller.mapper;

import fr.heliena.billetterie.controller.dto.PostRequestEntryBasketDto;
import fr.heliena.billetterie.controller.dto.PutRequestEntryBasketDto;
import fr.heliena.billetterie.controller.dto.ResponseEntryBasketDto;
import fr.heliena.billetterie.model.EntryBasket;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntryBasketMapper {

    // transforme entryBasket en dto
    ResponseEntryBasketDto toDto(EntryBasket entryBasket);

    List<ResponseEntryBasketDto> toDto(List<EntryBasket> entryBaskets);

    EntryBasket toModel(PostRequestEntryBasketDto dto);

    EntryBasket toModel(PutRequestEntryBasketDto dto);

}
