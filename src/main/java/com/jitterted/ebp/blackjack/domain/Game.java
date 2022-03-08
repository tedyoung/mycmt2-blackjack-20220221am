package com.jitterted.ebp.blackjack.domain;

import com.jitterted.ebp.blackjack.domain.port.GameMonitor;

public class Game {

    private final Deck deck;
    private final GameMonitor gameMonitor;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    private boolean playerDone;

    public Game(Deck deck) {
        this(deck, game -> {});
    }

    public Game(Deck deck, GameMonitor gameMonitor) {
        this.deck = deck;
        this.gameMonitor = gameMonitor;
    }

    private void dealRoundOfCards() {
        // why: players first because this is the rule
        playerHand.drawFrom(deck);
        dealerHand.drawFrom(deck);
    }

    public GameOutcome determineOutcome() {
        if (playerHand.isBusted()) {
            return GameOutcome.PLAYER_BUSTED;
        } else if (dealerHand.isBusted()) {
            return GameOutcome.DEALER_BUSTED;
        } else if (playerHand.hasBlackjack()) {
            return GameOutcome.PLAYER_WINS_BLACKJACK;
        } else if (playerHand.beats(dealerHand)) {
            return GameOutcome.PLAYER_BEATS_DEALER;
        } else if (playerHand.pushes(dealerHand)) {
            return GameOutcome.PLAYER_PUSHES;
        } else {
            return GameOutcome.PLAYER_LOSES;
        }
    }

    private void dealerTurn() {
        // Dealer makes its choice automatically based on a simple heuristic (<=16 must hit, =>17 must stand)
        if (!playerHand.isBusted()) {
            while (dealerHand.dealerMustDrawCard()) {
                dealerHand.drawFrom(deck);
            }
        }
    }

    // "Query Method Rule":
    // -> SNAPSHOT (point-in-time)
    // -> PREVENT illegal change to state
    // 0. Hand - NO - is mutable, and not a snapshot
    // 1. Copy/clone (new Hand(dealerHand(cards()) - maybe - confusing to client
    // 2. DTO ("HandView") - cards, dealerFaceUpCard, value - NO - JavaBean
    // 2a. Value Object ("HandView") - query methods (Domain) - YES
    //      Created by Hand or here in Game, depending on usage
    // 3. Interface "read-only" Hand (not have command methods) - NO - live view
    public Hand dealerHand() {
        return dealerHand;
    }

    public Hand playerHand() {
        return playerHand;
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
        updatePlayerDoneStateTo(playerHand.hasBlackjack());
    }

    public void playerHits() {
        playerHand.drawFrom(deck);
        updatePlayerDoneStateTo(playerHand.isBusted());
    }

    public void playerStands() {
        dealerTurn();
        updatePlayerDoneStateTo(true);
    }

    public boolean isPlayerDone() {
        return playerDone;
    }

    private void updatePlayerDoneStateTo(boolean playerDoneState) {
        if (playerDoneState) {
            playerDone = true;
            gameMonitor.roundCompleted(this);
        }
    }

}
