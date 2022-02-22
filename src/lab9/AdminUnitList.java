package lab9;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();
    Map<Long, AdminUnit> idToAdminUnit = new HashMap<>();
    Map<AdminUnit, Long> adminUnitToParentId = new HashMap<>();
    Map<Long, List<AdminUnit>> parentIdToChildren = new HashMap<>();

    /**
     * Czyta rekordy pliku i dodaje do listy
     *
     * @param filename nazwa pliku
     */
    void read(String filename) {
        CSVReader reader = null;

        try {
            reader = new CSVReader(filename, ",", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (reader != null && reader.next()) {
            AdminUnit adminUnit = new AdminUnit();

            adminUnit.name = reader.get("name");

            try {
                adminUnit.adminLevel = reader.getInt("admin_level");
            } catch (Exception e) {
                adminUnit.adminLevel = -1;
            }

            try {
                adminUnit.population = reader.getInt("population");
            } catch (Exception e) {
                adminUnit.population = -1;
            }

            try {
                adminUnit.area = reader.getDouble("area");
            } catch (Exception e) {
                adminUnit.area = -1;
            }

            try {
                adminUnit.density = reader.getDouble("density");
            } catch (Exception e) {
                adminUnit.density = -1;
            }

            try {
                adminUnit.bbox.xmin = Math.min(
                        Math.min(
                                reader.getDouble("x1"),
                                reader.getDouble("x2")),
                        Math.min(
                                reader.getDouble("x3"),
                                reader.getDouble("x4")));
                adminUnit.bbox.ymin = Math.min(
                        Math.min(
                                reader.getDouble("y1"),
                                reader.getDouble("y2")),
                        Math.min(
                                reader.getDouble("y3"),
                                reader.getDouble("y4")));
                adminUnit.bbox.xmax = Math.max(
                        Math.max(
                                reader.getDouble("x1"),
                                reader.getDouble("x2")),
                        Math.max(
                                reader.getDouble("x3"),
                                reader.getDouble("x4")));
                adminUnit.bbox.ymax = Math.max(
                        Math.max(
                                reader.getDouble("y1"),
                                reader.getDouble("y2")),
                        Math.max(
                                reader.getDouble("y3"),
                                reader.getDouble("y4")));
            } catch (Exception e) {
                // Jeżeli chociaż jedna wartość będzie pusta,
                // to cały bounding box będzie zły
                adminUnit.bbox.xmin = Double.NaN;
                adminUnit.bbox.ymin = Double.NaN;
                adminUnit.bbox.xmax = Double.NaN;
                adminUnit.bbox.ymax = Double.NaN;
            }

            long parentId = -1;
            try {
                parentId = reader.getLong("parent");
            } catch (Exception e) {
                // nie ma parenta
            }

            try {
                this.idToAdminUnit.put(reader.getLong("id"), adminUnit);
            } catch (Exception e) {
                // nie ma opcji, że nie będzie ID
                e.printStackTrace();
            }
            this.adminUnitToParentId.put(adminUnit, parentId);

            if (!this.parentIdToChildren.containsKey(parentId)) {
                this.parentIdToChildren.put(parentId, new ArrayList<>());
            }

            this.parentIdToChildren.get(parentId).add(adminUnit);

            // dodaj do listy
            this.units.add(adminUnit);
        }

        for (AdminUnit unit : this.units) {
            long parentId = this.adminUnitToParentId.get(unit);
            unit.parent = this.idToAdminUnit.getOrDefault(parentId, null);
        }

        for (Map.Entry<Long, AdminUnit> entry : this.idToAdminUnit.entrySet()) {
            entry.getValue().children = this.parentIdToChildren.get(entry.getKey());
        }
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     *
     * @param out - strumień wyjsciowy
     */
    void list(PrintStream out) {
        for (AdminUnit unit : this.units) {
            out.print(unit);
        }
    }

    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     *
     * @param out    - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit  - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out, int offset, int limit) {
        for (int i = 0; i < limit; i++) {
            out.print(this.units.get(i + offset));
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     *
     * @param pattern - wzorzec dla nazwy
     * @param regex   - jeśli regex=true, użyj funkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();

        // przeiteruj po zawartości units
        for (AdminUnit unit : this.units) {
            if (regex) {
                if (unit.name.matches(pattern)) {
                    // jeżeli nazwa jednostki pasuje do wzorca dodaj do ret
                    ret.units.add(unit);
                }
            } else {
                if (unit.name.contains(pattern)) {
                    // jeżeli nazwa jednostki pasuje do wzorca dodaj do ret
                    ret.units.add(unit);
                }
            }
        }
        return ret;
    }

    private void fixMissingValues() {
        for (AdminUnit unit : this.units) {
            unit.fixMissingValues();
        }
    }

    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     *
     * @param unit        - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance) {
        AdminUnitList ret = new AdminUnitList();

        for (AdminUnit _unit : this.units) {
            if (_unit.adminLevel == unit.adminLevel && _unit != unit) {
                if (unit.bbox.intersects(_unit.bbox)) {
                    ret.units.add(_unit);
                } else {
                    try {
                        if (unit.adminLevel == 8 && unit.bbox.distanceTo(_unit.bbox) <= maxdistance) {
                            ret.units.add(_unit);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return ret;
    }

    AdminUnitList sortInplaceByName() {
        class Sortowanko implements Comparator<AdminUnit> {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return o1.name.compareToIgnoreCase(o2.name);
            }
        }

        Sortowanko sortowanko = new Sortowanko();
        this.units.sort(sortowanko);

        return this;
    }

    AdminUnitList sortInplaceByArea() {
        Comparator<AdminUnit> sortowanko = new Comparator<AdminUnit>() {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area, o2.area);
            }
        };

        this.units.sort(sortowanko);

        return this;
    }

    AdminUnitList sortInplaceByPopulation() {
        this.units.sort((AdminUnit o1, AdminUnit o2) -> Double.compare(o1.population, o2.population));

        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> comparator) {
        this.units.sort(comparator);

        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> comparator) {
        AdminUnitList adminUnitList = new AdminUnitList();
        adminUnitList.units = this.units;

        adminUnitList.sortInplace(comparator);

        return adminUnitList;
    }

    AdminUnitList filter(Predicate<AdminUnit> predicate) {
        AdminUnitList adminUnitList = new AdminUnitList();

        for (AdminUnit unit : this.units) {
            if (predicate.test(unit)) {
                adminUnitList.units.add(unit);
            }
        }

        return adminUnitList;
    }

    AdminUnitList filter(Predicate<AdminUnit> predicate, int limit) {
        AdminUnitList adminUnitList = this.filter(predicate);
        AdminUnitList res = new AdminUnitList();

        for (int i = 0; i < limit; i++) {
            res.units.add(adminUnitList.units.get(i));
        }

        return res;
    }

    AdminUnitList filter(Predicate<AdminUnit> predicate, int offset, int limit) {
        AdminUnitList adminUnitList = this.filter(predicate);
        AdminUnitList res = new AdminUnitList();

        for (int i = 0; i < limit; i++) {
            res.units.add(adminUnitList.units.get(i + offset));
        }

        return res;
    }
}
