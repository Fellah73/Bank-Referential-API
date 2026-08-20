package dz.banquealgerie.referential.utils.excel.geographical.export;

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

import dz.banquealgerie.referential.domain.entities.geographic.PaysEntity;

@Component
public class PaysExcelExport {
    public static ByteArrayInputStream exportPaysToExcel(List<PaysEntity> paysList) {
        String[] headers = { "Code", "Libellé", "Abréviation", "Status" };

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Pays");

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
            for (PaysEntity pays : paysList) {
                Row row = sheet.createRow(rowIdx++);

                Cell cellCode = row.createCell(0);
                cellCode.setCellStyle(textCellStyle);
                cellCode.setCellValue(pays.getCode() != null ? pays.getCode() : "");

                row.createCell(1).setCellValue(pays.getLibelle() != null ? pays.getLibelle() : "");

                row.createCell(2).setCellValue(pays.getAbreviation() != null ? pays.getAbreviation() : "");

                row.createCell(3).setCellValue(Boolean.TRUE.equals(pays.getIsActive()) ? "Actif" : "Inactif");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la génération du fichier Excel d'exportation des pays : " + e.getMessage());
        }
    }

}
