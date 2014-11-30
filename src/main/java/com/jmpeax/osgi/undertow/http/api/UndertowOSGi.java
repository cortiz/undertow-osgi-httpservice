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

package com.jmpeax.osgi.undertow.http.api;

/**
 * Created by cortiz on 9/10/14.
 */
public final class UndertowOSGi {

    public static final String SERVLET_MAPPING = "undertow.osgi.servletMapping";
    public static final String DEFAULT_SERVLET_MAPPING = "/*";
    public static final String CONTEXT_NAME = "undertow.osgi.contextName";
    public static final String DEFAULT_CONTEXT_NAME = "undertowHttpServiceContext";

    /**
     * Prevents this class to be instance.
     */
    private UndertowOSGi(){}

}
