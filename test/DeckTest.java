import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest {

    @Test
    void checkNewDeckHas52UniqueCards() {
        // Arrange
        Deck deck = new Deck();
        Set<Card> seen = new HashSet<>();

        // Act + Assert
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawTopCard();
            assertNotNull(card);
            assertTrue(seen.add(card));
        }

        assertEquals(0, deck.remainingCards());
        assertNull(deck.drawTopCard());
    }

    @Test
    void checkReturnToBottomIncreasesCount() {
        // Arrange
        Deck deck = new Deck();
        Card drawnCard = deck.drawTopCard();
        assertNotNull(drawnCard);

        // Act
        deck.returnToBottom(drawnCard);

        // Assert
        assertEquals(52, deck.remainingCards());
    }

    @Test
    void bugCaseNullCardShouldThrowError() {
        // Arrange
        Deck deck = new Deck();

        // Assert
        assertThrows(NullPointerException.class, () -> deck.returnToBottom(null));
    }
}
