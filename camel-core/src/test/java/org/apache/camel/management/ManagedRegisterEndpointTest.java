/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.management;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.builder.RouteBuilder;

/**
 * @version 
 */
public class ManagedRegisterEndpointTest extends ManagementTestSupport {

    public void testLookupEndpointsByName() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }

        MBeanServer mbeanServer = getMBeanServer();

        ObjectName name = ObjectName.getInstance("org.apache.camel:context=localhost/camel-1,type=endpoints,name=\"direct://start\"");
        String uri = (String) mbeanServer.getAttribute(name, "EndpointUri");
        assertEquals("direct://start", uri);

        name = ObjectName.getInstance("org.apache.camel:context=localhost/camel-1,type=endpoints,name=\"log://foo\"");
        uri = (String) mbeanServer.getAttribute(name, "EndpointUri");
        assertEquals("log://foo", uri);

        name = ObjectName.getInstance("org.apache.camel:context=localhost/camel-1,type=endpoints,name=\"mock://result\"");
        uri = (String) mbeanServer.getAttribute(name, "EndpointUri");
        assertEquals("mock://result", uri);

        String id = (String) mbeanServer.getAttribute(name, "CamelId");
        assertEquals("camel-1", id);

        String state = (String) mbeanServer.getAttribute(name, "State");
        assertEquals("Started", state);

        Boolean singleton = (Boolean) mbeanServer.getAttribute(name, "Singleton");
        assertEquals(Boolean.TRUE, singleton);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("log:foo").to("mock:result");
            }
        };
    }

}
