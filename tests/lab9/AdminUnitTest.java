package lab9;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class AdminUnitTest {
    String path = "/path/to/lab9/examples/";

    @Test
    public void testFileDoesNotExist() {
        assertThrows(FileNotFoundException.class, () -> {
            CSVReader csvReader = new CSVReader("THIS_FILE_DOES_NOT_EXIST");
        });
    }

    @Test
    public void testListWithOffsetAndLimit() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        adminUnitList.list(ps, 1, 1);
        String result = os.toString(StandardCharsets.UTF_8);

        assertTrue(result.contains("Kolonia Południowa"));
        assertTrue(result.contains("typ: "));
        assertTrue(result.contains("powierzchnia: "));
    }

    @Test
    public void testFindUnitsOnGivenLevel() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);

        AdminUnitList found = adminUnitList.selectByName("Mystków", false);
        assertEquals(1, found.units.size());

        AdminUnit unit = found.units.get(0);

        double t1 = System.nanoTime() / 1e6;
        AdminUnitList neighbors = adminUnitList.getNeighbors(unit, 10);
        double t2 = System.nanoTime() / 1e6;
        System.out.printf(Locale.US, "Czas wyszukiwania sasiadow: %f\n", t2 - t1);

        neighbors.list(ps);
        String result = os.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Paszyn"));
        assertTrue(result.contains("Królowa Polska"));
        assertTrue(result.contains("Kamionka Wielka"));
        assertTrue(result.contains("Mszalnica"));
        assertTrue(result.contains("Cieniawa"));
    }
}
