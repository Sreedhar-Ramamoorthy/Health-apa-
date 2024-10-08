package ke.co.apollo.health.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
public class ExcelUtil {

  private static String mark = "\"";

  public static void main(String[] args) {
    hospitalExcelToSQL();
  }

  @SuppressWarnings("squid:S3776")
  public static void hospitalExcelToSQL() {
    try (
        Workbook workbook = WorkbookFactory
            .create(new FileInputStream("hospital.xlsx"));
        BufferedWriter out = new BufferedWriter(
            new FileWriter("hospital-data.sql"));
    ) {
      Map<String, Integer> locationMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      Map<String, Integer> coPayMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      List<String> hospitalList = new ArrayList<>();
      List<String> serviceList = new ArrayList<>();
      List<String> coPayList = new ArrayList<>();
      List<String> locationList = new ArrayList<>();
      List<String> hospitalServiceRelList = new ArrayList<>();

      // Service
      Sheet sheet = workbook.getSheetAt(1);
      int rowId = 1;
      do {
        Row row = sheet.getRow(rowId++);
        if (row == null) {
          break;
        }
        String name = getCellValue(row.getCell(0));
        String code = getCellValue(row.getCell(1));
        StringBuilder sb = new StringBuilder("INSERT INTO tbl_services (id, name) VALUES(");
        sb.append(code).append(",").append(mark).append(name).append(mark).append(");")
            .append("\n");
        serviceList.add(sb.toString());
      } while (true);

      int hospitalSeq = 0;
      int locationSeq = 0;
      int coPaySeq = 0;
      sheet = workbook.getSheetAt(0);
      rowId = 1;
      do {
        Row row = sheet.getRow(rowId++);
        if (row == null) {
          break;
        }

        String name = getCellValue(row.getCell(0));
        String address = getCellValue(row.getCell(1));
        String phone = getCellValue(row.getCell(2));
        String email = getCellValue(row.getCell(3));

        String location = getCellValue(row.getCell(4));
        String coPay = getCellValue(row.getCell(5));

        // location
        Integer locationId = locationMap.get(location);
        if (locationId == null) {
          locationId = ++locationSeq;
          StringBuilder sb = new StringBuilder(
              "INSERT INTO tbl_locations (id, name) VALUES(");
          sb.append(locationId).append(",").append(mark).append(location).append(mark).append(");")
              .append("\n");
          locationList.add(sb.toString());
          locationMap.put(location, locationId);
        }

        // co-pay
        Integer coPayId = coPayMap.get(coPay);
        if (coPayId == null) {
          coPayId = ++coPaySeq;
          StringBuilder sb = new StringBuilder(
              "INSERT INTO tbl_payments (id, name) VALUES(");
          sb.append(coPayId).append(",").append(mark).append(coPay).append(mark).append(");")
              .append("\n");
          coPayList.add(sb.toString());
          coPayMap.put(coPay, coPayId);
        }

        // hospital
        int hospitalId = ++hospitalSeq;
        StringBuilder sbr = new StringBuilder(
            "INSERT INTO tbl_hospitals (id, name, address, contact, email, locations_id, payments_id) VALUES(");
        sbr.append(hospitalId).append(",");
        sbr.append(mark).append(name).append(mark).append(",");
        sbr.append(mark).append(address).append(mark).append(",");
        sbr.append(mark).append(phone).append(mark).append(",");
        sbr.append(mark).append(email).append(mark).append(",");
        sbr.append(locationId).append(",");
        sbr.append(coPayId).append(");").append("\n");
        hospitalList.add(sbr.toString());

        // hospital-service relation
        for (int i = 6; i < 39; i++) {
          String serviceId = getCellValue(row.getCell(i));
          if (StringUtils.isNotBlank(serviceId)) {
            StringBuilder sb = new StringBuilder(
                "INSERT INTO tbl_hospital_service_rel (hospital_id, service_id) VALUES(");
            sb.append(hospitalId).append(",").append(serviceId).append(");").append("\n");
            hospitalServiceRelList.add(sb.toString());
          }
        }

      } while (true);

      printSQL(out, hospitalList);
      out.write("\n");
      printSQL(out, serviceList);
      out.write("\n");
      printSQL(out, coPayList);
      out.write("\n");
      printSQL(out, locationList);
      out.write("\n");
      printSQL(out, hospitalServiceRelList);
      log.info("success");
    } catch (Exception e) {
      log.error("exception: {}", e);
    }
  }


  public static void printSQL(BufferedWriter out, List<String> list) {
    list.stream().forEach(s -> {
      try {
        out.write(s);
      } catch (Exception e) {
        log.error("print sql exception: {}", e.getMessage());
      }
    });
  }

  public static String getCellValue(Cell cell) {
    String value = "";
    if (cell == null) {
      return value;
    }
    if (cell.getCellType() == CellType.STRING) {
      value = cell.getStringCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      value = "" + (int) cell.getNumericCellValue();
    } else {
      value = cell.getStringCellValue();
    }
    return value.trim();
  }
}
