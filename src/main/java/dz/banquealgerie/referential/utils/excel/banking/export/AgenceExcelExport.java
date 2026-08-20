package dz.banquealgerie.referential.utils.excel.banking.export;

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
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.entities.banking.AgenceEntity;

@Component
public class AgenceExcelExport {
    public static ByteArrayInputStream exportAgencesToExcel(List<AgenceEntity> agences) {
        String[] headers = { "Code", "Libellé", "Code Établissement", "Status" };

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Agences");

            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
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
            for (AgenceEntity agence : agences) {
                Row row = sheet.createRow(rowIdx++);

                Cell cellCode = row.createCell(0);
                cellCode.setCellStyle(textCellStyle);
                cellCode.setCellValue(agence.getCode() != null ? agence.getCode() : "");

                row.createCell(1).setCellValue(agence.getLibelle() != null ? agence.getLibelle() : "");

                Cell cellEtablissementCode = row.createCell(2);
                cellEtablissementCode.setCellStyle(textCellStyle);
                cellEtablissementCode
                        .setCellValue(
                                agence.getEtablissement().getCode() != null ? agence.getEtablissement().getCode() : "");

                row.createCell(3).setCellValue(agence.getIsActive() ? "Actif" : "Inactif");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la génération du fichier Excel d'exportation des agences : " + e.getMessage());
        }
    }
}
