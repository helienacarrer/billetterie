package fr.heliena.billetterie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotEnoughRemainingBilletsException extends RuntimeException {

    private final int wanted;
    private final int remaining;

}
