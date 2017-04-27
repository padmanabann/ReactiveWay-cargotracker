/****************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 ****************************************************/

package net.java.cargotracker.application.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

/**
 *
 * @author Ondrej Mihalyi
 */
@ApplicationScoped
public class ApplicationInfo {
    private ServletContext servletContext;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(@Observes @Initialized(ApplicationScoped.class) ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
}

