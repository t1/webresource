package com.github.t1.webresource;

/** runs a {@link Runnable} exactly once */
public class Lazy {
    private final Runnable runnable;
    private boolean ran = false;

    public Lazy(Runnable runnable) { this.runnable = runnable; }

    public void reset() {
        this.ran = false;
    }

    public Lazy setRan(boolean ran) {
        this.ran = ran;
        return this;
    }

    public void run() {
        if (!ran) {
            runnable.run();
            this.ran = true;
        }
    }

    public boolean ran() {
        return ran;
    }
}
