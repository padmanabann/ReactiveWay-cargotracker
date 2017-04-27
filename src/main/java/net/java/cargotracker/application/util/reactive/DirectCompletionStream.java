package net.java.cargotracker.application.util.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

public class DirectCompletionStream<ITEM_TYPE> implements CompletionStream<ITEM_TYPE> {
    private final CompletableFuture<Consumer<CompletionStage<ITEM_TYPE>>> initialItemCF = new CompletableFuture<>();
    private CompletionStage<Consumer<CompletionStage<ITEM_TYPE>>> lastItemCF = initialItemCF;
    private final CompletableFuture<Void> finishedCF = new CompletableFuture<>();
    
    public static <T> DirectCompletionStream<T> empty() {
        final DirectCompletionStream<T> emptyCompletionSream = new DirectCompletionStream<>();
        emptyCompletionSream.processingFinished();
        return emptyCompletionSream;    
    }
    
    @Override
    public CompletionStream<ITEM_TYPE> acceptEach(Consumer<CompletionStage<ITEM_TYPE>> consumer) {
        initialItemCF.complete(consumer);
        return this;
    }

    @Override
    public CompletionStage<Void> whenFinished() {
        return finishedCF;
    }
    
    public void itemProcessed(ITEM_TYPE item) {
        lastItemCF = lastItemCF.thenApply(consumer -> {
           consumer.accept(CompletableFuture.completedFuture(item));
           return consumer;
        });
    }
    
    public void processingFinished() {
        finishedCF.complete(null);
    }

    @Override
    public <NEW_TYPE> CompletionStream<NEW_TYPE> applyToEach(Function<CompletionStage<ITEM_TYPE>, CompletionStage<NEW_TYPE>> function) {
        DirectCompletionStream<NEW_TYPE> result = new DirectCompletionStream<>();
        this.acceptEach(stage -> {
            function.apply(stage)
                .thenAccept(newItem -> {
                    result.itemProcessed(newItem);
                });
        })
        .whenFinished()
        .thenRun(result::processingFinished);
        return result;
    }

}
