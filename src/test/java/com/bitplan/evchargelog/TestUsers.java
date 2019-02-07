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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManagerImpl;

/**
 * test the user handling from the base rest implementation
 * @author wf
 *
 */
public class TestUsers {

  @Test
  public void testUser() throws Exception {
    UserManager um=UserManagerImpl.getInstance();
    User u=new UserImpl();
    u.setName("Doe");
    u.setFirstname("John");
    um.add(u);
    String xml=um.asXML();
    // System.out.println(xml);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<UserManager>\n" + 
        "   <users>\n" + 
        "      <User>\n" + 
        "         <firstname>John</firstname>\n" + 
        "         <name>Doe</name>\n" + 
        "      </User>\n" + 
        "   </users>\n" + 
        "</UserManager>\n",xml);
  }

}
