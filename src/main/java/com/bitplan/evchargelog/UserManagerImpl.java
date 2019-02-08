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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.jaxb.JaxbFactoryApi;

/**
 * special UserManager for EV ChargeLog
 * 
 * @author wf
 *
 */
@XmlRootElement(name="EVChargeLogUsers")
public class UserManagerImpl extends com.bitplan.rest.users.UserManagerImpl {
  private static JaxbFactory<com.bitplan.rest.users.UserManagerImpl> factory;
  private static UserManagerImpl instance = null;
  public static boolean testMode=false;
  
  /**
   * parameterless constructor
   */
  public UserManagerImpl() {
    super();
  }

  /**
   * get the factory
   * 
   * @return the factory
   */
  public static JaxbFactoryApi<com.bitplan.rest.users.UserManagerImpl> getFactoryStatic() {
    if (factory == null) {
      factory = new JaxbFactory<com.bitplan.rest.users.UserManagerImpl>(
          UserManagerImpl.class);
    }
    return factory;
  }

  @Override
  public JaxbFactoryApi<com.bitplan.rest.users.UserManagerImpl> getFactory() {
    return getFactoryStatic();
  }
  
  /**
   * set a clean started TestMode
   */
  public static void cleanTestMode() {
    testMode=true;
    getXmlStorageFile().delete();
    instance=null;
  }
  
  /**
   * get my xml File
   */
  public static File getXmlStorageFile() {
    File xmlFile=XMLStorage.getXmlFile("EVChargeLogUsers","Users%s",testMode?"_test":"");
    return xmlFile;
  }

  /**
   * get the instance
   * 
   * @return
   */
  public static UserManagerImpl getInstance() {
    if (instance == null) {
      File xmlFile = getXmlStorageFile();
      String xml;
      try {
        xml = FileUtils.readFileToString(xmlFile, "UTF-8");
        instance = (UserManagerImpl) getFactoryStatic().fromXML(xml);
        instance.reinit();
        instance.setXmlFile(xmlFile);
        instance.setXmlPath(xmlFile.getAbsolutePath());
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
    }
    return instance;
  }

}
