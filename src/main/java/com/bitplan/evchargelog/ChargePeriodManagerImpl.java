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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.jaxb.JaxbFactoryApi;
import com.bitplan.jaxb.ManagerImpl;

@XmlRootElement(name = "ChargePeriods")
public class ChargePeriodManagerImpl
    extends ManagerImpl<ChargePeriodManager, ChargePeriod>
    implements ChargePeriodManager {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");

  private static JaxbFactory<ChargePeriodManager> factory;
  List<ChargePeriod> periods = new ArrayList<ChargePeriod>();

  @XmlElementWrapper(name = "periods")
  @XmlElement(name = "chargeperiod", type = ChargePeriodImpl.class)
  public List<ChargePeriod> getPeriods() {
    return periods;
  }

  public void setPeriods(List<ChargePeriod> periods) {
    this.periods = periods;
  }

  public ChargePeriodManagerImpl() {
  }

  public static JaxbFactoryApi<ChargePeriodManager> getFactoryStatic() {
    if (factory == null) {
      factory = new JaxbFactory<ChargePeriodManager>(
          ChargePeriodManagerImpl.class);
    }
    return factory;
  }

  /**
   * load me from the given xml File
   * 
   * @param xmlFile
   * @throws Exception
   */
  public static ChargePeriodManager load(File xmlFile) throws Exception {
    String xml = FileUtils.readFileToString(xmlFile, "UTF-8");
    ChargePeriodManager pm = getFactoryStatic().fromXML(xml);
    pm.setXmlPath(xmlFile.getAbsolutePath());
    pm.setXmlFile(xmlFile);
    return pm;
  }

  /**
   * load me from the given xmlPath
   * 
   * @param xmlPath
   * @return return a ChargePeriodManager
   * @throws Exception
   */
  public static ChargePeriodManager load(String xmlPath) throws Exception {
    ChargePeriodManager result = load(new File(xmlPath));
    return result;
  }

  /**
   * compare ChargePeriods by from
   */
  public static class FromComparator implements Comparator<ChargePeriod> {
    boolean up = true;

    public FromComparator(boolean pup) {
      this.up = pup;
    }

    @Override
    public int compare(ChargePeriod p1, ChargePeriod p2) {
      if (p1.getFrom() == null && p2.getFrom() == null) {
        return 0;
      }
      if (p1.getFrom() == null)
        return -1;
      if (p2.getFrom() == null)
        return 1;
      // reverse sort by from - higher usage first
      if (up)
        return p1.getFrom().compareTo(p2.getFrom());
      else
        return p2.getFrom().compareTo(p1.getFrom());
    }
  }

  /**
   * sort my periods
   */
  public void sort() {
    Collections.sort(periods, new FromComparator(false));
  }

  /**
   * add the given charge Period
   * 
   * @param period
   */
  public void add(ChargePeriod period) {
    this.periods.add(period);
  }

  @Override
  public JaxbFactoryApi<ChargePeriodManager> getFactory() {
    return getFactoryStatic();
  }

  /***
   * handle the given Throwable
   * 
   * @param th
   */
  private static void handle(Throwable th) {
    LOGGER.log(Level.WARNING, "Error: " + th.getMessage());
    StringWriter sw = new StringWriter();
    new Throwable().printStackTrace(new PrintWriter(sw));
    LOGGER.log(Level.WARNING, "Stacktrace: " + sw.toString());
  }

  /**
   * calculate Statistics
   * 
   * @return
   */
  public Map<String, Price> calcStatistics(String homeUrl) {
    Map<String, Price> stats = new HashMap<String, Price>();
    Price total = new Price("Total");
    Price road = new Price("Road");
    Price home = new Price("Home");
    ChargePeriod prev = new ChargePeriodImpl();

    for (int i = periods.size() - 1; i >= 0; i--) {
      ChargePeriod period = periods.get(i);
      total.addPeriod(period, prev, false);
      if (period.getUrl() != null && period.getUrl().contains(homeUrl)) {
        home.addPeriod(period, prev, false);
      } else {
        road.addPeriod(period, prev, false);
      }
      prev = period;
    }
    stats.put("total", total);
    stats.put("road", road);
    stats.put("home", home);
    return stats;
  }

  /**
   * get the xmlFile for the given vin
   * if it does not exist initialize it
   * 
   * @param vin
   * @return the file
   */
  public static File getXmlFile(String vin) {
    String xmlPath = System.getProperty("user.home") + java.io.File.separator
        + ".evchargelog" + File.separator + "ChargePeriods_"+vin + ".xml";
    File xmlFile=new File(xmlPath);
    if (!xmlFile.exists()) {
      xmlFile.getParentFile().mkdirs();
      ChargePeriodManager empty=new ChargePeriodManagerImpl();
      try {
        FileUtils.write(xmlFile,empty.asXML(),"UTF-8");
      } catch (IOException | JAXBException e) {
        LOGGER.log(Level.WARNING, e.getMessage(),e);
      }
    }
    return xmlFile;
  }

  /**
   * get the Instance for the given vehicle identification
   * 
   * @return the instance
   */
  public static ChargePeriodManager getInstance(String vin) {
    ChargePeriodManager instance = null;
    try {
      instance = load(getXmlFile(vin));
    } catch (Exception e) {
      handle(e);
    }
    return instance;
  }

  @Override
  public List<ChargePeriod> getElements() {
    return periods;
  }
}
