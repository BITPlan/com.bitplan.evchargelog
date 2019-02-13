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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.jaxb.JaxbFactoryApi;
import com.bitplan.jaxb.ManagerImpl;

@XmlRootElement(name = "Stations")
public class StationManagerImpl extends ManagerImpl<StationManager, Station>
    implements StationManager {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");

  private static JaxbFactory<StationManager> factory;

  public static boolean testMode=false;

  private static StationManager instance;
  List<Station> stations = new ArrayList<Station>();

  public static JaxbFactoryApi<StationManager> getFactoryStatic() {
    if (factory == null) {
      factory = new JaxbFactory<StationManager>(
          StationManagerImpl.class);
    }
    return factory;
  }

  @Override
  public List<Station> getElements() {
    return stations;
  }

  @Override
  public JaxbFactoryApi<StationManager> getFactory() {
    return getFactoryStatic();
  }

  @Override
  public void setStations(List<Station> stations) {
    this.stations=stations;
  }

  @XmlElementWrapper(name = "stations")
  @XmlElement(name = "station", type = StationImpl.class)

  @Override
  public List<Station> getStations() {
    return stations;
  }

  @Override
  public void add(Station station) {
    this.stations.add(station);
  }
  
  /**
   * load me from the given xml File
   * 
   * @param xmlFile
   * @throws Exception
   */
  public static StationManager load(File xmlFile) throws Exception {
    String xml = FileUtils.readFileToString(xmlFile, "UTF-8");
    StationManager sm = getFactoryStatic().fromXML(xml);
    sm.setXmlPath(xmlFile.getAbsolutePath());
    sm.setXmlFile(xmlFile);
    return sm;
  }

  public static StationManager getInstance() {
    if (instance==null) {
    File xmlFile=XMLStorage.getXmlFile("Stations", "Stations%s",testMode?"_test":"");
      try {
        instance=load(xmlFile);
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
    } 
    return instance;
  }
}
