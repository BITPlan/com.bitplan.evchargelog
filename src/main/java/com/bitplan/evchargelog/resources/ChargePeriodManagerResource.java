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

import java.security.Principal;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.bitplan.evchargelog.ChargePeriod;
import com.bitplan.evchargelog.ChargePeriodImpl;
import com.bitplan.evchargelog.ChargePeriodManager;
import com.bitplan.evchargelog.ChargePeriodManagerImpl;

/**
 * Jersey Resource for ChargePeriodManager
 */
@Path("/chargeperiods")
public class ChargePeriodManagerResource
    extends EVChargeLogManagerResource<ChargePeriodManager, ChargePeriod> {
  /**
   * create this resource
   * 
   * @throws Exception
   */
  public ChargePeriodManagerResource() throws Exception {
    setTemplate("chargePeriod.rythm");
    setElementName("cp");
    prepareRootMap("Ladevorgänge");
  }

  /**
   * init the manager for the given VIN
   * 
   * @param vin
   * @return
   */
  protected ChargePeriodManager initManager(String vin) {
    ChargePeriodManager cpm = ChargePeriodManagerImpl.getInstance(vin);
    setManager(cpm);
    return cpm;
  }

  @Override
  public ChargePeriodManager getManager() { 
   Principal principal = getPrincipal();
   if (principal!=null) {
      String vin = principal.getName();
      initManager(vin);
    }
    return super.getManager();
  }

  @GET
  @Path("add")
  public Response addPeriod() {
    ChargePeriod newPeriod = new ChargePeriodImpl();
    newPeriod.setFrom(new Date());
    newPeriod.setTo(new Date());
    return this.getPeriod(newPeriod);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces({ "text/html" })
  @Path("add")
  public Response addPeriodFromPost(MultivaluedMap<String, String> formParams)
      throws Exception {
    ChargePeriodManager lcpm = getManager();
    ChargePeriod newPeriod = new ChargePeriodImpl();
    newPeriod.fromMap(formParams);
    lcpm.getPeriods().add(newPeriod);
    lcpm.save();
    int index = lcpm.getElements().indexOf(newPeriod) + 1;
    //return redirect("hello/echo/"+index);
    return redirect("chargeperiods/at/" + index);
  }

  @GET
  @Produces({ MediaType.TEXT_HTML })
  @Path("delete/{index}")
  public Response delete(@PathParam("index") Integer index) throws Exception {
    ChargePeriodManager lcpm = getManager();
    lcpm.getElements().remove(index - 1);
    lcpm.save();
    rootMap.put("cpm", lcpm);
    Response response = super.templateResponse("chargePeriods.rythm");
    return response;
  }
  
  @POST
  @Produces({ MediaType.TEXT_HTML })
  @Path("at/{index}")
  public Response postChanges(@PathParam("index") Integer index,
      MultivaluedMap<String, String> formParams) throws Exception {
    ChargePeriodManager lcpm = getManager();
    ChargePeriod period = lcpm.getElements().get(index - 1);
    period.fromMap(formParams);
    lcpm.save();
    return getPeriod(period);
  }

  /**
   * return the given period's response
   * @param period
   * @return the period
   */
  public Response getPeriod(ChargePeriod period) {  
    rootMap.put("cp", period);
    Response response = super.templateResponse("chargePeriod.rythm");
    return response;
  }


  @GET
  @Path("range/{isoFrom}/{isoTo}")
  public Response getChargePeriodsForRange(@PathParam("vin") String vin,
      @PathParam("isoFrom") String isoFrom, @PathParam("isoTo") String isoTo)
      throws Exception {
    rootMap.put("isoFrom", isoFrom);
    rootMap.put("isoTo", isoTo);
    ChargePeriodManager lcpm = initManager(vin);
    rootMap.put("cpm", lcpm);
    Response response = super.templateResponse("chargePeriods.rythm");
    return response;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getChargePeriodsAsHtml(@PathParam("vin") String vin)
      throws Exception {
    ChargePeriodManager lcpm = initManager(vin);
    rootMap.put("cpm", lcpm);
    Response response = super.templateResponse("chargePeriods.rythm");
    return response;
  }

  /*
   * @GET
   * 
   * @Produces({ "text/xml", "text/plain", "text/html" })
   * 
   * @Path("by/{attributeName}/{attributeValue}") public Response
   * getChargePeriodBy(@Context UriInfo uri, @PathParam("attributeName") String
   * attributeName, @PathParam("attributeValue") String attributeValue) throws
   * Exception{ ContentType contentType = getContentType(); ChargePeriodManager
   * lChargePeriodManager = (ChargePeriodManager) getManager();
   * List<ChargePeriod> ChargePeriodList =
   * lChargePeriodManager.findBy(attributeName,attributeValue,getConfiguration
   * ().getRecordLimit()); String
   * result=getResultForList(contentType,ChargePeriodList); return result; }
   */

} // ChargePeriodManagerResource
