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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * helper class for XML Storage
 * @author wf
 *
 */
public class XMLStorage {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");
  
  /**
   * get the xmlFile for the given format and params
   * if it does not exist initialize it with the given xmlRootElement
   * @param xmlRootElement
   * @param format
   * @param params
   * @return the file
   */
  public static File getXmlFile(String xmlRootElement,String format, Object ...params) {
    String xmlPath = System.getProperty("user.home") + java.io.File.separator
        + ".evchargelog" + File.separator + String.format(format, params) + ".xml";
    File xmlFile=new File(xmlPath);
    if (!xmlFile.exists()) {
      xmlFile.getParentFile().mkdirs();
      try {
        String xml=String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<%s>\n" + 
            "</%s>\n",xmlRootElement,xmlRootElement);
        FileUtils.write(xmlFile,xml,"UTF-8");
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, e.getMessage(),e);
      }
    }
    return xmlFile;
  }

}
