/**
 * Copyright (c) 2016-2019 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplerest
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.evchargelog.resources;

import java.security.Principal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.bitplan.rest.resources.TemplateResource;

@Path("/hello")
/**
 * Simple Hello Resource
 * 
 * @author wf
 *
 */
public class HelloResource extends TemplateResource {
  public static Principal currentUser=null;
  
  public static void setDebug(boolean debug) {
    TemplateResource.debug=true;
  }
  
  @GET
  public String getHello() {
    currentUser=getPrincipal();
    return "Hello";
  }

  @GET
  @Produces("text/html")
  @Path("redirect")
  public Response getRedirect() {
    return redirect("hello/echo/redirect");
  }

  @GET
  @Produces("text/html")
  @Path("echo/{value}")
  public String getEcho(@PathParam("value") String value) {
    currentUser=getPrincipal();
    return value;
  }
}
