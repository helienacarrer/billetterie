package fr.heliena.billetterie.service;

import fr.heliena.billetterie.exception.BasketNotFoundException;
import fr.heliena.billetterie.exception.BilletNotFoundException;
import fr.heliena.billetterie.exception.EntryBasketNotFoundException;
import fr.heliena.billetterie.model.Basket;
import fr.heliena.billetterie.model.Billet;
import fr.heliena.billetterie.model.EntryBasket;
import fr.heliena.billetterie.model.Status;
import fr.heliena.billetterie.repository.BasketRepository;
import fr.heliena.billetterie.repository.BilletsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntryBasketServiceTest {

    @Mock
    BasketRepository basketRepository;

    @Mock
    BilletsRepository billetsRepository;

    @InjectMocks
    EntryBasketService entryBasketService;

    @Captor
    ArgumentCaptor<Billet> billetArgumentCaptor;

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
        when(basketRepository.findById(idBasket))
                .thenReturn(Optional.of(basket));
        when(billetsRepository.findById(idBillet))
                .thenReturn(Optional.of(billet));

        // WHEN
        entryBasketService.deleteAnEntryBasketById(idBasket, idEntry);

        // THEN
        // verify permet de s'assurer que la méthode save a été appelée une fois avec un billet, puis on capture ce avec quoi elle a été appelée
        // avec l'argument captor, on va capturer les arguments avec lesquels la méthode a été appelée (le billet sur lequel on a appliqué le delete du service)
        // ensuite, on va pouvoir faire des vérifications sur les arguments capturés
        verify(billetsRepository, times(1)).save(billetArgumentCaptor.capture());
        Billet capturedBillet = billetArgumentCaptor.getValue();
        assertEquals(idBillet, capturedBillet.getId());
        assertEquals(52, capturedBillet.getRemainingQuantity());

        verify(basketRepository, times(1)).save(basketArgumentCaptor.capture());
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

        when(basketRepository.findById(idBasket))
                .thenReturn(Optional.of(basket));

        EntryBasket entryBasketToUpdate = new EntryBasket(
                idEntry,
                billet,
                4
        );

        // WHEN
        entryBasketService.updateAnEntryBasket(idBasket, idEntry, entryBasketToUpdate);

        // THEN
        verify(basketRepository, times(1)).save(basketArgumentCaptor.capture());
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

        when(billetsRepository.findById(idBillet))
                .thenReturn(Optional.of(billet));
        when(basketRepository.findById(idBasket))
                .thenReturn(Optional.of(basket));

        EntryBasket entryBasketToAdd = new EntryBasket(
                null,
                billet,
                4
        );

        // WHEN
        entryBasketService.addAnEntryBasket(idBasket, entryBasketToAdd);

        // THEN
        verify(billetsRepository, times(1)).save(billetArgumentCaptor.capture());
        Billet capturedBillet = billetArgumentCaptor.getValue();
        assertEquals(idBillet, capturedBillet.getId());
        assertEquals(46, capturedBillet.getRemainingQuantity());


        verify(basketRepository, times(1)).save(basketArgumentCaptor.capture());
        Basket capturedBasket = basketArgumentCaptor.getValue();
        assertEquals(idBasket, capturedBasket.getId());
        assertEquals(1, capturedBasket.getEntries().size());

        EntryBasket capturedEntry = capturedBasket.getEntries().get(0);
        assertNull(capturedEntry.getId());
        assertEquals(billet, capturedEntry.getBillet());
        assertEquals(4, capturedEntry.getQuantity());
    }

    @Test
    void exceptionTestingDeleteEntryBasketCheckIdBasket() {
        // GIVEN
        UUID idBasket = UUID.randomUUID();
        UUID idEntry = UUID.randomUUID();

        when(basketRepository.findById(idBasket))
                .thenReturn(Optional.empty());

        // WHEN
        BasketNotFoundException thrown = assertThrows(
                BasketNotFoundException.class,
                () -> entryBasketService.deleteAnEntryBasketById(idBasket, idEntry)
        );

        // THEN
        assertEquals(idBasket, thrown.getId());
    }

    @Test
    void exceptionTestingDeleteEntryBasketCheckIdEntryBasket() {
        //billet oas trouvé par le findbyId
        // GIVEN
        UUID idEntry = UUID.randomUUID();
        UUID idBasket = UUID.randomUUID();

        Basket basket = new Basket(
                idBasket,
                Status.VALIDE,
                new ArrayList<>()
        );

        when(basketRepository.findById(idBasket))
                .thenReturn(Optional.of(basket));


        // WHEN
        EntryBasketNotFoundException thrown = assertThrows(
                EntryBasketNotFoundException.class,
                () -> entryBasketService.deleteAnEntryBasketById(idBasket, idEntry)
        );

        // THEN
        assertEquals(idEntry, thrown.getId());
    }

}
