package fr.heliena.billetterie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class EntryBasketIdMismatchException extends RuntimeException {

    private final UUID bodyId;
    private final UUID pathId;

}