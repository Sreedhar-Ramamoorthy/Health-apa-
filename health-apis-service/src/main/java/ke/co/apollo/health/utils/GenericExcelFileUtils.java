package ke.co.apollo.health.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.List;
@Service
public class GenericExcelFileUtils {
    public byte[] createExcelFile(List<?> l, String excelName, String sheetName){
        try
        {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(sheetName);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFamily(FontFamily.ROMAN);
            headerFont.setColor(IndexedColors.MAROON.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            int rowNumber = 1;
            int count = 0;
            for (Object o : l)
            {
                Row row = sheet.createRow(0);
                sheet.autoSizeColumn(count);
                createExcelHeaders(o, row,headerCellStyle);
                count ++;
            }
            for (Object o : l)
            {
                Row row = sheet.createRow(rowNumber++);
                createExcelContent(o, row);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            baos.close();
            return baos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static void createExcelContent(Object obj, Row row) throws IllegalAccessException, NoSuchFieldException {
        int count = 0;
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Cell cell = row.createCell(count);
            cell.setCellValue(String.valueOf(field.get(obj) == null ? "-" : field.get(obj)));
            count ++;
        }
    }

    private static void createExcelHeaders(Object obj, Row row, CellStyle headerCellStyle)
    {
        int count = 0;
        for (Field field : obj.getClass().getDeclaredFields()) {
            Cell cell = row.createCell(count);
            cell.setCellValue(field.getName().toUpperCase());
            cell.setCellStyle(headerCellStyle);
            count ++;
        }
    }
}
