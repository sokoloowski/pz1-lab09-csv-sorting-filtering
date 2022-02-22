package lab9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryTest {
    String path = "/path/to/lab9/examples/";

    @Test
    public void testQueryExecution() {
        AdminUnitList adminUnitList = new AdminUnitList();
        try {
            adminUnitList.read(this.path + "admin-units.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(adminUnitList)
                .where(a -> a.area > 1000)
                .or(a -> a.name.startsWith("Sz"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);

        AdminUnitList res = query.execute();

        assertTrue(res.units.size() <= 100);

    }
}
