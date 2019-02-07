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

/**
 * calculate Price from charge Periods
 * @author wf
 *
 */
public class Price {
  public static final double pricePerKWh=0.30;
  double kWhSum=0;
  double eurSum=0;
  double odo=0;
  private String title;
  public double getkWhSum() {
    return kWhSum;
  }

  public void setkWhSum(double kWhSum) {
    this.kWhSum = kWhSum;
  }

  public double getEurSum() {
    return eurSum;
  }

  public void setEurSum(double eurSum) {
    this.eurSum = eurSum;
  }

  public double getOdo() {
    return odo;
  }

  public void setOdo(double odo) {
    this.odo = odo;
  }

  public Price(String title) {
    this.title=title;
  }
  
  /**
   * add the period - automatically calculating kwH if not available
   * @param period - the period to add
   * @param prev - the previous period
   */
  public void addPeriod(ChargePeriod period, ChargePeriod prev) {
    addPeriod(period,prev,true);
  }
  
  /**
   * add the given period
   * @param period
   * @param prev 
   */
  public void addPeriod(ChargePeriod period, ChargePeriod prev, boolean calc) {
    if (period.getkWh()==null)
      if (calc)
        period.setkWh(period.calcKWhours());
      else
        period.setkWh(0.0);
    if (period.getCost()==null)
      period.setCost(period.getkWh()*pricePerKWh);
    kWhSum+=period.getkWh();
    eurSum+=period.getCost();
    if (prev==null) {
      odo+=period.getOdo();
    } else {
      odo+=period.getOdo()-prev.getOdo();
    }
  }
  /**
   * show the price
   * @param dieselPerL
   */
  public void show(Double dieselPerL) {
    Double eurPer100km=eurSum/odo*100.0;
    Double lPer100kmE=eurPer100km/dieselPerL;
    Double kWhPer100km=kWhSum/odo*100.0;
    Double eurPerKWh=eurSum/kWhSum;
    System.out.println(String.format("%10s %5.0f km %5.1f kWh %5.1f kWh/100km %6.2f EUR %6.2f EUR/kWh %6.2f EUR/100 km ≈%3.1f l/100 km Diesel",title,odo,kWhSum,kWhPer100km,eurSum,eurPerKWh,eurPer100km,lPer100kmE));
  }
}