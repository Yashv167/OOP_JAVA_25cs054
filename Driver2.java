public class Driver2 {

    public static void main(String[] args) {

        Card[] cards = new Card[5];

        Card[] input = {
                new Card("Ace", "Spades"),
                new Card("King", "Hearts"),
                new Card("Queen", "Clubs"),
                new Card("Ace", "Spades"),   // Duplicate
                new Card("Jack", "Diamonds")
        };

        int count = 0;
        boolean duplicateFound = false;

        for (Card card : input) {

            boolean duplicate = false;

            for (int i = 0; i < count; i++) {
                if (card.equals(cards[i])) {
                    System.out.println("Duplicate found: " + card);
                    duplicate = true;
                    duplicateFound = true;
                    break;
                }
            }

            if (!duplicate) {
                cards[count] = card;
                count++;
            }

            if (duplicateFound) {
                break; 
            }
        }
    }
}