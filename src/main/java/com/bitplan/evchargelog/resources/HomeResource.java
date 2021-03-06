/**
 * Copyright (c) 2019 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.evchargelog
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Jersey Resource for Home
 */
@SuppressWarnings("rawtypes")
@Path("/home")
public class HomeResource extends EVChargeLogResource {
 
  /**
   * constructor
   */
  public HomeResource() {
    super.prepareRootMap("Home");
  }

  @GET
  public Response showHome() {
    super.getPrincipal();
    Response response = super.templateResponse("home.rythm");
    return response;
  }

} // HomeResource
