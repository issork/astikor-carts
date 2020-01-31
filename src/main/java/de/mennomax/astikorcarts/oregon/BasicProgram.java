package de.mennomax.astikorcarts.oregon;

import java.util.Random;

public abstract class BasicProgram implements Runnable {
    private final IO io;

    private final Random rng;

    private final StringBuilder line;

    protected BasicProgram(final IO io, final Random rng) {
        this.io = io;
        this.rng = rng;
        this.line = new StringBuilder();
    }

    protected final int prompt(final int lower, final int upper) throws InterruptedException {
        return this.io.prompt(lower, upper);
    }

    protected final String prompt(final String... options) throws InterruptedException {
        return this.io.prompt(options);
    }

    protected final float rnd() {
        return this.rng.nextFloat();
    }

    protected final int rnd(final int n) {
        return this.rng.nextInt(n);
    }

    protected final float pow(final float a, int b) {
        if (b < 0) {
            throw new IllegalArgumentException();
        }
        if (b == 0) {
            return 1;
        }
        float q = a;
        while (--b > 0) q *= a;
        return q;
    }

    protected final long clk() {
        return System.currentTimeMillis();
    }

    protected final void print(final Object o) {
        final String s = String.valueOf(o);
        this.line.append(s);
        if (!s.endsWith(" ")) {
            this.io.print(this.line.toString());
            this.line.setLength(0);
        }
    }

    protected final void print(final Object... arr) {
        if (arr.length > 0) {
            for (int i = 0; i < arr.length - 1; i++) {
                this.line.append(String.format("%1$-15s", arr[i]));
            }
            this.line.append(arr[arr.length - 1]);
        }
        this.io.print(this.line.toString());
        this.line.setLength(0);
    }

    public interface IO {
        int prompt(final int lower, final int upper) throws InterruptedException;

        String prompt(final String... options) throws InterruptedException;

        void print(final String s);
    }
}
