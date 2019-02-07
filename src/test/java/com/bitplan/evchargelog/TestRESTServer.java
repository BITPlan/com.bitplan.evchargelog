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
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.bitplan.datatypes.DefaultTypeConverter;
import com.bitplan.datatypes.TypeConverter;
import com.sun.jersey.api.client.ClientResponse;

/**
 * test the RESTful server
 * @author wf
 *
 */
public class TestRESTServer extends TestEVChargeLogServer {


  @Test
  public void testTypeConverter() {
    ChargePeriod cp=new ChargePeriodImpl();
    TypeConverter tc=new DefaultTypeConverter();
    assertEquals("-",tc.nullValue(cp.getkWh()));
  }

  @Test
  public void testPost() throws Exception {
    Map<String, String> formData = new HashMap<String, String>();
    formData.put("odo", "1925");
    formData.put("cost", "2,25");
    formData.put("from", "2017-03-18 19:25");
    formData.put("to", "2017-03-18 20:25");
    formData.put("socStart", "33");
    formData.put("socEnd", "80");
    formData.put("url","http://www.chargeit.com");
    String vin="WAUEH74F97N170230";
    String xmlPath = ChargePeriodManagerImpl.getXmlPath(vin);
    File xmlFile=new File(xmlPath);
    ChargePeriodManager empty=new ChargePeriodManagerImpl();
    FileUtils.write(xmlFile,empty.asXML(),"UTF-8");
    ChargePeriodManager cpm=ChargePeriodManagerImpl.getInstance(vin);
    assertNotNull("chargperiods for vin "+vin+" should be loadable",cpm);
    int prevSize = cpm.getPeriods()
        .size();
    ClientResponse response = super.getPostResponse(
        "/charge/chargeperiods/add", formData, debug);
    assertEquals(200, response.getStatus());
    List<ChargePeriod> periods = cpm.getPeriods();
    assertEquals(prevSize + 1, periods.size());
    ChargePeriod period = periods.get(periods.size() - 1);
    System.out.println(period.asString());
    assertEquals(2.25, period.getCost(), 0.001);
    assertEquals(1925, period.getOdo(), 0.1);
    assertEquals(33,period.getSocStart(),0.1);
    assertEquals(80,period.getSocEnd(),0.1);
    assertEquals("http://www.chargeit.com",period.getUrl());
    assertEquals("2017-03-18 19:25:00",period.getFromString());
    assertEquals("2017-03-18 20:25:00",period.getToString());
  }
}
