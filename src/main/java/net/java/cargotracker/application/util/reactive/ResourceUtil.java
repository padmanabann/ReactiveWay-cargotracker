/****************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 ****************************************************/

package net.java.cargotracker.application.util.reactive;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.concurrent.ContextService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

/**
 *
 * @author Ondrej Mihalyi
 */
public class ResourceUtil {
    public static ContextService lookupDefaultContextService() {
        ContextService ctxService = null;
        try {
            ctxService = (ContextService) (new InitialContext().lookup("java:comp/DefaultContextService"));
        } catch (NamingException ex) {
            Logger.getLogger(ResourceUtil.class.getName()).log(Level.FINE, null, ex);
        }
        return ctxService;
    }

    public static TransactionManager lookupCurrentTransactionManager() {
        TransactionManager txManager = null;
        try {
            // this is a vendor specific way of retrieving TransactionManager - works in Payara Server and GlassFish
            txManager = (TransactionManager) (new InitialContext().lookup("java:appserver/TransactionManager"));
        } catch (NamingException ex) {
            Logger.getLogger(ResourceUtil.class.getName()).log(Level.FINE, null, ex);
        }
        return txManager;
    }


}

