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
package com.bitplan.evchargelog;

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

/**
 * a time period in which the ev was charged
 * @author wf
 *
 */
public interface ChargePeriod {
  public enum ChargeMode {
    Chademo, CCS, Type2, Tesla, AC
  };
  
  public ChargeMode getChargeMode();
  public void setChargeMode(ChargeMode ac);
  public Date getFrom();
  public void setFrom(Date from);

  public Date getTo();
  public void setTo(Date to);

  public Double getOdo();
  public void setOdo(Double odo);
  
  public Double getAmpere();
  public void setAmpere(Double d);
  
  public Double getCost();
  public void setCost(Double cost);
  
  public Double getSocStart();
  public void setSocStart(Double socStart);
  
  public double getSocEnd();
  public void setSocEnd(double socEnd);
  
  public Double getkWh();
  public void setkWh(Double kWh);
  
  public Double getRR();
  public void setRR(Double rR);
  
  public Double getAh();
  public void setAh(Double Ah);
  
  public String getUrl();
  public void setUrl(String url);
  
  public Double calcKWhours();
  
  /**
   * format the from time as isoDate
   * @return
   */
  public String getFromString();

  /**
   * format the to time as isoDate
   * @return
   */
  public String getToString();
  
  /**
   * set my values from the given formParams
   * @param formParams
   */
  public void fromMap(MultivaluedMap<String, String> formParams);

  /**
   * get the duration as Hours and Minutes
   * 
   * @return
   */
  public String asHoursAndMinutes();
  
  /**
   * return me as a String
   * 
   * @return
   */
  public String asString();
 
}
