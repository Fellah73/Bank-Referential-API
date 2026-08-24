package dz.banquealgerie.referential.utils.excel.regulatory.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dz.banquealgerie.referential.domain.dto.regulatory.activity.ActivityDto;

public class ActivitiesExcelExporter {

    public static ByteArrayInputStream exportActivitiesToExcel(List<ActivityDto> activities) {
        String[] headers = { "code", "code section", "Domaine", "Libelle",
                "Statut" };

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Activities");

            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerCellStyle.setFont(font);

            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
                cell.setCellStyle(headerCellStyle);
            }

            CellStyle textCellStyle = workbook.createCellStyle();
            DataFormat fmt = workbook.createDataFormat();
            textCellStyle.setDataFormat(fmt.getFormat("@"));

            int rowIdx = 1;
            for (ActivityDto activity : activities) {
                Row row = sheet.createRow(rowIdx++);

                // Cellule 0: CODE (Format Texte)
                Cell cellCode = row.createCell(0);
                cellCode.setCellStyle(textCellStyle);
                cellCode.setCellValue(activity.getCode());

                // Cellule 1: code section
                row.createCell(1).setCellValue(activity.getCodeSection() != null ? activity.getCodeSection() : "");

                // Cellule 2: Domaine (Format Texte)
                Cell cellDomaine = row.createCell(2);
                cellDomaine.setCellStyle(textCellStyle);
                cellDomaine.setCellValue(activity.getDomaine() != null ? activity.getDomaine() : "");

                // Cellule 3: Libelle (Format Texte)
                Cell cellLibelle = row.createCell(3);
                cellLibelle.setCellStyle(textCellStyle);
                cellLibelle.setCellValue(activity.getLibelle() != null ? activity.getLibelle() : "");

                // Cellule 4: Statut
                row.createCell(4).setCellValue(activity.getIsActive() ? "Actif" : "Inactif");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la génération du fichier Excel d'exportation NIF : " + e.getMessage());
        }
    }
}