/*
 * Copyright (C) 2007-2014 JMPEAX.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jmpeax.osgi.undertow.http;

import com.jmpeax.osgi.undertow.http.impl.UndertowHttpServer;
import com.jmpeax.osgi.undertow.http.impl.UndertowHttpService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Properties;

/**
 * Bundle activator for Undertow http service implementation.
 * @since 1.0.0
 * @author Carlos Ortiz
 */
public class UndertowHttpServiceActivator implements BundleActivator {

    /**
     * Internal Logger.
     */
    private Logger log = LoggerFactory.getLogger(UndertowHttpServiceActivator.class);
    /**
     * Properties of the service.
     */
    private Dictionary<String, ?> configuration;
    private ServiceRegistration<?> httpService;
    private UndertowHttpServer server;

    @Override
    public void start(final BundleContext context) throws Exception {
        defaults();
        server = new UndertowHttpServer(configuration);
        httpService = context.registerService(HttpService.class, new UndertowHttpService(server), null);
        server.startServer();
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        if (httpService != null) {
            log.debug("Unregister Http service");
            httpService.unregister();
        }
        if (server != null) {
            log.debug("Stopping Http Server");
            server.stopServer();
        }
    }

    private Dictionary defaults() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/undertow-defaults.properties"));
        return properties;
    }
}
