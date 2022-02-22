package lab9;

import java.util.Comparator;
import java.util.function.Predicate;

public class AdminUnitQuery {
    AdminUnitList source;
    Predicate<AdminUnit> predicate = a -> true;
    Comparator<AdminUnit> comparator;
    int limit = Integer.MAX_VALUE;
    int offset = 0;

    /**
     * Ustawia listę jako przetwarzane źródło
     *
     * @param src
     * @return this
     */
    AdminUnitQuery selectFrom(AdminUnitList src) {
        this.source = src;

        return this;
    }

    /**
     * @param pred - ustawia predykat p
     * @return this
     */
    AdminUnitQuery where(Predicate<AdminUnit> pred) {
        this.predicate = pred;

        return this;
    }

    /**
     * Wykonuje operację p = p and pred
     *
     * @param predicate
     * @return this
     */
    AdminUnitQuery and(Predicate<AdminUnit> predicate) {
        this.predicate = this.predicate.and(predicate);

        return this;
    }

    /**
     * Wykonuje operację p = p or pred
     *
     * @param predicate
     * @return this
     */
    AdminUnitQuery or(Predicate<AdminUnit> predicate) {
        this.predicate = this.predicate.or(predicate);

        return this;
    }

    /**
     * Ustawia komparator cmp
     *
     * @param comparator
     * @return this
     */
    AdminUnitQuery sort(Comparator<AdminUnit> comparator) {
        this.comparator = comparator;

        return this;
    }

    /**
     * Ustawia limit
     *
     * @param limit
     * @return this
     */
    AdminUnitQuery limit(int limit) {
        this.limit = limit;

        return this;
    }

    /**
     * Ustawia offset
     *
     * @param offset
     * @return this
     */
    AdminUnitQuery offset(int offset) {
        this.offset = offset;

        return this;
    }

    /**
     * Wykonuje zapytanie i zwraca wynikową listę
     *
     * @return przefiltrowana i opcjonalnie posortowana lista (uwzględniamy także offset/limit)
     */
    AdminUnitList execute() {
        return this.source.filter(this.predicate, this.offset, this.limit).sort(this.comparator);
    }
}
