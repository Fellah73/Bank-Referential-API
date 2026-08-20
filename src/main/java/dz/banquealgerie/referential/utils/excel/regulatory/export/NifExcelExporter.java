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
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.regulatory.nif.NifResponseDto;

@Component
public class NifExcelExporter {
    public static ByteArrayInputStream exportNifsToExcel(List<NifResponseDto> nifs) {
        String[] headers = { "NIF", "RC", "Code Activité", "Date Création", "Désignation", "Code Forme Juridique",
                "Statut" };

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("NIFs");

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
            for (NifResponseDto nif : nifs) {
                Row row = sheet.createRow(rowIdx++);

                // Cellule 0: NIF (Format Texte)
                Cell cellNif = row.createCell(0);
                cellNif.setCellStyle(textCellStyle);
                cellNif.setCellValue(nif.getNif());

                // Cellule 1: RC
                row.createCell(1).setCellValue(nif.getRc() != null ? nif.getRc() : "");

                // Cellule 2: Code Activité (Format Texte)
                Cell cellAct = row.createCell(2);
                cellAct.setCellStyle(textCellStyle);
                cellAct.setCellValue(nif.getActivity() != null ? nif.getActivity().getCode() : "");

                // Cellule 3: Date Création
                row.createCell(3).setCellValue(nif.getDateCreation() != null ? nif.getDateCreation().toString() : "");

                // Cellule 4: Désignation
                row.createCell(4).setCellValue(nif.getDesignation() != null ? nif.getDesignation() : "");

                // Cellule 5: Code Forme Juridique (Format Texte)
                Cell cellFj = row.createCell(5);
                cellFj.setCellStyle(textCellStyle);
                cellFj.setCellValue(nif.getFormeJuridique() != null ? nif.getFormeJuridique().getCode() : "");

                // Cellule 6: Statut
                row.createCell(6).setCellValue(Boolean.TRUE.equals(nif.getIsActive()) ? "Actif" : "Inactif");
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
