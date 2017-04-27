/** **************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *************************************************** */
package net.java.cargotracker.application.util.reactive;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.transaction.*;

/**
 *
 * @author Ondrej Mihalyi
 */
public class TransactionContext {

    Transaction txCtx;

    public TransactionContext() {
        TransactionManager tm = getTM();
        suspendTx();
    }
    
    public Runnable inTransaction(Runnable action) {
        return () -> {
            try {
                resumeTx();
                action.run();
            } catch (Exception ex) {
                throw new CompletionException(ex);
            } finally {
                suspendTx();
            }
        };
    }

    public <T, R> Function<T, R> inTransaction(Function<T, R> action) {
        return x -> {
            try {
                resumeTx();
                return action.apply(x);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            } finally {
                suspendTx();
            }
        };
    }

    public <T> Consumer<T> inTransaction(Consumer<T> action) {
        return x -> {
            try {
                resumeTx();
                action.accept(x);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            } finally {
                suspendTx();
            }
        };
    }

    private TransactionManager getTM() {
        return ResourceUtil.lookupCurrentTransactionManager();
    }

    ;

    private void suspendTx() throws CompletionException {
        try {
            txCtx = getTM().suspend();
        } catch (SystemException ex) {
            throw new CompletionException(ex);
        }
    }

    private void resumeTx() {
        if (txCtx != null) {
            try {
                getTM().resume(txCtx);
            } catch (IllegalStateException | InvalidTransactionException | SystemException ex) {
                throw new CompletionException(ex);
            }
        }
    }

}
