package net.alex9849.cocktailpi.model.pump;

import net.alex9849.motorlib.exception.HX711Exception;
import net.alex9849.motorlib.sensor.HX711;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoadCellReader extends Thread {
    private static class Request {
        CompletableFuture<Long> future;
        int rounds;
    }

    private final Map<Integer, List<Request>> taskMap = new HashMap();
    private final HX711 hx711;
    private final long[] dataPoints;
    private int dataPointIdx = 0;
    private boolean arrayFilled = false;
    private boolean running;

    public LoadCellReader(HX711 hx711, int dataPoints) {
        this.hx711 = hx711;
        this.dataPoints = new long[dataPoints];
        this.running = true;
        this.start();
    }

    @Override
    public void run() {
        long startTime;
        while (!interrupted() && running) {
            try {
                startTime = System.currentTimeMillis();
                try {
                    dataPoints[dataPointIdx] = hx711.read_once();
                    synchronized (taskMap) {
                        if(arrayFilled && taskMap.containsKey(dataPointIdx)) {
                            for(Request request : taskMap.get(dataPointIdx)) {
                                request.future.complete(mean(request.rounds));
                            }
                            taskMap.remove(dataPointIdx);
                        }
                        dataPointIdx = (dataPointIdx + 1) % dataPoints.length;
                        arrayFilled |= dataPointIdx == 0;
                    }
                } catch (HX711Exception e) {
                    e.printStackTrace();
                }
                long waitTime = 10 - (System.currentTimeMillis() - startTime);
                if(waitTime > 0) {
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                running = false;
                return;
            }

        }
        running = false;
    }

    public void shutdown() {
        synchronized (taskMap) {
            this.running = false;
            for(List<Request> list : taskMap.values()) {
                for(Request req : list) {
                    req.future.completeExceptionally(new HX711Exception("Load cell shut down"));
                }
            }
        }
    }

    public void shutdownBlocking() throws InterruptedException {
        shutdown();
        this.join();
    }

    private long mean(int rounds) {
        int rangeMin = (dataPointIdx - rounds + 1) % dataPoints.length;
        if(rangeMin < 0) {
            rangeMin += dataPoints.length;
        }
        long[] dataCopy = new long[rounds];
        for(int i = 0; i < rounds; i++) {
            dataCopy[i] = dataPoints[(rangeMin + i) % dataPoints.length];
        }
        Arrays.sort(dataCopy);
        if (dataCopy.length % 2 == 0)
            return (long) (((double)dataCopy[dataCopy.length/2] + (double)dataCopy[dataCopy.length/2 - 1])/2);
        else
            return dataCopy[dataCopy.length/2];
    }

    public Future<Long> readCurrent() {
        return readCurrent(dataPoints.length);
    }

    public Future<Long> readCurrent(int rounds) {
        synchronized (taskMap) {
            if(!running) {
                throw new HX711Exception("Load cell shut down");
            }
            if(arrayFilled) {
                CompletableFuture<Long> future = new CompletableFuture<>();
                future.complete(mean(rounds));
                return future;
            }
            List<Request> list = taskMap.computeIfAbsent(dataPoints.length - 1, k -> new ArrayList<>());
            CompletableFuture<Long> future = new CompletableFuture<>();
            Request request = new Request();
            request.future = future;
            request.rounds = rounds;
            list.add(request);
            return future;
        }
    }

    public Future<Long> readFromNow() {
        return readFromNow(dataPoints.length);
    }

    public Future<Long> readFromNow(int readRounds) {
        if(readRounds > dataPoints.length) {
            throw new IllegalArgumentException("readRounds > dataPoints.length");
        }
        synchronized (taskMap) {
            if(!running) {
                throw new HX711Exception("Load cell shut down");
            }
            int completionIdx = (dataPointIdx + readRounds - 1) % dataPoints.length;
            if(completionIdx < 0) {
                completionIdx += dataPoints.length;
            }
            List<Request> list = taskMap.computeIfAbsent(completionIdx, k -> new ArrayList<>());
            CompletableFuture<Long> future = new CompletableFuture<>();
            Request request = new Request();
            request.future = future;
            request.rounds = readRounds;
            list.add(request);
            return future;
        }
    }
}
