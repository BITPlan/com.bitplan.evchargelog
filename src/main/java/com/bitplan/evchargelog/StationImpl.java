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
import javax.xml.bind.annotation.XmlRootElement;

import com.bitplan.datatypes.DefaultTypeConverter;
import com.bitplan.datatypes.TypeConverter;

/**
 * a charging Station
 * @author wf
 *
 */
@XmlRootElement
public class StationImpl implements Station {
  String name;
  String operator;
  String logo;
  String url;
  String comment;
  String location;
  String country;
  String zip;
  String city;
  String address;
  Double lat;
  Double lon;
  
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getUrl()
   */
  @Override
  public String getUrl() {
    return url;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setUrl(java.lang.String)
   */
  @Override
  public void setUrl(String url) {
    this.url = url;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getName()
   */
  @Override
  public String getName() {
    return name;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }
  public String getOperator() {
    return operator;
  }
  public void setOperator(String operator) {
    this.operator = operator;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getLogo()
   */
  @Override
  public String getLogo() {
    return logo;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setLogo(java.lang.String)
   */
  @Override
  public void setLogo(String logo) {
    this.logo = logo;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getComment()
   */
  @Override
  public String getComment() {
    return comment;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setComment(java.lang.String)
   */
  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getLocation()
   */
  @Override
  public String getLocation() {
    return location;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setLocation(java.lang.String)
   */
  @Override
  public void setLocation(String location) {
    this.location = location;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#getZip()
   */
  @Override
  public String getZip() {
    return zip;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#setZip(java.lang.String)
   */
  @Override
  public void setZip(String zip) {
    this.zip = zip;
  }
  
 
  public Double getLat() {
    return lat;
  }
  public void setLat(Double lat) {
    this.lat = lat;
  }
  public Double getLon() {
    return lon;
  }
  public void setLon(Double lon) {
    this.lon = lon;
  }
  /* (non-Javadoc)
   * @see com.bitplan.evchargelog.Station#fromMap(javax.ws.rs.core.MultivaluedMap)
   */
  @Override
  public void fromMap(MultivaluedMap<String, String> formParams) {
    TypeConverter tc=new DefaultTypeConverter();
    if (formParams.containsKey("lat")) this.setLat(tc.getDouble(formParams.getFirst("lat")));
    if (formParams.containsKey("lon")) this.setLon(tc.getDouble(formParams.getFirst("lon")));
    if (formParams.containsKey("url")) this.setUrl(tc.getString(formParams.getFirst("url")));
    if (formParams.containsKey("name")) this.setName(tc.getString(formParams.getFirst("name")));
    if (formParams.containsKey("operator")) this.setOperator(tc.getString(formParams.getFirst("operator")));
    if (formParams.containsKey("logo")) this.setLogo(tc.getString(formParams.getFirst("logo")));
    if (formParams.containsKey("location")) this.setLocation(tc.getString(formParams.getFirst("location")));
    if (formParams.containsKey("zip")) this.setZip(tc.getString(formParams.getFirst("zip")));
    if (formParams.containsKey("country")) this.setCountry(tc.getString(formParams.getFirst("country")));
    if (formParams.containsKey("city")) this.setCity(tc.getString(formParams.getFirst("city")));
    if (formParams.containsKey("address")) this.setAddress(tc.getString(formParams.getFirst("address")));
    if (formParams.containsKey("comment")) this.setComment(tc.getString(formParams.getFirst("comment")));
  }
}
