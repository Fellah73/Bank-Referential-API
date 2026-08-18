package dz.banquealgerie.referential.utils.excel.banking;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.banking.etablissement.EtablissementDto;
import dz.banquealgerie.referential.domain.dto.geographic.pays.PaysDto;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;

public class EtablissementExcelParser {
    public static List<EtablissementDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<EtablissementDto> etablissementList = new ArrayList<>();

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

                EtablissementDto etablissement = new EtablissementDto();
                etablissement.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                etablissement.setAbreviation(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                etablissement.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));

                etablissementList.add(etablissement);
            }

            return etablissementList;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur lors de la lecture du fichier Excel des établissements : " + e.getMessage());
        }
    }
}
