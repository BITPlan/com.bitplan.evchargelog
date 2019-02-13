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

import javax.ws.rs.core.MultivaluedMap;

/**
 * Charging Station
 * @author wf
 *
 */
public interface Station {

  String getUrl();

  void setUrl(String url);

  String getName();

  void setName(String name);
  
  public String getOperator();

  public void setOperator(String operator);

  String getLogo();

  void setLogo(String logo);

  String getComment();

  void setComment(String comment);

  String getLocation();

  void setLocation(String location);

  String getZip();

  void setZip(String zip);
  
  public String getCountry();
  public void setCountry(String country);
  
  public String getCity();
  public void setCity(String city);
  
  public String getAddress();
  public void setAddress(String address);

  Double getLat();
  void setLat(Double lat);

  Double getLon();
  void setLon(Double lon);

  /**
   * update my values from the given formParam values
   * @param formParams
   */
  void fromMap(MultivaluedMap<String, String> formParams);

}