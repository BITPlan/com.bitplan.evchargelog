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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.bitplan.datatypes.DefaultTypeConverter;
import com.bitplan.datatypes.TypeConverter;
import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.jaxb.JaxbFactoryApi;
import com.bitplan.jaxb.JaxbPersistenceApi;

/**
 * ChargePeriod which can be stored as XML
 * 
 * @author wf
 *
 */
@XmlRootElement(name = "ChargePeriod")
@XmlType(propOrder = { "from","to","odo", "chargeMode", "ampere", "RR", "socStart",
    "socEnd", "kWh", "cost", "url" })
public class ChargePeriodImpl
    implements ChargePeriod, JaxbPersistenceApi<ChargePeriod> {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");
  static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  static SimpleDateFormat shortIsoDateFormatter = new SimpleDateFormat("dd HH:mm:ss");
  
  static boolean debug=false;
  
  Date from;
  Date to;
  Double odo = 0.0;
  Double cost = 0.0;
  Double socStart = 0.0;
  Double socEnd = 100.0;
  Double kWh = 0.0;
  Double RR = 0.0;
  Double ampere = 0.0;
  String url = "";

  private ChargeMode chargeMode = ChargeMode.AC;

  static JaxbFactory<ChargePeriod> factory = null;

  // e.g. make JAXB happy
  public ChargePeriodImpl() {
    init();
  }
  
  /**
   * create a chargePeriod for to isoDate String
   * 
   * @param isoFrom
   * @param isoTo
   * @throws ParseException
   */
  public ChargePeriodImpl(String isoFrom, String isoTo) throws ParseException {
    this.from = isoDateFormatter.parse(isoFrom);
    this.to = isoDateFormatter.parse(isoTo);
    init();
  }
  
  public void init() {
    
  }

  public Date getFrom() {
    return from;
  }

  public void setFrom(Date from) {
    this.from = from;
  }

  public Date getTo() {
    return to;
  }

  public void setTo(Date to) {
    this.to = to;
  }

  public ChargeMode getChargeMode() {
    return chargeMode;
  }

  public Double getOdo() {
    return odo == null ? 0.0 : odo;
  }

  public void setOdo(Double odo) {
    this.odo = odo;
  }

  public void setChargeMode(ChargeMode chargeMode) {
    this.chargeMode = chargeMode;
  }

  public Double getCost() {
    return cost == null ? 0.0 : cost;
  }

  public void setCost(Double cost) {
    this.cost = cost;
  }

  public Double getSocStart() {
    return socStart == null ? 0.0 : socStart;
  }

  public void setSocStart(Double socStart) {
    this.socStart = socStart;
  }

  public double getSocEnd() {
    return socEnd;
  }

  public void setSocEnd(double socEnd) {
    this.socEnd = socEnd;
  }

  public Double getkWh() {
    return kWh == null ? 0.0 : kWh;
  }

  public void setkWh(Double kWh) {
    this.kWh = kWh;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Double getAmpere() {
    return ampere == null ? 0.0 : ampere;
  }

  public Double getRR() {
    return RR == null ? 0.0 : RR;
  }

  public void setRR(Double rR) {
    RR = rR;
  }

  public void setAmpere(Double ampere) {
    this.ampere = ampere;
  }

  @Override
  public JaxbFactoryApi<ChargePeriod> getFactory() {
    if (factory == null) {
      factory = new JaxbFactory<ChargePeriod>(ChargePeriod.class);
    }
    return factory;
  }

  @Override
  public String asJson() throws JAXBException {
    return this.getFactory().asJson(this);
  }

  @Override
  public String asXML() throws JAXBException {
    return getFactory().asXML(this);
  }

  @Override
  public Double calcKWhours() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * get the number of Seconds between to dates
   * @param from
   * @param to
   * @return the seconds
   */
  public static long diffSeconds(Date from, Date to) {
    long diff = to.getTime() - from.getTime();
    long diffSeconds = diff / 1000;
    return diffSeconds;
  }
  
  /**
   * get the number of minutes Difference between two dates
   * 
   * @param from
   * @param to
   * @return the minutes
   */
  public static long diffMinutes(Date from, Date to) {
    long diffMinutes = diffSeconds(from,to)/60;
    return diffMinutes;
  }
  
  /**
   * get the duration as Hours and Minutes
   * 
   * @return
   */
  public String asHoursAndMinutes() {
    long diffMinutes = diffMinutes(from, to);
    long diffHours = diffMinutes / 60;
    diffMinutes = diffMinutes % 60;
    String hoursAndMinutes = String.format("%2d h %02d'", diffHours, diffMinutes);
    return hoursAndMinutes;
  }
  /**
   * return me as a Date Range
   * 
   * @return
   */
  public String asDateRange() {
    String result = asDateRange(from, to);
    return result;
  }

  /**
   * return the given from - to period as an ISO Date range
   * 
   * @param from
   * @param to
   * @return the string representation
   */
  public static String asDateRange(Date from, Date to) {
    String result = String.format("%s - %s", isoDateFormatter.format(from), shortIsoDateFormatter.format(to));
    return result;
  }
  
  /**
   * format the from time as isoDate
   * @return
   */
  public String getFromString() {
    String result = isoDateFormatter.format(from);
    return result;
  }

  /**
   * format the to time as isoDate
   * @return
   */
  public String getToString() {
    String result = isoDateFormatter.format(to);
    return result;
  }

  /**
   * return me as a String
   * 
   * @return
   */
  public String asString() {
    String result = String.format("%s %s @%2.0f A %4.1f kWh", asDateRange(),
        asHoursAndMinutes(), ampere, kWh);
    return result;
  }
  
  /**
   * set my values from the given formParams
   * @param formParams
   */
  public void fromMap(MultivaluedMap<String, String> formParams) {
    if (debug) {
      for (String key:formParams.keySet()) {
        LOGGER.log(Level.INFO,key+"="+formParams.getFirst(key).toString());
      }
    }
    TypeConverter tc=new DefaultTypeConverter();
    if (formParams.containsKey("odo")) this.setOdo(tc.getDouble(formParams.getFirst("odo")));
    if (formParams.containsKey("ampere")) this.setAmpere(tc.getDouble(formParams.getFirst("ampere")));
    if (formParams.containsKey("from")) this.setFrom(tc.getDate(formParams.getFirst("from")));
    if (formParams.containsKey("to")) this.setTo(tc.getDate(formParams.getFirst("to")));
    if (formParams.containsKey("kWh")) this.setkWh(tc.getDouble(formParams.getFirst("kWh")));
    if (formParams.containsKey("cost")) this.setCost(tc.getDouble(formParams.getFirst("cost")));
    if (formParams.containsKey("RR")) this.setRR(tc.getDouble(formParams.getFirst("RR")));
    if (formParams.containsKey("socStart")) this.setSocStart(tc.getDouble(formParams.getFirst("socStart")));
    if (formParams.containsKey("socEnd")) this.setSocEnd(tc.getDouble(formParams.getFirst("socEnd")));
    if (formParams.containsKey("url")) this.setUrl(tc.getString(formParams.getFirst("url")));
    if (formParams.containsKey("chargeMode")) this.setChargeMode(ChargeMode.valueOf(tc.getString(formParams.getFirst("chargeMode"))));
  }

}
