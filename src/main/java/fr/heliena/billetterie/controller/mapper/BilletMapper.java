package fr.heliena.billetterie.controller.mapper;

import fr.heliena.billetterie.controller.dto.PostRequestBilletDto;
import fr.heliena.billetterie.controller.dto.PutRequestBilletDto;
import fr.heliena.billetterie.controller.dto.ResponseBilletDto;
import fr.heliena.billetterie.model.Billet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BilletMapper {

    // transforme le billet en dto
    ResponseBilletDto toDto(Billet billet);

    List<ResponseBilletDto> toDto(List<Billet> billets);

    Billet toModel(PostRequestBilletDto dto);

    Billet toModel(PutRequestBilletDto dto);

}
