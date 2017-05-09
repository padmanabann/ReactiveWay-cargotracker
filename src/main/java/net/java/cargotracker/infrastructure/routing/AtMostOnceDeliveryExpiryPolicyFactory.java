package net.java.cargotracker.infrastructure.routing;

import javax.cache.configuration.Factory;
import javax.cache.expiry.*;

/**
 *
 * @author Ondrej Mihalyi
 */
public class AtMostOnceDeliveryExpiryPolicyFactory implements Factory<ExpiryPolicy> {

    @Override
    public ExpiryPolicy create() {
        return new CreatedExpiryPolicy(Duration.ONE_MINUTE);
    }
    
}

