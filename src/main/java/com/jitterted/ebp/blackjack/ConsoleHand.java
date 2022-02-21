package com.jitterted.ebp.blackjack;

public class ConsoleHand {

    // TRANSFORM Hand (domain) -> String (console-specific)
    static String displayDealerFaceUpCard(Hand hand) {
        return ConsoleCard.display(hand.dealerFaceUpCard());
    }
}
