package com.jitterted.ebp.blackjack.adapter.in.console;

import com.jitterted.ebp.blackjack.domain.Hand;

import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

class ConsoleHand {

    // TRANSFORM Hand (domain) -> String (console-specific)
    static String displayDealerFaceUpCard(Hand hand) {
        return ConsoleCard.display(hand.dealerFaceUpCard());
    }

    static String cardsAsString(Hand hand) {
        return hand.cards().stream()
                   .map(ConsoleCard::display)
                   .collect(Collectors.joining(
                            ansi().cursorUp(6).cursorRight(1).toString()));
    }
}
