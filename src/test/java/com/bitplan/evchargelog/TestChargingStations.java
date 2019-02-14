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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
   * get the Excel File from BundesNetzAgentur
   * 
   * @return the file
   * @throws Exception
   */
  protected File getLadesaeulenkarteDatenbankauszug() throws Exception {
    // The original file has some superfluous sheets and the title row is not
    // in the first line so we downloaded and adapted it a bit to avoid to do
    // this in software e.g.
    // as outlined in
    // https://stackoverflow.com/questions/1834971/removing-a-row-from-an-excel-sheet-with-apache-poi-hssf
    String url = "https://www.bundesnetzagentur.de/SharedDocs/Downloads/DE/Sachgebiete/Energie/Unternehmen_Institutionen/HandelundVertrieb/Ladesaeulen/Ladesaeulenkarte_Datenbankauszug20.xlsx?__blob=publicationFile&v=2";
    Excel excel = new Excel(url);
    assertNotNull("Download from " + url, excel.workbook);
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
    return excelFile;
  }

  /**
   * get the Excel File for ChargingStations
   * 
   * @return
   * @throws Exception
   */
  public ExcelSystem getBundesnetzAgenturChargingStations() throws Exception {
    // File excelFile=this.getLadesaeulenkarteDatenbankauszug();
    // work around quite a few issues with Apache POI and the original file by
    // manually fixing and using the cached version
    File excelFile = new File(
        "src/test/data/Ladesaeulenkarte_Datenbankauszug20.xlsx");
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
   * show the given station
   * 
   * @param station
   */
  public void showStation(Station station) {
    if (station.getCountry() != null)
      System.out
          .println(String.format("%2s-%5s %-15s %-15s %-20s %.4f %.4f%s%s",
              station.getCountry(), station.getZip(), station.getCity(),
              station.getName() == null
                  ? (station.getOperator() == null ? "" : station.getOperator())
                  : station.getName(),
              station.getAddress(), station.getLat(), station.getLon(),
              station.getUrl() == null ? "" : "\n\t" + station.getUrl(),
              station.getComment() == null ? ""
                  : "\n\t" + station.getComment()));
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
        station.setZip(ziploc[0].trim());
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
    SortedMap<Double, Station> stations;

    public double closestDistance() {
      return stations.firstKey();
    }

    public Station closestStation() {
      return stations.get(stations.firstKey());
    }
  }

  /**
   * get a Station from an OpenChargeMap Vertex derived from it's Json record
   * 
   * @param sv
   *          - station vertex
   * @return the com.bitplan.evchargelog.Station
   */
  public Station fromOpenChargeMap(Vertex sv) {
    if (debug)
      SimpleNode.printDebug.accept(sv);
    Station station = new StationImpl();
    sv.property("Title").ifPresent(value -> station.setName(value.toString()));
    sv.property("Latitude")
        .ifPresent(value -> station.setLat(((Number) value).doubleValue()));
    sv.property("Longitude")
        .ifPresent(value -> station.setLon(((Number) value).doubleValue()));
    sv.property("Postcode")
        .ifPresent(value -> station.setZip(value.toString()));
    sv.property("Town")
        .ifPresent(value -> station.setCity(value.toString().trim()));
    sv.property("AddressLine1")
        .ifPresent(value -> station.setAddress(value.toString()));
    sv.property("RelatedURL")
        .ifPresent(value -> station.setUrl(value.toString()));
    sv.property("AccessComments")
        .ifPresent(value -> station.setComment(value.toString()));
    sv.property("CountryID").ifPresent(value -> {
      String country = "?";
      switch (((Number) value).intValue()) {
      case 1:
        country = "GB";
        break;
      case 2:
        country = "US";
        break;
      case 3:
        country = "IE";
        break;
      case 4:
        country = "HK";
        break;
      case 7:
        country = "AL";
        break;
      case 44:
        country = "CA";
        break;
      case 70:
        country = "EG";
      case 80:
        country = "FR";
        break;
      case 87:
        country = "DE";
        break;
      case 112:
        country = "IT";
        break;
      case 159:
        country = "NL";
        break;
      case 168:
        country = "NO";
      }
      station.setCountry(country);
    });
    return station;
  }

  /**
   * get the closest Station to the given latitude and longitude from
   * OpenChargeMap
   * 
   * @param lat
   * @param lon
   * @param maxresults
   * @return the Station
   */
  public OpenChargeMapResult closestOpenChargeMap(Double lat, Double lon,
      int maxresults) {
    String apiUrl = String.format(Locale.ENGLISH,
        "http://api.openchargemap.io/v3/poi/?output=json&latitude=%.4f8&longitude=%.4f&maxresults=%d",
        lat, lon, maxresults);
    JsonSystem js = new JsonSystem();
    try {
      js.connect();
    } catch (Exception e) {
      fail(e.getMessage());
    }
    js.moveTo(apiUrl);

    OpenChargeMapResult result = new OpenChargeMapResult();
    result.stations = new TreeMap<Double, Station>();
    js.g().V().hasLabel("AddressInfo").order().by("Distance")
        .forEachRemaining(sv -> {
          sv.property("Distance").ifPresent(value -> {
            double distance = ((Number) value).doubleValue();
            result.stations.put(distance, this.fromOpenChargeMap(sv));
          });
        });
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
    // actually there are only 7733 but we might have to work around
    // https://bz.apache.org/bugzilla/show_bug.cgi?id=62711
    assertEquals(7733, count);
    es.g().V().has("row").forEachRemaining(v -> {
      Station station = this.fromBundesNetzagentur(v);
      showStation(station);
    });
  }

  class TestLocation {
    public TestLocation(double lat, double lon, String country, String name) {
      super();
      this.lat = lat;
      this.lon = lon;
      this.country = country;
      this.name = name;
    }

    double lat;
    double lon;
    String country;
    String name;
  }

  @Test
  public void testOpenChargeMapApi() throws Exception {
    TestLocation locs[] = {
        new TestLocation(37.3456955, -121.9349518, "US", "2605 The Alameda"),
        new TestLocation(53.9552988220623, -1.0905436441803, "GB",
            "Nunnery Lane Car Park"),
        new TestLocation(52.3786, 4.896995, "NL", "Stationsplein"),
        new TestLocation(45.77466, 4.77867, "FR", "Carrefour Ecully"),
        new TestLocation(43.3236932607158, 11.3456253706452, "IT",
            "Estra Toselli Siena"),
        new TestLocation(58.97014, 5.73365, "NO",
            "P-Domkirkehallen, Stavanger"),
        new TestLocation(51.21867, 6.63348, "DE", "Supermarkt Aldi") };
    for (TestLocation loc : locs) {
      OpenChargeMapResult result = closestOpenChargeMap(loc.lat, loc.lon, 1);
      Station station = result.closestStation();
      showStation(station);
      System.out.println(String.format("%.3f km", result.closestDistance()));
      assertTrue(result.closestDistance() < 0.01);
      assertEquals(loc.name, station.getName());
      assertEquals(loc.lat, station.getLat(), 0.001);
      assertEquals(loc.lon, station.getLon(), 0.001);
      assertEquals(loc.country, station.getCountry());
    }
  }

  @Test
  public void testClosestStations() {
    OpenChargeMapResult c = this.closestOpenChargeMap(48.0304, 10.7263, 4);
    assertEquals(4, c.stations.size());
    for (Entry<Double, Station> se : c.stations.entrySet()) {
      showStation(se.getValue());
      System.out.println(String.format("distance: %.3f km", se.getKey()));
    }
  }

  @Test
  public void testCheckBundesNetzAgenturAgainsOpenChargeMapApi()
      throws Exception {
    ExcelSystem es = this.getBundesnetzAgenturChargingStations();
    // number of stations to check
    int limit = 8000;
    boolean immediateSave=false;
    StationManagerImpl.testMode = true;
    StationManager sm = StationManagerImpl.getInstance();
    es.g().V().has("row").limit(limit).forEachRemaining(basv -> {
      Station bnaStation = this.fromBundesNetzagentur(basv);
      showStation(bnaStation);
      OpenChargeMapResult result = this
          .closestOpenChargeMap(bnaStation.getLat(), bnaStation.getLon(), 3);
      for (Entry<Double, Station> se : result.stations.entrySet()) {
        showStation(se.getValue());
        double match = this.getMatch(bnaStation, se.getValue(), se.getKey())
            * 100.0;
        System.out.println(String.format("\tmatch: %.2f%% distance: %.3f km",
            match, se.getKey()));
        if (match > 90.0) {
          sm.add(se.getValue());
          if (immediateSave)
          try {
            sm.save();
          } catch (Exception e) {
            ErrorHandler.handle(e);
          }
        }
      }
    });
    sm.save();
  }

  /**
   * rate the matching of two stations
   * 
   * @param s
   *          - the original station
   * @param m
   *          - the match candidate
   * @return a matching number which should be close to the percentual
   *         probability of a match
   */
  public double getMatch(Station s, Station m, double distance) {
    // we start assuming a 100% match and then start "degrading"
    double match = 1.0;
    if (distance > 2) {
      match = match * 0.05; // 5% chance for a match
    } else if (distance > 1) {
      match = match * 0.1; // 10% chance for a match
    } else if (distance > 0.5) {
      match = match * 0.2; // 20% chance for a match
    } else if (distance > 0.25) {
      match = match * 0.4; // 40% chance for a match
    } else if (distance > 0.125) {
      match = match * 0.8; // 80% chance for a match (e.g. hotel, company area)
    }
    if (!s.getCountry().equals(m.getCountry())) {
      match = match * 0.1; // 10% chance for a match
    }
    if (!s.getZip().equals(m.getZip())) {
      match = match * 0.5; // only 50% chance for match
    }
    if (!s.getAddress().equals(m.getAddress())) {
      match = match * 0.95; // no big deal - still 95% chance
    }
    return match;
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
