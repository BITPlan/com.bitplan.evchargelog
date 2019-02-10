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

import org.junit.After;
import org.junit.Before;

import com.bitplan.evchargelog.rest.EVChargeLogServer;
import com.bitplan.rest.RestServer;
import com.bitplan.rest.User;
import com.bitplan.rest.test.TestRestServer;
import com.bitplan.rest.users.UserImpl;

/**
 * test the EVChargeLog RESTFul server
 * @author wf
 *
 */
public class TestEVChargeLogServer extends TestRestServer {
  private static UserManagerImpl um;
  
  @Before
  public void initServer() throws Exception {
    startServer();
  }
  
  @After
  public void tearDownServer() throws Exception {
    stopServer();
  }

  @Override
  public RestServer createServer() throws Exception {
    UserManagerImpl.testMode=true;
    if (um == null) {
      UserManagerImpl.cleanTestMode();
      um = UserManagerImpl.getInstance();
      User user = new UserImpl(um, "scott", "Scott", "Bruce", "scott@tiger.com",
          "tiger", "CEO", "since 2016-01");
      um.add(user);
      User vehicle = new UserImpl(um, "WAUEH74F97N170230", "Vehicle",
          "sample", "sample@vehicle.com", "leopard", "Vehicle",
          "since 2019-02");
      um.add(vehicle);
      um.save();
    }
    RestServer result = new EVChargeLogServer();
    result.getSettings().setUserManager(um);
    return result;
  }
  
}
