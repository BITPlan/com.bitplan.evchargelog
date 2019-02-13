/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph-tutorial-geo
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.Excel;
import com.bitplan.simplegraph.excel.ExcelSystem;
import com.bitplan.simplegraph.json.JsonSystem;

/**
 * JUnit Tests for the Charging Stations tutorial example
 * 
 * @author wf
 *
 */
public class TestChargingStations {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.evchargelog");

  boolean debug = false;
  ExcelSystem baExcelSystem = null;

  /**
   * get the Excel File for ChargingStations
   * 
   * @return
   * @throws Exception
   */
  public ExcelSystem getBundesnetzAgenturChargingStations() throws Exception {
    // The original file has some superfluous sheets and the title row is not
    // in the first line so we downloaded and adapted it a bit to avoid to do
    // this in software e.g.
    // as outlined in
    // https://stackoverflow.com/questions/1834971/removing-a-row-from-an-excel-sheet-with-apache-poi-hssf
    String url = "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/HandelundVertrieb/Ladesaeulen/Ladesaeulenkarte_Datenbankauszug20.xlsx?__blob=publicationFile&v=2";
    Excel excel = new Excel(url);
    assertNotNull("Download from "+url,excel.workbook);
    excel.workbook.removeSheetAt(2);
    excel.workbook.removeSheetAt(1);
    assertEquals(1, excel.getSheets().size());
    // fix the title row by shifting it up
    XSSFSheet mainSheet = excel.getSheets().get(0);
    XSSFRow titleRow = mainSheet.getRow(5);
    String firstCell = titleRow.getCell(0).getStringCellValue();
    assertEquals("Betreiber", firstCell);
    int cellCount = titleRow.getLastCellNum();
    assertEquals(21, cellCount);
    XSSFRow firstRow = mainSheet.getRow(0);
    for (int col = 0; col < cellCount; col++) {
      // https://stackoverflow.com/a/12754818/1497139
      Cell cell = firstRow.getCell(col);
      if (cell == null) {
        cell = firstRow.createCell(col);
      }
      cell.setCellType(CellType.STRING);
      Cell srcCell = titleRow.getCell(col);
      cell.setCellValue(srcCell.getStringCellValue());
    }
    /**
     * int maxTitleRowShift=1; String firstCell="?"; // keep shifting the title
     * row until it's found or // the maxShift is reached for (int
     * i=0;i<maxTitleRowShift;i++) {
     * firstCell=titleRow.getCell(0).getStringCellValue();
     * System.out.println(String.format("%3d: %3d - %s",i,cellCount,firstCell));
     * if (firstCell.trim().equals("Betreiber")) { break; }
     * Excel.removeRow(mainSheet, 0); }
     */
    File excelFile = File.createTempFile("Ladesaeulenkarte_Datenbankauszug20",
        ".xlsx");
    excel.save(excelFile.getPath());
    if (baExcelSystem == null) {
      baExcelSystem = new ExcelSystem();
      baExcelSystem.connect();
      baExcelSystem.moveTo(excelFile.toURI().toString());
    }
    // remove the temporary file again
    // excelFile.delete();
    return baExcelSystem;
  }

  /**
   * test reading the list of registered german charging stations from
   * Bundesnetzagentur
   * 
   * @throws Exception
   */
  @Test
  public void testBundesnetzagentur() throws Exception {
    ExcelSystem es = this.getBundesnetzAgenturChargingStations();
    long count = es.g().V().count().next().longValue();
    // actually there are only 7733 but we have to work around
    // https://bz.apache.org/bugzilla/show_bug.cgi?id=62711
    assertEquals(7737, count);
    es.g().V().has("row").forEachRemaining(v -> {
      Station station = this.fromBundesNetzagentur(v);
      showStation(station);
    });
  }

  /**
   * show the given station
   * 
   * @param station
   */
  public void showStation(Station station) {
    if (station.getCountry() != null)
      System.out.println(String.format("%2s-%5s %25s %30s %.4f %.4f",
          station.getCountry(), station.getZip(), station.getCity(),
          station.getAddress(), station.getLat(), station.getLon()));
  }

  /**
   * get a Station from a Bundesnetzagentur Vertex derived from it's Excel
   * record
   * 
   * @param sv
   *          - station vertex
   * @return the com.bitplan.evchargelog.Station
   */
  public Station fromBundesNetzagentur(Vertex sv) {
    Station station = new StationImpl();
    sv.property("Betreiber")
        .ifPresent(value -> station.setOperator(value.toString()));
    sv.property("Adresse")
        .ifPresent(value -> station.setAddress(value.toString()));
    sv.property("Postleitzahl Ort").ifPresent(value -> {
      String city = value.toString();
      String[] ziploc = value.toString().split(" ");
      if (ziploc.length >= 2) {
        station.setZip(ziploc[0]);
        station.setCity(city.substring(ziploc[0].length()).trim());
        station.setCountry("DE");
      } else {
        LOGGER.log(Level.WARNING,
            String.format("Postleitzahl Ort (%3d)=%s ", ziploc.length, value));
      }
    });
    sv.property("Breitengrad [DG]")
        .ifPresent(value -> station.setLat(((Number) value).doubleValue()));
    sv.property("Längengrad [DG]")
        .ifPresent(value -> station.setLon(((Number) value).doubleValue()));

    return station;
  }

  class OpenChargeMapResult {
    Double distance;
    Station station;
  }

  /**
   * get a Station from an OpenChargeMap Vertex derived from it's Json record
   * 
   * @param sv
   *          - station vertex
   * @return the com.bitplan.evchargelog.Station
   */
  public OpenChargeMapResult fromOpenChargeMap(Vertex sv) {
    OpenChargeMapResult result = new OpenChargeMapResult();
    Station station = new StationImpl();
    sv.property("Latitude")
        .ifPresent(value -> station.setLat(((Number) value).doubleValue()));
    sv.property("Longitude")
        .ifPresent(value -> station.setLon(((Number) value).doubleValue()));
    sv.property("Postcode")
        .ifPresent(value -> station.setZip(value.toString()));
    sv.property("Town").ifPresent(value -> station.setCity(value.toString()));
    sv.property("AddressLine1")
        .ifPresent(value -> station.setAddress(value.toString()));

    result.station = station;
    sv.property("Distance")
        .ifPresent(value -> result.distance = ((Number) value).doubleValue());
    return result;
  }

  /**
   * get the closest Station to the given latitude and longitude from
   * OpenChargeMap
   * 
   * @param lat
   * @param lon
   * @return the Station
   */
  public OpenChargeMapResult closestOpenChargeMap(Double lat, Double lon) {
    // number of stations nearby to get from open chargemap api
    int maxresults = 3;
    String apiUrl = String.format(Locale.ENGLISH,
        "http://api.openchargemap.io/v3/poi/?output=json&latitude=%.4f8&longitude=%.4f&maxresults=10",
        lat, lon, maxresults);
    JsonSystem js = new JsonSystem();
    try {
      js.connect();
    } catch (Exception e) {
      fail(e.getMessage());
    }
    js.moveTo(apiUrl);

    GraphTraversal<Vertex, Vertex> addressesByDistance = js.g().V()
        .hasLabel("AddressInfo").order().by("Distance");
    assertTrue(addressesByDistance.hasNext());
    Vertex av = addressesByDistance.next();
    OpenChargeMapResult result = this.fromOpenChargeMap(av);
    return result;
  }

  /**
   * convert the given Bundesnetzagentur entry to openChargMap
   * 
   * @param graph
   * @param v
   */
  public Vertex convertToOpenChargeMap(Graph graph, Vertex v) {
    if (debug)
      SimpleNode.printDebug.accept(v);
    Vertex station = graph.addVertex();
    station.property("AddressLine1", v.property("Adresse").value().toString());

    station.property("Latitude",
        Double.parseDouble(v.property("Breitengrad [DG]").value().toString()));
    station.property("Longitude",
        Double.parseDouble(v.property("Längengrad [DG]").value().toString()));
    return station;
  }

  @Test
  public void testConvertToOpenChargeMap() throws Exception {
    ExcelSystem es = this.getBundesnetzAgenturChargingStations();
    es.g().V().has("row").forEachRemaining(v -> {
      if (v.property("Betreiber").isPresent()) {
        Station station = this.fromBundesNetzagentur(v);
        showStation(station);
      }
    });
  }

  @Test
  public void testOpenChargeMapApi() throws Exception {
    OpenChargeMapResult result = closestOpenChargeMap(51.219, 6.634);
    showStation(result.station);
  }

  public void testCheckBundesNetzAgenturAgainsOpenChargeMapApi()
      throws Exception {
    ExcelSystem es = this.getBundesnetzAgenturChargingStations();
    // number of stations to check
    int limit = 20;
    es.g().V().has("row").limit(limit).forEachRemaining(basv -> {
      Station bnaStation = this.fromBundesNetzagentur(basv);
      showStation(bnaStation);
      OpenChargeMapResult result = this
          .closestOpenChargeMap(bnaStation.getLat(), bnaStation.getLon());
      showStation(result.station);
      System.out.println(String.format("\tdistance: %.3f km", result.distance));
    });
  }

  /**
   * get the path the given class was loaded from
   * 
   * @param clazz
   * @return the path
   */
  public String getClassLoaderPath(Class<?> clazz) {
    ClassLoader classloader = clazz.getClassLoader();
    String resource = clazz.getName().replaceAll("\\.", "/") + ".class";
    URL res = classloader.getResource(resource);
    String path = res.getPath();
    return path;
  }

  @Test
  public void testPOI() {
    Class<?>[] classes = {
        org.apache.poi.poifs.filesystem.POIFSFileSystem.class,
        org.apache.poi.ooxml.POIXMLDocument.class,
        org.apache.poi.hslf.usermodel.HSLFSlideShow.class,
        org.apache.poi.xssf.usermodel.XSSFCell.class,
        org.apache.poi.ss.usermodel.CellType.class };
    for (Class<?> clazz : classes) {
      String path = getClassLoaderPath(clazz);
      if (debug)
        System.out.println(
            String.format("%s came from %s", clazz.getSimpleName(), path));
      assertTrue(path.contains("4.0.1"));
    }
  }
}
