package dz.banquealgerie.referential.utils.excel.regulatory.imports;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import dz.banquealgerie.referential.domain.dto.regulatory.NifDto;
import dz.banquealgerie.referential.utils.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NifExcelParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<NifDto> parse(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Iterator<Row> rows = sheet.iterator();
            List<NifDto> nifs = new ArrayList<>();

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

                NifDto nif = new NifDto();
                LocalDate dateCreation = NifExcelParser.parseCellDate(currentRow.getCell(3), formatter);

                nif.setNif(ExcelUtils.getCellValueAsString(currentRow.getCell(0)));
                nif.setRc(ExcelUtils.getCellValueAsString(currentRow.getCell(1)));
                nif.setActivityCode(ExcelUtils.getCellValueAsString(currentRow.getCell(2)));
                nif.setDateCreation(dateCreation);
                nif.setDesignation(ExcelUtils.getCellValueAsString(currentRow.getCell(4)));
                nif.setFormeJuridiqueCode(ExcelUtils.getCellValueAsString(currentRow.getCell(5)));

                nifs.add(nif);
            }

            return nifs;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel des NIF : " + e.getMessage());
        }
    }

    private static LocalDate parseCellDate(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String cellValue = formatter.formatCellValue(cell).trim();
        if (cellValue.isEmpty()) {
            return null;
        }

        return LocalDate.parse(cellValue, DATE_FORMATTER);
    }

}
