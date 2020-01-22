package de.mennomax.astikorcarts.oregon;

import java.util.Random;

public abstract class BasicProgram implements Runnable {
    private final IO io;

    private final Random rng;

    private final StringBuilder line;

    protected BasicProgram(final IO io) {
        this.io = io;
        this.rng = new Random();
        this.line = new StringBuilder();
    }

    protected final int round(final float x) {
        final int i = (int) x;
        return x < i ? i - 1 : i;
    }

    protected final float inputNumeric() {
        while (true) {
            final String s = this.inputString();
            try {
                return Float.parseFloat(s);
            } catch (final NumberFormatException e) {
                this.io.print("INVALID INPUT");
            }
        }
    }

    protected final String inputString() {
        return this.io.input();
    }

    protected final float rnd() {
        return this.rng.nextFloat();
    }

    protected final float pow(final float a, final float b) {
        return (float) Math.pow(a, b);
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
        final StringBuilder bob = new StringBuilder();
        for (final Object o : arr) {
            bob.append(String.format("%1$-15s", o));
        }
        this.io.print(bob.toString());
    }

    public interface IO {
        String input();

        void print(final String s);
    }
}
