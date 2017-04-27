package net.java.cargotracker.domain.service;

import java.util.List;
import java.util.concurrent.CompletionStage;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;

public interface RoutingService {

    /**
     * @param routeSpecification route specification
     * @return A list of itineraries that satisfy the specification. May be an
     * empty list if no route is found.
     */
    CompletionStage<List<Itinerary>> fetchRoutesForSpecification(RouteSpecification routeSpecification);
}
