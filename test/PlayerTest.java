import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

    @Test
    void checkComputerPlayerName() {
        // Assert
        assertTrue(new Player("Computer").isComputerControlled());
        assertTrue(new Player("computer").isComputerControlled());
        assertFalse(new Player("Alice").isComputerControlled());
    }

    @Test
    void checkScoreCalculation() {
        // Arrange
        Player player = new Player("Alice");

        // Act
        player.addRoundPoints(3);
        player.addSequenceBonus(5);
        player.addSuitBonus(2);

        // Assert
        assertEquals(3, player.getRoundPoints());
        assertEquals(5, player.getSequenceBonus());
        assertEquals(2, player.getSuitBonus());
        assertEquals(10, player.getScore());
    }

    @Test
    void checkDiscardByOneBasedIndex() {
        // Arrange
        Player player = new Player("Alice");
        Card first = new Card(Card.Suit.CLUBS, Card.Rank.TWO);
        Card second = new Card(Card.Suit.DIAMONDS, Card.Rank.THREE);
        Card third = new Card(Card.Suit.HEARTS, Card.Rank.FOUR);

        player.collectCard(first);
        player.collectCard(second);
        player.collectCard(third);

        // Act
        List<Card> discarded = player.discardByOneBasedIndexes(new ArrayList<>(List.of(1, 3)));

        // Assert
        assertEquals(List.of(third, first), discarded);
        assertEquals(List.of(second), player.getCollectedCards());
    }

    @Test
    void bugCaseInvalidDiscardIndexesShouldBeIgnored() {
        // Arrange
        Player player = new Player("Alice");
        Card first = new Card(Card.Suit.CLUBS, Card.Rank.TWO);
        Card second = new Card(Card.Suit.DIAMONDS, Card.Rank.THREE);

        player.collectCard(first);
        player.collectCard(second);

        // Act
        List<Card> discarded = player.discardByOneBasedIndexes(new ArrayList<>(List.of(0, 99)));

        // Assert
        assertTrue(discarded.isEmpty());
        assertEquals(2, player.getCollectedCards().size());
    }
}
