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

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import org.osgi.framework.BundleException;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages the undertow server, also is the one who does the actual load/unload of the servlets.
 *
 * @author Carlos Ortiz
 * @since 1.0.0
 */
public class UndertowHttpServer {


    private final Logger log = LoggerFactory.getLogger(UndertowHttpServer.class);
    protected Dictionary<String, ?> properties;
    protected Undertow server;
    protected PathHandler pathHandler;
    protected Set<String> registeredPaths;

    public UndertowHttpServer(Dictionary<String, ?> properties) {
        this.properties = properties;
        pathHandler = new PathHandler();
        registeredPaths = new HashSet<>();
    }

    public void startServer() throws BundleException {
        Undertow.Builder serverBuilder = Undertow.builder();
        try {
            int port = Integer.parseInt(properties.get("undertow.server.http.port").toString());
            String host = properties.get("undertow.server.http.host").toString();
            log.debug("Starting Http Listener {}:{}", host, port);
            serverBuilder.addHttpListener(port, host);

        } catch (NumberFormatException | NullPointerException ex) {
            log.error("Unable to start Http Server, missing configuration for host and port");
            log.debug("Reason:", ex);
            throw new BundleException("Unable to start Http service, configuration invalid!");
        }
        serverBuilder.setHandler(pathHandler);
        server = serverBuilder.build();
        server.start();
    }


    public void addServletHandler(final String url, final Servlet servlet, final Dictionary<String, ?> props) throws NamespaceException, ServletException {
        log.debug("Adding {} to url {} with properties {}", servlet, url, props);
        if (registeredPaths.contains(url)) {
            throw new NamespaceException("Url " + url + " already Exist");
        }
        DeploymentInfo servletBuilder = initServetHandler(url, servlet.getClass().getClassLoader());

        final ServletInfo tobeAdd = Servlets.servlet(url, servlet.getClass(), new InstanceFactory<Servlet>() {
            // OSGi already provides the instance!
            @Override
            public InstanceHandle<Servlet> createInstance() throws InstantiationException {
                return new ImmediateInstanceHandle<Servlet>(servlet);
            }
        });
        tobeAdd.addMapping("/*");
        servletBuilder.setContextPath(url);
        servletBuilder.setDeploymentName("TEST");
        servletBuilder.addServlet(tobeAdd);
        final DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        pathHandler.addExactPath(url, manager.start());
        log.debug("Servlet {} was added", url);
        registeredPaths.add(url);
    }

    public void addServletHandler(final String url, final Class<? extends Servlet> servlet,
                                  final Dictionary<String, ?> props) {
        DeploymentInfo servletBuilder = initServetHandler(url, servlet.getClassLoader());
        Servlets.servlet(url, servlet);
    }

    protected DeploymentInfo initServetHandler(final String url, ClassLoader classloader) {
        DeploymentInfo servletBuilder = Servlets.deployment();
        servletBuilder.setClassLoader(classloader);
        servletBuilder.setContextPath(url);
        return servletBuilder;
    }

    public void removeHandler(final String path) {
        pathHandler.removeExactPath(path);
        registeredPaths.remove(path);
        log.debug("{} was remove from server", path);
    }

    public void stopServer() {
        log.info("Stopping Http Server");
        log.debug("Clearing all paths");
        pathHandler.clearPaths();
        log.debug("Stopping all Http/Ajp services");
        server.stop();
        log.info("Http Server was shutdown");
    }

}
