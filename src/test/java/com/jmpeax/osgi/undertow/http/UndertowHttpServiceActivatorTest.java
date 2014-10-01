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

import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UndertowHttpServiceActivatorTest {

    private BundleContext dummyBundleContext;

    @BeforeMethod
    public void setUp() throws Exception {
        dummyBundleContext= Mockito.mock(BundleContext.class);
    }

    @Test
    public void testStart() throws Exception {
        new UndertowHttpServiceActivator().start(dummyBundleContext);
    }

    @Test
    public void testStop() throws Exception {
        new UndertowHttpServiceActivator().stop(dummyBundleContext);
    }
}