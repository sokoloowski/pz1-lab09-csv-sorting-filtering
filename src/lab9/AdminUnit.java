package lab9;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent;
    List<AdminUnit> children;
    BoundingBox bbox = new BoundingBox();

    public String toString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.printf("%s:\n\ttyp: %d\n\tpowierzchnia: %f\n\n", this.name, this.adminLevel, this.area);
        return os.toString(StandardCharsets.UTF_8);
    }

    void fixMissingValues() {
        // Najbardziej nas interesuje, by parent miał density
        if (this.parent.density == -1) {
            this.parent.fixMissingValues();
        }

        if (this.density == -1) {
            this.density = this.parent.density;
        }

        if (this.population == -1) {
            this.population = this.area * this.density;
        }
    }

    String getWKT() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.printf(Locale.US, "LINESTRING(%f %f, %f %f, %f %f, %f %f,%f %f)\n",
                this.bbox.xmin,
                this.bbox.ymax,

                this.bbox.xmin,
                this.bbox.ymin,

                this.bbox.xmax,
                this.bbox.ymin,

                this.bbox.xmax,
                this.bbox.ymax,

                this.bbox.xmin,
                this.bbox.ymax
        );

        return os.toString(StandardCharsets.UTF_8);
    }
}
