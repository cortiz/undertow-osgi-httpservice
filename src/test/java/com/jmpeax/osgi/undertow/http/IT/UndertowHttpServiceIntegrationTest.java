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

package com.jmpeax.osgi.undertow.http.IT;


import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.osgi.service.http.HttpService;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * Created by cortiz on 15/11/14.
 */
@Listeners(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class UndertowHttpServiceIntegrationTest {

    @Inject
    private HttpService httpService;

    @Configuration
    public Option[] config() {

        return options(
        );
    }




    @Test
    public void testHttpServiceNotNull() throws Exception {
        assertNotNull(httpService);
        System.out.println(httpService.getClass());
    }
}
