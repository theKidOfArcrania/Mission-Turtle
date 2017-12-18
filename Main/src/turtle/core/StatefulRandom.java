package turtle.core;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This extension of random allows preserving of the current state. Used so
 * that the values generated from this random class will be able to be easily
 * rewound to an earlier state.
 *
 * @author Henry Wang
 */
public class StatefulRandom extends Random {
    public static final long MULTIPLIER = 0x5DEECE66DL;
    public static final long MASK = (1L << 48) - 1;
    public static final int MAX_BITS = 48;
    public static final long ADDEND = 0xBL;
    private static final long serialVersionUID = -3171683136036560219L;

    private final AtomicLong seed;
    private final AtomicLong state;

    /**
     * Constructs a new random instance seeded with our current time.
     */
    public StatefulRandom() {
        this(System.currentTimeMillis());
    }

    /**
     * Constructs a new rewindable random instance.
     *
     * @param seed the seed to initialize with
     */
    public StatefulRandom(long seed) {
        this.seed = new AtomicLong(seed);
        state = new AtomicLong(this.seed.get());
        next(0);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public StatefulRandom clone() {
        StatefulRandom copy = new StatefulRandom(seed.get());
        copy.setState(state.get());
        return copy;
    }

    /**
     * Obtains the next bits of randomness.
     *
     * @param bits the number of bits to obtain.
     * @return next bits of randomness
     */
    protected int next(int bits) {
        long oldSeed, nextSeed;
        AtomicLong seed = this.state;
        do {
            oldSeed = seed.get();
            nextSeed = (oldSeed * MULTIPLIER + ADDEND) & MASK;
        } while (!seed.compareAndSet(oldSeed, nextSeed));
        return (int) (nextSeed >>> (MAX_BITS - bits));
    }

    /**
     * Gets the current state for randomness
     *
     * @return the state for randomness
     */
    public long getState() {
        return state.get();
    }

    /**
     * Sets the state of this random to a new state.
     *
     * @param state the state to set to
     */
    public synchronized void setState(long state) {
        this.state.set(state);
    }

    /**
     * Getter for the current seed used to initialize the random object at.
     *
     * @return the seed value used.
     */
    public long getSeed() {
        return seed.get();
    }

    /**
     * Sets the state of this random to a new seed. Note that this does not
     * do the same thing as set to a new state, since this will first
     * scramble the seed to create the initial state.
     *
     * @param seed the seed to initialize randomness
     */
    public synchronized void setSeed(long seed) {
        if (this.seed != null) {
            this.seed.set(seed);
            setState(seed);
            next(0);
        }
    }

    /**
     * Generates a gaussian distributed number without setting a next next
     * gaussian, since our state is volatile to change.
     *
     * @return a gaussian-distributed number with mean of 0 and standard
     * deviance of 1.
     * @see Random#nextGaussian()
     */
    public synchronized double nextGaussian() {
        double v1, v2, s;
        do {
            v1 = 2 * nextDouble() - 1; // between -1 and 1
            v2 = 2 * nextDouble() - 1; // between -1 and 1
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
        return v1 * multiplier;
    }
}
