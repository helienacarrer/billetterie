package fr.heliena.billetterie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@RequiredArgsConstructor //constructeur avec que les arg final
@Getter
public class BasketNotFoundException extends RuntimeException{

    private final UUID id;

}
