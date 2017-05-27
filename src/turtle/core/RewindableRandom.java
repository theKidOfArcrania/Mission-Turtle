package turtle.core;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RewindableRandom.java
 *
 * This extension of random allows preserving of the current state. Used so
 * that the values generated from this random class will be able to be easily
 * redone. See the reference for source
 *
 * @author Henry Wang
 * @see
 *  <a href="http://www.java-gaming.org/index.php?PHPSESSID=c1c997k5c59rm6t9ko4pp5bdu5&topic=27607.msg247734#msg247734">Link to code</a>
 */
public class RewindableRandom extends Random
{
    public static final long MULTIPLIER = 0x5DEECE66DL;
    public static final long MASK = (1L << 48) - 1;
    public static final int MAX_BITS = 48;
    public static final long ADDEND = 0xBL;

    private final AtomicLong state;

    /**
     * Constructs a new rewindable random instance seeded with our current time.
     */
    public RewindableRandom()
    {
        state = new AtomicLong(System.currentTimeMillis());
        next(0);
    }

    /**
     * Constructs a new rewindable random instance.
     * @param seed the seed to initialize with
     */
    public RewindableRandom(long seed)
    {
        state = new AtomicLong(seed);
    }

    /**
     * Obtains the next bits of randomness.
     * @param bits the number of bits to obtain.
     * @return next bits of randomness
     */
    protected int next(int bits)
    {
        long oldseed, nextseed;
        AtomicLong seed = this.state;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * MULTIPLIER + ADDEND) & MASK;
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int)(nextseed >>> (MAX_BITS - bits));
    }

    /**
     * Gets the current state for randomness
     * @return the state for randomness
     */
    public long getState()
    {
        return state.get();
    }

    /**
     * Sets the seed to a new state
     * @param state the state to set to
     */
    public void setState(long state)
    {
        this.state.set(state);
    }
}
