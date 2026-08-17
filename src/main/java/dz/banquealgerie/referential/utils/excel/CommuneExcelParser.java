package dz.banquealgerie.referential.utils.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.geographic.commune.CommuneDto;

public class CommuneExcelParser {
    public static List<CommuneDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<CommuneDto> communes = new ArrayList<>();

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

                CommuneDto commune = new CommuneDto();
                commune.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                commune.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                commune.setLibelleAr(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));
                commune.setWilayaCode(ExcelUtils.getCellValueAsString(currentRow.getCell(3)));

                communes.add(commune);
            }

            return communes;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des communes : " + e.getMessage());
        }
    }
}
