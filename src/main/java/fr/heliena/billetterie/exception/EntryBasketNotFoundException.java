package fr.heliena.billetterie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;


@RequiredArgsConstructor //constructeur avec que les arg final
@Getter
public class EntryBasketNotFoundException extends RuntimeException {

    private final UUID id;

}
