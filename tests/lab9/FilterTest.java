package lab9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilterTest {
    String path = "/path/to/lab9/examples/";

    @Test
    public void testFilteringByFirstLetter() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        adminUnitList = adminUnitList.filter(a -> a.name.startsWith("K")).sortInplaceByName();

        for (AdminUnit unit : adminUnitList.units) {
            // Assert that all admin units start with "K"
            assertTrue(unit.name.startsWith("K"));
        }

        for (int i = 0; i < adminUnitList.units.size() - 1; ) {
            // Assert that admin units' names are sorted by name
            assertTrue(-1 >= adminUnitList.units.get(i).name.compareToIgnoreCase(adminUnitList.units.get(++i).name));
        }
    }

    @Test
    public void testFilteringByAdminLevel() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        adminUnitList = adminUnitList.filter(a -> a.adminLevel == 6)
                .filter(a -> a.parent.name.equals("województwo małopolskie"));

        for (AdminUnit unit : adminUnitList.units) {
            assertEquals(6, unit.adminLevel);
            assertEquals("województwo małopolskie", unit.parent.name);
        }
    }

    @Test
    public void testFilteringWithLimit() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 100; i++) {
            AdminUnitList filtered = adminUnitList.filter(a -> true, i);

            // Assert that limit works properly
            assertEquals(i, filtered.units.size());
        }
    }

    @Test
    public void testFilteringWithOffsetAndLimit() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 100; i++) {
            AdminUnitList filtered = adminUnitList.filter(a -> true, i, 1);

            // Assert that limit works properly
            assertEquals(adminUnitList.units.get(i), filtered.units.get(0));
        }
    }
}
