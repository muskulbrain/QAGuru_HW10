import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class ZipParsingTest {
    private ClassLoader cl = ZipParsingTest.class.getClassLoader();

    @Test
    void readFromPdfFileZip() throws Exception {
        try (InputStream files = cl.getResourceAsStream("rar.zip");
             ZipInputStream zis = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("Statistika_i_kotiki-3.pdf")) {
                    PDF pdf = new PDF(zis);
                    Assertions.assertEquals(1, pdf.numberOfPages);
                }
            }
        }
    }

    @Test
    void readFromXlsFileZip() throws Exception {
        try (InputStream files = cl.getResourceAsStream("rar.zip");
             ZipInputStream zis = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("STS.xlsx")) {
                    XLS xls = new XLS(zis);
                    assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(1).getStringCellValue());
                }
            }
        }
    }

    @Test
    void readFromCsvFileZip() throws Exception {
        try (InputStream files = cl.getResourceAsStream("rar.zip");
             ZipInputStream zis = new ZipInputStream(files)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("MINERALS.csv")) {
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> string = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[]{"Abenakiite-(Ce)", "\"Na_26_Ce_6_(SiO_3_)_6_(PO_4_)_6_(CO_3_)_6_(SO_2_)O\""}, string.get(1));
                }
            }
        }
    }

    @Test
    void readFromJsonFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = cl.getResourceAsStream("UserInfo.json");
             InputStreamReader isr = new InputStreamReader(is)) {
            User user = mapper.readValue(isr, User.class);
            Assertions.assertEquals("2004-04-01T00:00:00+08:00", user.Birthday);
            Assertions.assertEquals(0, user.Bonus);
            Assertions.assertEquals(0, user.CashbackPercent);
            Assertions.assertEquals(272, user.CityId);
            Assertions.assertEquals(1351, user.ClientServiceCenterId);
            Assertions.assertEquals(List.of("User", "Client", "Other"), user.Roles);
        }
    }

}
