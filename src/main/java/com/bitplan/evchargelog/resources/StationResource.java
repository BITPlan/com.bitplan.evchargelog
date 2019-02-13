/**
 * Copyright (C) 2017 BITPlan GmbH
 * 
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 *
 */
package com.bitplan.evchargelog.resources;

import javax.ws.rs.Path;

import com.bitplan.evchargelog.Station;
import com.bitplan.evchargelog.StationManager;


/**
 * Jersey Resource for Station
 */
@Path("/station")
public class StationResource extends EVChargeLogResource<StationManager,Station> {

  /**
   * constructor
   */
  public StationResource() {
    setTemplate("station.rythm");
    setElementName("s");
    prepareRootMap("Ladestation");
  }

} // StationResource
