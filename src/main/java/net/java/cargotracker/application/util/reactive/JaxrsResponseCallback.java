package net.java.cargotracker.application.util.reactive;

import java.util.List;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.concurrent.ContextService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.GenericType;
import net.java.pathfinder.api.TransitPath;

/**
 * A generic JAR-RS client response callback that is also a CompletableFuture.
 * It is meant to be used in async get method of JAX-RS client. It is
 * recommended to use the static {@code JaxrsResponseCallback.get} method to
 * enable chaining callbacks to the CompletableFuture fluently:
 *
 * <pre>
 * {@code
 * JaxrsResponseCallback.get(ClientBuilder.newClient()
 * .request()
 * .async())
 * .thenAccept(
 * response -> {
 * System.out.println("Response code " + response.getStatus()
 * + ", with content: " + response.readEntity(String.class));
 * }
 * )
 * }
 * </pre>
 *
 */
public class JaxrsResponseCallback<T> extends CompletableFuture<T> implements InvocationCallback<Response> {

    private Consumer<Response> onCompleted;

    private Consumer<Throwable> onFailed;
    
    private GenericType<T> type;

    @Override
    public void completed(Response response) {
        onCompleted.accept(response);
    }

    @Override
    public void failed(Throwable throwable) {
        onFailed.accept(throwable);
    }

    private JaxrsResponseCallback(ContextService contextService, GenericType<T> type) {
        this.type = type;
        onCompleted = contextService.createContextualProxy(new Consumer<Response>() {
            @Override
            public void accept(Response response) {
                if (type == null) {
                    JaxrsResponseCallback.super.complete((T)response);
                } else {
                    JaxrsResponseCallback.super.complete(response.readEntity(type));
                }
            }

        }, Consumer.class);
        
        onFailed = contextService.createContextualProxy(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                JaxrsResponseCallback.super.completeExceptionally(t);
            }

        }, Consumer.class);
    }

    public static CompletionStage<Response> get(AsyncInvoker invoker) {
        final JaxrsResponseCallback<Response> completion = new JaxrsResponseCallback<>(lookupDefaultContextService(), null);
        invoker.get(completion);
        return completion;
    }

    public static <XT> CompletionStage<XT> get(AsyncInvoker invoker, GenericType<XT> type) {
        final JaxrsResponseCallback<XT> completion = new JaxrsResponseCallback<>(lookupDefaultContextService(), type);
        invoker.get(completion);
        return completion;
    }

    private static ContextService lookupDefaultContextService() {
        ContextService ctxService = null;
        try {
            ctxService = (ContextService) (new InitialContext().lookup("java:comp/DefaultContextService"));
        } catch (NamingException ex) {
            Logger.getLogger(JaxrsResponseCallback.class.getName()).log(Level.FINE, null, ex);
        }
        return ctxService;
    }

}
