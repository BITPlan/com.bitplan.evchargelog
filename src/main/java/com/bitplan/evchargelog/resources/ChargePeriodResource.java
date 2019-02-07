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

import javax.ws.rs.Path;

import com.bitplan.evchargelog.ChargePeriod;
import com.bitplan.evchargelog.ChargePeriodManager;

/**
 * Jersey Resource for ChargePeriod
 */
@Path("/chargeperiod")
public class ChargePeriodResource extends EVChargeLogResource<ChargePeriodManager,ChargePeriod> {

  /**
   * constructor
   */
  public ChargePeriodResource() {
    setTemplate("chargePeriod.rythm");
    setElementName("cp");
    prepareRootMap("Ladevorgang");
  }

} // ChargePeriodResource