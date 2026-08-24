package dz.banquealgerie.referential.utils.excel.regulatory.imports;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityDto;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;

public class ActivitiesExcelParser {

    public static List<ActivityDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ActivityDto> activities = new ArrayList<>();

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

                ActivityDto activity = new ActivityDto();

                activity.setCode(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                activity.setCodeSection(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                activity.setDomaine(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));
                activity.setLibelle(ExcelUtils.getCellValueAsString(currentRow.getCell(3)));
                System.out.println(activity);
                activities.add(activity);
            }

            return activities;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des Activités : " + e.getMessage());
        }
    }

}
