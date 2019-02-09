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
package com.bitplan.evchargelog.rest;

import com.bitplan.evchargelog.ChargePeriodImpl;
import com.bitplan.evchargelog.ChargePeriodManagerImpl;
import com.bitplan.rest.RestServerImpl;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.providers.JsonProvider;
import com.bitplan.evchargelog.UserManagerImpl;

/**
 * RESTful Server for EVChargeLog
 */
public class EVChargeLogServer extends RestServerImpl{

  /**
   * construct EVChargeLogServer
   * setting defaults
   * @throws Exception 
   */
  public EVChargeLogServer() throws Exception {
    settings.setHost("0.0.0.0");
    settings.setPort(8199);
    settings.setContextPath("/charge".toLowerCase());
    settings.addClassPathHandler("/", "/static/");
    super.useFastJson=false;
    String packages="com.bitplan.evchargelog.resources;com.bitplan.evchargelog.rest;com.bitplan.vzjava.resources;com.bitplan.rest.providers";
    JsonProvider.registerType(UserManagerImpl.class);
    JsonProvider.registerType(ChargePeriodManagerImpl.class);
    JsonProvider.registerType(ChargePeriodImpl.class);
	
    settings.setPackages(packages);
    
    UserManager um=UserManagerImpl.getInstance();
    settings.setUserManager(um);
  }       
       
  /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String[] args) throws Exception {
     EVChargeLogServer rs=new EVChargeLogServer();
     rs.settings.parseArguments(args);
     rs.startWebServer();
   } // main
} // EVChargeLogServer
