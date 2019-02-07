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

import java.util.List;
import java.util.Map;

import com.bitplan.persistence.Manager;

/**
 * Charge Period Manager interface
 * @author wf
 *
 */
public interface ChargePeriodManager extends Manager<ChargePeriodManager, ChargePeriod>{
  public void setPeriods(List<ChargePeriod> periods);
  public List<ChargePeriod> getPeriods();
  public void add(ChargePeriod period);
  /**
   * calculate Statistics
   * 
   * @return
   */
  public Map<String, Price> calcStatistics(String homeUrl);
}
