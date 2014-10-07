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

import com.jmpeax.osgi.undertow.http.SampleServlet;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.osgi.framework.BundleException;
import org.osgi.service.http.NamespaceException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Dictionary;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@SuppressWarnings({"unchecked"})
public class UndertowHttpServerImplTest {


    private Properties goodProperties;

    private HttpClient httpTestClient;

    @BeforeMethod()
    public void setUp() throws Exception {
        goodProperties = new Properties();
        goodProperties.load(getClass().getResourceAsStream("/undertow-defaults.properties"));
        httpTestClient = HttpClientBuilder.create().build();
    }


    @Test
    @SuppressWarnings({""})
    public void testDefaultProperties() {
        assertNotNull(goodProperties.getProperty("undertow.server.http.host"));
        assertNotNull(goodProperties.getProperty("undertow.server.http.port"));
        Integer.parseInt(goodProperties.getProperty("undertow.server.http.port"));
    }

    @Test(dependsOnMethods = "testDefaultProperties")
    public void testStartServer() throws Exception {
        UndertowHttpServer testServer = new UndertowHttpServer((Dictionary) goodProperties);
        testServer.startServer();
        final HttpResponse result = httpTestClient.execute(buildHttpRequest("/thisShouldReturn404"));
        assertEquals(result.getStatusLine().getStatusCode(), 404);
        testServer.stopServer();
    }

    @Test(dependsOnMethods = "testStartServer")
    public void testSimpleRegister() throws Exception {
        UndertowHttpServer testServer = new UndertowHttpServer((Dictionary) goodProperties);
        testServer.startServer();
        testServer.addServletHandler("/thisMustExist", new SampleServlet(), null);
        final HttpResponse result = httpTestClient.execute(buildHttpRequest("/thisMustExist"));
        assertEquals(result.getStatusLine().getStatusCode(), HttpServletResponse.SC_OK);
        final InputStream in = result.getEntity().getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1)
            out.write(ch);
        final String str = new String(out.toByteArray(), Charset.forName(SampleServlet.ENCODING));
        assertEquals(str, SampleServlet.SEND_TEXT);
        assertEquals(result.getEntity().getContentType().getValue(), SampleServlet.CONTENT_TYPE);
        testServer.stopServer();
    }

    @Test(dependsOnMethods = "testSimpleRegister")
    public void testUnRegister() throws Exception {
        UndertowHttpServer testServer = new UndertowHttpServer((Dictionary) goodProperties);
        testServer.startServer();
        testServer.addServletHandler("/thisMustExist", new SampleServlet(), null);
        HttpResponse result = httpTestClient.execute(buildHttpRequest("/thisMustExist"));
        assertEquals(result.getStatusLine().getStatusCode(), HttpServletResponse.SC_OK);
        testServer.removeHandler("/thisMustExist");
        result = httpTestClient.execute(buildHttpRequest("/thisMustExist"));
        assertEquals(result.getStatusLine().getStatusCode(), HttpServletResponse.SC_NOT_FOUND);
        testServer.stopServer();
    }

    @Test(dependsOnMethods = "testStartServer", expectedExceptions = NamespaceException.class)
    public void testRegisterSameUrl() throws Exception {
        UndertowHttpServer testServer = new UndertowHttpServer((Dictionary) goodProperties);
        testServer.addServletHandler("/thisMustExist", new SampleServlet(), null);
        testServer.addServletHandler("/thisMustExist", new SampleServlet(), null);

    }

    @Test(dependsOnMethods = "testDefaultProperties", expectedExceptions = BundleException.class)
    public void testBadConfigMissingAll() throws Exception {
        Dictionary dic = new Properties();
        UndertowHttpServer testServer = new UndertowHttpServer(dic);
        testServer.startServer();
    }

    @Test(dependsOnMethods = "testDefaultProperties", expectedExceptions = BundleException.class)
    public void testBadConfigBadPort() throws Exception {
        Dictionary dic = new Properties();
        dic.put("undertow.server.http.port", "80.80");
        dic.put("undertow.server.http.host", "127.0.0.1");
        UndertowHttpServer testServer = new UndertowHttpServer(dic);
        testServer.startServer();
    }

    @Test(dependsOnMethods = "testDefaultProperties", expectedExceptions = HttpHostConnectException.class,
            expectedExceptionsMessageRegExp = ".* failed: Connection refused")
    public void testStopServer() throws Exception {
        UndertowHttpServer testServer = new UndertowHttpServer((Dictionary) goodProperties);
        testServer.startServer();
        final HttpResponse result = httpTestClient.execute(buildHttpRequest("/thisShouldReturn404"));
        assertEquals(result.getStatusLine().getStatusCode(), 404);
        testServer.stopServer();
        httpTestClient.execute(buildHttpRequest("/thisShouldFailsServerIsDown"));
    }

    private HttpGet buildHttpRequest(final String path) throws URISyntaxException {
        final URIBuilder builder = new URIBuilder();
        builder.setHost(goodProperties.getProperty("undertow.server.http.host"));
        builder.setPort(Integer.parseInt(goodProperties.getProperty("undertow.server.http.port")));
        builder.setScheme("http");
        builder.setPath(path);
        return new HttpGet(builder.build());
    }


}