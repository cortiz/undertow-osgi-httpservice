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

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class UndertowHttpServiceActivatorTest {

    private BundleContext dummyBundleContext;

    public static void main(String[] args) throws IOException, ServletException {
        PathHandler pathHandler = new PathHandler();
        pathHandler.addExactPath("/hello", new HttpHandler() {
            @Override
            public void handleRequest(final HttpServerExchange exchange) throws Exception {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello World");
            }
        });
        Undertow server = Undertow.builder().addHttpListener(9090, "localhost")
                .setHandler(pathHandler).build();
        server.start();
        pathHandler.addExactPath("/bye", new HttpHandler() {
            @Override
            public void handleRequest(final HttpServerExchange exchange) throws Exception {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("bye World");
            }
        });
        DeploymentInfo servletBuilder = Servlets.deployment().addServlet(Servlets.servlet("sample", SampleServlet
                .class).addMapping("/*")).setDeploymentName("Test").setContextPath("/servlets").setClassLoader
                (UndertowHttpServiceActivatorTest.class.getClassLoader());
        final DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        pathHandler.addExactPath("/servlets", manager.start());

    }

    @BeforeMethod
    public void setUp() throws Exception {
        dummyBundleContext= Mockito.mock(BundleContext.class);
    }

    @Test
    public void testStart() throws Exception {
        dummyBundleContext.notify();
    }

    @Test
    public void testStop() throws Exception {
        // new UndertowHttpServiceActivator().stop(dummyBundleContext);
    }

}