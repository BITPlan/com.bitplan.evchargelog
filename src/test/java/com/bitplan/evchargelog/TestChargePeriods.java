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
import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import com.bitplan.evchargelog.ChargePeriod.ChargeMode;


/**
 * Test charge period handling
 * @author wf
 *
 */
public class TestChargePeriods {
  boolean debug=true;
  
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");

  @Test
  public void testXML() throws Exception {
    ChargePeriodManager pm = new ChargePeriodManagerImpl();
    ChargePeriod period = new ChargePeriodImpl("2017-02-04 16:00:00",
        "2017-02-05 06:00:00");
    period.setOdo(13470.0);
    period.setAmpere(8.0); 
    period.setChargeMode(ChargeMode.AC);
    period.setRR(5.0);
    period.setkWh(15.2);
    period.setAh(44.8);
    period.setCost(period.getkWh() * 0.30);
    period.setUrl("https://www.openstreetmap.org/node/1271476903");
    pm.add(period);
    File xmlFile = File.createTempFile("chargePeriods", ".xml");
    pm.saveAsXML(xmlFile);
    String xml=FileUtils.readFileToString(xmlFile,"UTF-8");
    if (debug) {
      LOGGER.log(Level.INFO,xml);
    }
    ChargePeriodManager lpm = ChargePeriodManagerImpl.load(xmlFile);
    assertEquals(1, lpm.getPeriods().size());
    ChargePeriod period2=lpm.getPeriods().get(0);
    assertEquals(period2.getAh(),period.getAh(),0.01);
    xmlFile.delete();
  }
  
  @Test
  public void testCalcKwh() throws ParseException {
    ChargePeriod period = new ChargePeriodImpl("2019-02-11 05:00:00",
        "2019-02-11 06:00:00");
    period.setAmpere(10.0);
    assertEquals(10.0,period.calcKWhours(),0.01);
  }


}
