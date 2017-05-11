package net.java.cargotracker.application.util.reactive;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A generic JAR-RS client response callback that is also a CompletableFuture. It is meant to be used in async get method of JAX-RS client.
 * It is recommended to use the static {@code JaxrsResponseCallback.get} method to enable chaining callbacks to the CompletableFuture fluently:
 * 
 * <pre>
 * {@code
    JaxrsResponseCallback.get(ClientBuilder.newClient()
        .request()
        .async())
        .thenAccept(
            response -> {
                System.out.println("Response code " + response.getStatus()
                        + ", with content: " + response.readEntity(String.class));
            }
        )  
 * }
 * </pre>
 * 
 * Created by mertcaliskan
 */
public class JaxrsResponseCallback extends CompletableFuture<Response> implements InvocationCallback<Response> {

    @Override
    public void completed(Response response) {
        super.complete(response);
    }

    @Override
    public void failed(Throwable throwable) {
        super.completeExceptionally(throwable);
    }

    public static CompletionStage<Response> get(AsyncInvoker invoker) {
        final JaxrsResponseCallback completion = new JaxrsResponseCallback();
        invoker.get(completion);
        return completion;
    }
}