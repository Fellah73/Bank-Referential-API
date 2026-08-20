package dz.banquealgerie.referential.utils.excel.banking.imports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.banking.agence.AgenceDto;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;

public class AgenceExcelParser {
    public static List<AgenceDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<AgenceDto> agences = new ArrayList<>();

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

                AgenceDto agence = new AgenceDto();
                agence.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                agence.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                agence.setEtablissementCode(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));

                agences.add(agence);
            }

            return agences;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des agences : " + e.getMessage());
        }
    }
}
