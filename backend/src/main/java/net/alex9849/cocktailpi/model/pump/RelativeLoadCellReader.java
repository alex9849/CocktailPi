package net.alex9849.cocktailpi.model.pump;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RelativeLoadCellReader implements ILoadCellReader {
    private final LoadCellReader loadCellReader;
    private long tareValue = 0;

    public RelativeLoadCellReader(LoadCellReader loadCellReader) {
        this.loadCellReader = loadCellReader;
    }

    private CompletableFuture<Long> transformFuture(CompletableFuture<Long> future) {
        return future.thenApply(v -> v - this.tareValue);
    }

    @Override
    public CompletableFuture<Long> readFromNow() {
        return transformFuture(this.loadCellReader.readFromNow());
    }

    @Override
    public CompletableFuture<Long> readFromNow(int readRounds) {
        return transformFuture(this.loadCellReader.readFromNow(readRounds));
    }

    public CompletableFuture<Long> readCurrent() {
        return transformFuture(this.loadCellReader.readCurrent());
    }

    @Override
    public CompletableFuture<Long> readCurrent(int rounds) {
        return transformFuture(this.loadCellReader.readCurrent(rounds));
    }

    public void tare(boolean tareFromPast) throws ExecutionException, InterruptedException {
        if (tareFromPast) {
            this.tareValue = this.loadCellReader
                    .readCurrent(5).get();
        } else {
            this.tareValue = this.loadCellReader
                    .readFromNow(5).get();
        }
    }
}
