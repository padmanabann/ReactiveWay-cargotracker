package net.java.cargotracker.infrastructure.routing;

import fish.payara.micro.cdi.Inbound;
import fish.payara.micro.cdi.Outbound;
import net.java.pathfinder.api.TransitPath;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import net.java.cargotracker.application.util.reactive.CompletionStream;
import net.java.cargotracker.application.util.reactive.DirectCompletionStream;
import net.java.pathfinder.api.reactive.GraphTraversalRequest;
import net.java.pathfinder.api.reactive.GraphTraversalResponse;

@ApplicationScoped
class GraphTraversalResource {

    @Inject
    @Outbound(loopBack = true)
    private Event<GraphTraversalRequest> requestEvent;
    
    private ConcurrentHashMap<Long,DirectCompletionStream<TransitPath>> completionMap = new ConcurrentHashMap<>();
            
    public CompletionStream<TransitPath> get(String origin, String destination) {
        DirectCompletionStream<TransitPath> completion = new DirectCompletionStream<>();
        final GraphTraversalRequest request = new GraphTraversalRequest(origin, destination);
        completionMap.put(request.getId(), completion);
        requestEvent.fire(request);
        return completion;
    }
    
    public void handleResponse(@Observes @Inbound GraphTraversalResponse response) {
        DirectCompletionStream<TransitPath> completion = null;
        if (response.isCompleted()) {
            completion = completionMap.remove(response.getId());
        } else {
            completion = completionMap.get(response.getId());
        }
        if (completion != null) {
            if (response.getTransitPath() != null) {
                completion.itemProcessed(response.getTransitPath());
            }
            if (response.isCompleted()) {
                completion.processingFinished();
            }
        }
    }

}
