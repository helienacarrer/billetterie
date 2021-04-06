package fr.heliena.billetterie.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor //constructeur avec que les arg final
@Getter
public class BasketIdMismatchException extends RuntimeException {

    private final UUID bodyId;
    private final UUID pathId;

}
