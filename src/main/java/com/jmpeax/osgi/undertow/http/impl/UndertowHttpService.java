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

package com.jmpeax.osgi.undertow.http.impl;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.util.Dictionary;

/**
 * Implementation of HttpService internally uses {@link com.jmpeax.osgi.undertow.http.impl.UndertowHttpServer}
 *
 * @author Carlos Ortiz.
 * @since 1.0.0.
 */
public class UndertowHttpService implements HttpService {

    private UndertowHttpServer httpServer;

    private Logger log = LoggerFactory.getLogger(UndertowHttpService.class);

    public UndertowHttpService(UndertowHttpServer httpServer) {
        this.httpServer = httpServer;
    }

    @Override
    public void registerServlet(final String alias, final Servlet servlet, final Dictionary initparams, final HttpContext context) throws ServletException, NamespaceException {
        log.debug("Registering {} with servlet {} properties {} and context {}", alias, servlet, initparams, context);
        httpServer.addServletHandler(alias, servlet, initparams);
    }

    @Override
    public void registerResources(final String alias, final String name, final HttpContext context) throws NamespaceException {

    }

    @Override
    public void unregister(final String alias) {
        httpServer.removeHandler(alias);
    }

    @Override
    public HttpContext createDefaultHttpContext() {
        return null;
    }
}
