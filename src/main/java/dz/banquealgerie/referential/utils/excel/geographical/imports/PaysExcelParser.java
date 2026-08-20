package dz.banquealgerie.referential.utils.excel.geographical.imports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;

public class PaysExcelParser {
    public static List<PaysDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<PaysDto> paysList = new ArrayList<>();

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

                PaysDto pays = new PaysDto();
                pays.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                pays.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                pays.setAbreviation(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));

                paysList.add(pays);
            }

            return paysList;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des pays : " + e.getMessage());
        }
    }
}
