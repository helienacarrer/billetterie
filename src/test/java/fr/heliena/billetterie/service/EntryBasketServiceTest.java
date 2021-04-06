package fr.heliena.billetterie.service;

import fr.heliena.billetterie.exception.EntryBasketIdMismatchException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntryBasketServiceTest {

    @Mock
    BasketService basketService;

    @InjectMocks
    EntryBasketService entryBasketService;

    @Captor
    ArgumentCaptor<UUID> idArgumentCaptor;

    @Captor
    ArgumentCaptor<Basket> basketArgumentCaptor;

    @Test
    void shouldDeleteAnEntryFromABasket() {
        // GIVEN
        UUID idBillet = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();
        UUID idEntry = UUID.randomUUID();

        Billet billet = new Billet(
                // met pas null car repo est un mocke, pas une vraie base, donc id pas créé automatiquement
                idBillet,
                "vielles charrues",
                70.0,
                100,
                50
        );
        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>(List.of(
                        new EntryBasket(
                                idEntry,
                                billet,
                                2
                        )
                ))
        );

        //dire quel comportemeny la méthode findByID doit adopter acr repo est un mock,
        //donc faut dire quel sera son comportement
        when(basketService.getABasketById(idBasket))
                .thenReturn(basket);

        // WHEN
        entryBasketService.deleteAnEntryBasketById(idBasket, idEntry);

        // THEN
        verify(basketService, times(1)).updateABasket(idArgumentCaptor.capture(), basketArgumentCaptor.capture());
        UUID capturedId = idArgumentCaptor.getValue();
        assertEquals(idBasket, capturedId);

        Basket capturedBasket = basketArgumentCaptor.getValue();
        assertEquals(idBasket, capturedBasket.getId());
        assertEquals(0, capturedBasket.getEntries().size());
    }

    @Test
    void shouldUpdateAnEntryOfTheBasket() {
        // GIVEN
        UUID idBillet = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();
        UUID idEntry = UUID.randomUUID();

        Billet billet = new Billet(
                // met pas null car repo est un mocke, pas une vraie base, donc id pas créé automatiquement
                idBillet,
                "vielles charrues",
                70.0,
                100,
                50
        );
        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>(List.of(
                        new EntryBasket(
                                idEntry,
                                billet,
                                2
                        )
                ))
        );

        when(basketService.getABasketById(idBasket))
                .thenReturn(basket);

        EntryBasket entryBasketToUpdate = new EntryBasket(
                idEntry,
                billet,
                4
        );

        // WHEN
        entryBasketService.updateAnEntryBasket(idBasket, idEntry, entryBasketToUpdate);

        // THEN
        verify(basketService, times(1)).updateABasket(idArgumentCaptor.capture(), basketArgumentCaptor.capture());
        UUID capturedId = idArgumentCaptor.getValue();
        assertEquals(idBasket, capturedId);

        Basket capturedBasket = basketArgumentCaptor.getValue();
        assertEquals(idBasket, capturedBasket.getId());
        assertEquals(1, capturedBasket.getEntries().size());

        EntryBasket capturedEntry = capturedBasket.getEntries().get(0);
        assertEquals(idEntry, capturedEntry.getId());
        assertEquals(billet, capturedEntry.getBillet());
        assertEquals(4, capturedEntry.getQuantity());
    }

    @Test
    void shouldAddAnEntryOfTheBasket() {
        // GIVEN
        UUID idBillet = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();

        Billet billet = new Billet(
                // met pas null car repo est un mock, pas une vraie base, donc id pas créé automatiquement
                idBillet,
                "vielles charrues",
                70.0,
                100,
                50
        );
        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>()
        );

        when(basketService.getABasketById(idBasket))
                .thenReturn(basket);

        EntryBasket entryBasketToAdd = new EntryBasket(
                null,
                billet,
                4
        );

        // WHEN
        entryBasketService.addAnEntryBasket(idBasket, entryBasketToAdd);

        // THEN
        verify(basketService, times(1)).updateABasket(idArgumentCaptor.capture(), basketArgumentCaptor.capture());
        UUID capturedId = idArgumentCaptor.getValue();
        assertEquals(idBasket, capturedId);

        Basket capturedBasket = basketArgumentCaptor.getValue();
        assertEquals(idBasket, capturedBasket.getId());
        assertEquals(1, capturedBasket.getEntries().size());

        EntryBasket capturedEntry = capturedBasket.getEntries().get(0);
        assertNull(capturedEntry.getId());
        assertEquals(billet, capturedEntry.getBillet());
        assertEquals(4, capturedEntry.getQuantity());
    }

    @Test
    void shouldThrowAnExceptionWhenDeletingAnEntryThatDoesNotExsit() {
        // GIVEN
        UUID idEntry = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();

        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>()
        );

        when(basketService.getABasketById(idBasket))
                .thenReturn(basket);


        // WHEN
        EntryBasketNotFoundException thrown = assertThrows(
                EntryBasketNotFoundException.class,
                () -> entryBasketService.deleteAnEntryBasketById(idBasket, idEntry)
        );

        // THEN
        assertEquals(idEntry, thrown.getId());
    }

    @Test
    void shouldThrowAnExceptionWhenUpdatingAnEntryThatDoesNotExsit() {
        // GIVEN
        UUID idBillet = UUID.randomUUID();
        UUID idEntry = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();

        Billet billet = new Billet(
                // met pas null car repo est un mock, pas une vraie base, donc id pas créé automatiquement
                idBillet,
                "vielles charrues",
                70.0,
                100,
                50
        );
        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>()
        );

        when(basketService.getABasketById(idBasket))
                .thenReturn(basket);


        // WHEN
        EntryBasketNotFoundException thrown = assertThrows(
                EntryBasketNotFoundException.class,
                () -> entryBasketService.updateAnEntryBasket(idBasket, idEntry, new EntryBasket(
                        idEntry,
                        billet,
                        5
                ))
        );

        // THEN
        assertEquals(idEntry, thrown.getId());
    }

    @Test
    void shouldThrowAnExceptionWhenUpdatingAnEntryBasketWithIdMismatch() {
        // GIVEN
        UUID idBillet = UUID.randomUUID();
        UUID idEntry = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();

        Billet billet = new Billet(
                // met pas null car repo est un mock, pas une vraie base, donc id pas créé automatiquement
                idBillet,
                "vielles charrues",
                70.0,
                100,
                50
        );

        // WHEN
        assertThrows(
                EntryBasketIdMismatchException.class,
                () -> entryBasketService.updateAnEntryBasket(idBasket, idEntry, new EntryBasket(
                        UUID.randomUUID(),
                        billet,
                        5
                ))
        );
    }

}
