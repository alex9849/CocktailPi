package net.alex9849.cocktailpi.model.pump;

import java.util.concurrent.CompletableFuture;

public interface ILoadCellReader {

    CompletableFuture<Long> readFromNow();

    CompletableFuture<Long> readFromNow(int readRounds);

    CompletableFuture<Long> readCurrent();

    CompletableFuture<Long> readCurrent(int rounds);
}
