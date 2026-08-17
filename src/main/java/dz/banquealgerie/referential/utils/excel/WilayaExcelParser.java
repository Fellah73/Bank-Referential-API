package dz.banquealgerie.referential.utils.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.geographic.wilaya.WilayaDto;

public class WilayaExcelParser {
    public static List<WilayaDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<WilayaDto> wilayas = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) { 
                    rowNumber++;
                    continue;
                }

                if (ExcelUtils.isRowEmpty(currentRow)) {
                    continue;
                }

                WilayaDto wilaya = new WilayaDto();
                wilaya.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                wilaya.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                wilaya.setLibelleAr(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));

                wilayas.add(wilaya);
            }

            return wilayas;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des wilayas : " + e.getMessage());
        }
    }
}
