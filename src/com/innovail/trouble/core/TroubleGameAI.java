/**
 * @file: com.innovail.trouble.core - TroubleGameAI.java
 * @date: Jul 2, 2013
 * @author: bweber
 */
package com.innovail.trouble.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import com.innovail.trouble.core.gameelement.Spot;
import com.innovail.trouble.core.gameelement.Token;

/**
 * 
 */
public class TroubleGameAI
{
    public static enum Difficulty {
        EASY, MEDIUM, HARD,
    }

    private class DistanceComparator implements Comparator <Token>
    {

        @Override
        public int compare (final Token token0, final Token token1)
        {
            final int token0Distance = token0.getDistanceToFinish (true);
            final int token1Distance = token1.getDistanceToFinish (true);
            if (token0Distance < token1Distance) {
                return -1;
            } else if (token0Distance > token1Distance) {
                return 1;
            }
            return 0;
        }

    }

    private static enum Operation {
        CHECK_OWN_START, CHECK_OTHER_START, CHECK_FURTHEST, CHECK_FOE, NOOP,
    }

    public static enum Policy {
        FAST, AGGRESSIVE, NICE,
    }

    private final Policy     _currentPolicy;

    private final Difficulty _currentDifficulty;

    public TroubleGameAI ()
    {
        _currentPolicy = Policy.FAST;
        _currentDifficulty = Difficulty.EASY;
    }

    /**
     * 
     */
    public TroubleGameAI (final Policy policy, final Difficulty difficulty)
    {
        _currentPolicy = policy;
        _currentDifficulty = difficulty;
    }

    private Token checkForFurthestFoeToken (final TreeSet <Token> foeDistanceSet,
                                            final Map <Token, Token> friendFoeMapping)
    {
        try {
            final Token closestFoeToken = foeDistanceSet.first ();
            final Token token = friendFoeMapping.get (closestFoeToken);
            return token;
        } catch (final NoSuchElementException e) {
        }
        return null;
    }

    private Token checkForFurthestToken (final TreeSet <Token> distanceSet,
                                         final Token previousToken)
    {
        Token token;
        if (previousToken != null) {
            token = distanceSet.higher (previousToken);
        } else {
            token = distanceSet.first ();
        }
        if (token == null) {
            token = previousToken;
        }
        return token;
    }

    private boolean checkForOtherStart (final List <Token> otherStartList,
                                        final Token token)
    {
        if (otherStartList.contains (token)) {
            return true;
        }
        return false;
    }

    private Token checkForTokenAtStart (final TreeSet <Token> distanceSet)
    {
        final Token token = distanceSet.last ();
        if (token.isOnStart () && token.getOwner ().hasTokensAtHome ()) {
            return token;
        }
        return null;
    }

    public void moveTokens (final TroubleGame game,
                            final List <Token> tokenList, final int moves)
    {
        final TreeSet <Token> distanceSet = new TreeSet <Token> (new DistanceComparator ());
        final TreeSet <Token> foeDistanceSet = new TreeSet <Token> (new DistanceComparator ());
        final Map <Token, Token> friendFoeMapping = new HashMap <Token, Token> ();
        final List <Token> otherStartList = new ArrayList <Token> ();

        final Iterator <Token> tokens = tokenList.iterator ();
        while (tokens.hasNext ()) {
            final Token token = tokens.next ();
            distanceSet.add (token);
            final Spot targetPosition = token.getTargetPosition (moves);
            if (targetPosition != null) {
                if (targetPosition.isStart () && !targetPosition.getOwner ().equals (token.getOwner ())) {
                    otherStartList.add (token);
                }
                final Token potentialToken = targetPosition.getCurrentToken ();
                if (potentialToken != null) {
                    foeDistanceSet.add (potentialToken);
                    friendFoeMapping.put (potentialToken, token);
                }
            }
        }

        Operation [] ops = new Operation [] { Operation.NOOP };
        switch (_currentPolicy) {
        case FAST:
            switch (_currentDifficulty) {
            case EASY:
                ops = new Operation [] { Operation.CHECK_FURTHEST };
                break;
            case MEDIUM:
                ops = new Operation [] {
                                Operation.CHECK_FURTHEST,
                                Operation.CHECK_OWN_START };
            case HARD:
                ops = new Operation [] {
                                Operation.CHECK_FURTHEST,
                                Operation.CHECK_OWN_START,
                                Operation.CHECK_OTHER_START };
            }
            break;
        case AGGRESSIVE:
            switch (_currentDifficulty) {
            case EASY:
                ops = new Operation [] {
                                Operation.CHECK_FURTHEST,
                                Operation.CHECK_FOE };
                break;
            case MEDIUM:
                ops = new Operation [] {
                                Operation.CHECK_FURTHEST,
                                Operation.CHECK_FOE,
                                Operation.CHECK_OWN_START };
                break;
            case HARD:
                ops = new Operation [] {
                                Operation.CHECK_FURTHEST,
                                Operation.CHECK_OTHER_START,
                                Operation.CHECK_FOE,
                                Operation.CHECK_OWN_START };
                break;
            }
            break;
        case NICE:
        default:
            break;
        }
        final Token tokenToMove = runOperations (ops, tokenList, distanceSet,
                                                 foeDistanceSet,
                                                 friendFoeMapping,
                                                 otherStartList);
        game.selectToken (tokenToMove);
    }

    private Token runOperations (final Operation [] ops,
                                 final List <Token> tokenList,
                                 final TreeSet <Token> distanceSet,
                                 final TreeSet <Token> foeDistanceSet,
                                 final Map <Token, Token> friendFoeMapping,
                                 final List <Token> otherStartList)
    {
        Token token = tokenList.get (0);
        for (final Operation op : ops) {
            final Token currentToken = token;
            switch (op) {
            case CHECK_OWN_START:
                token = checkForTokenAtStart (distanceSet);
                break;
            case CHECK_FURTHEST:
                token = checkForFurthestToken (distanceSet, null);
                break;
            case CHECK_FOE:
                token = checkForFurthestFoeToken (foeDistanceSet,
                                                  friendFoeMapping);
                break;
            case CHECK_OTHER_START:
                boolean checkResult = false;
                Token lastValidToken = token;
                do {
                    checkResult = checkForOtherStart (otherStartList, token);
                    if (checkResult) {
                        token = checkForFurthestToken (distanceSet, token);
                        lastValidToken = token;
                    }
                } while ((token != null) && checkResult);
                if (token == null) {
                    token = lastValidToken;
                }
                break;
            default:
                break;
            }
            if (token == null) {
                token = currentToken;
            }
        }
        return token;
    }
}
