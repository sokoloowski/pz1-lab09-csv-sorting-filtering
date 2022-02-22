# Laboratorium 9 - Województwa, powiaty, gminy - sortowanie i filtrowanie

Zapoznaj się z treścią wykładów dotyczących:

- [Intefejsów i klas wewnętrznych](https://home.agh.edu.pl/~pszwed/wiki/lib/exe/fetch.php?media=java:w6-java-interfejsy-klasy-wewnetrzne.pdf)
- [Wyrażeń lambda i strumieni](https://home.agh.edu.pl/~pszwed/wiki/lib/exe/fetch.php?media=java:w9-java-lambdas.pdf)
- Mogą się także przydać informacje o [Typach generycznych](https://home.agh.edu.pl/~pszwed/wiki/lib/exe/fetch.php?media=java:w8-java-generics.pdf)

## Sortujemy listę jednostek administracyjnych

Listy w Java mają funkcję `sort()`, której argumentem jest `Comparator<T>` referencja do klasy zapewniającej funkcję do porównywania elementów `public int compare(T t, T t1)`.

Funkcja powinna zwrócić

- wartość ujemną, jeżeli w wyniku sortowania `t` ma się znaleźć przed `t1`
- 0, jeżeli elementy są równe
- wartość dodatnią, jeżeli `t` ma się znaleźć po `t1`

### Użyj lokalnej nazwanej klasy wewnętrznej

Napisz funkcję - zdefiniuj klasę dziedziczącą po `Comparator<AdminUnit>`. Utwórz obiekt tej klasy i przekaż do funkcji `sort()`

```java
/**
 * Sortuje daną listę jednostek (in place = w miejscu) 
 * @return this
 */
AdminUnitList sortInplaceByName() {...}
```

### Użyj lokalnej klasy anonimowej

Napisz funkcję używając w niej lokalnej klasy anonimowej.

```java
/**
 * Sortuje daną listę jednostek (in place = w miejscu) 
 * @return this
 */
AdminUnitList sortInplaceByArea() {...}
```

### Użyj wyrażenia lambda

Patrz [Wyrażenia lambda i strumienie](https://home.agh.edu.pl/~pszwed/wiki/lib/exe/fetch.php?media=java:w9-java-lambdas.pdf#page=13) przykład na stronie 13

Napisz funkcję korzystając z wyrażenia lambda

```java
/**
 * Sortuje daną listę jednostek (in place = w miejscu) 
 * @return this
 */
AdminUnitList sortInplaceByPopulation() {...}
```

## Piszemy bardziej ogólne funkcje

Zaimplementuj funkcje

```java
AdminUnitList sortInplace(Comparator<AdminUnit> cmp) {
    //...
    return this;
}
```

oraz

```java
AdminUnitList sort(Comparator<AdminUnit> cmp) {
    // Tworzy wyjściową listę
    // Kopiuje wszystkie jednostki
    // woła sortInPlace
}
```

## Filtrowanie

Użyjemy interfejsu `Predicate`, a zwłaszcza jego metody `test()`.

Napisz

```java
/**
 *
 * @param pred referencja do interfejsu Predicate
 * @return nową listę, na której pozostawiono tylko te jednostki, 
 * dla których metoda test() zwraca true
 */
AdminUnitList filter(Predicate<AdminUnit> pred) {...}
```

Poeksperymentuj z różnymi warunkami.

Np. kod, który wyświetla jednostki o nazwach na "Ż" posortowane według powierzchni może wyglądać następująco

```java
list.filter(a->a.name.startsWith("Ż")).sortByArea().list(out);
```

Napisz:

- wybór (i sortowanie) elementów zaczynających się na "K"
- wybór jednostek będących powiatami, których parent.name to województwo małopolskie
- zaproponuj kilka podobnych kryteriów selekcji

Interfejs `Predicate<T>` ma metody (standardowe implementacje metod) `and`, `or` oraz `negate`. Zaproponuj kryteria selekcji używające tych funkcji. Oczywiście za każdym razem możesz używać klas anonimowych w stylu

```java
Predicate<AdminUnit> p = new Predicate() { // i tu naciśnij Alt Enter
```

albo krótszych wyrażeń lambda.

## Ograniczanie liczby wyjściowych elementów

Zaimplementuj filtrowanie z parametrem `limit`

```java
/**
 * Zwraca co najwyżej limit elementów spełniających pred 
 * @param pred - predykat
 * @param limit - maksymalna liczba elementów
 * @return nową listę
 */
AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
    throw new RuntimeException("Not implemented yet")
}
```

Zaimplementuj również funkcję:

```java
/**
 * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
 * Offest jest obliczany po przefiltrowaniu
 * @param pred - predykat
 * @param - od którego elementu
 * @param limit - maksymalna liczba elementów
 * @return nową listę
 */
AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
    throw new RuntimeException("Not implemented yet")
}
```

## AdminUnitQuery

Napisz stosunkowo prostą klasę `AdminUnitQuery` skupiającą specyfikację (i wykonanie) tych wszystkich operacji

```java
public class AdminUnitQuery {
    AdminUnitList src;
    Predicate<AdminUnit> p = a->true;
    Comparator<AdminUnit> cmp;
    int limit = Integer.MAX_VALUE;
    int offset = 0;
 
    /**
     * Ustawia listę jako przetwarzane źródło
     * @param src
     * @return this
     */
    AdminUnitQuery selectFrom(AdminUnitList src){
    ...    
    }
 
    /**
     *
     * @param pred - ustawia predykat p
     * @return this
     */
    AdminUnitQuery where(Predicate<AdminUnit> pred){
    ...    
    }
 
    /**
     * Wykonuje operację p = p and pred
     * @param pred
     * @return this
     */
    AdminUnitQuery and(Predicate<AdminUnit> pred){
    ...    
    }
    /**
     * Wykonuje operację p = p or pred
     * @param pred
     * @return this
     */
    AdminUnitQuery or(Predicate<AdminUnit> pred){
    ...    
    }
 
    /**
     * Ustawia komparator cmp
     * @param cmp
     * @return this
     */
    AdminUnitQuery sort(Comparator<AdminUnit> cmp){
    ...    
    }
 
    /**
     * Ustawia limit
     * @param limit
     * @return this
     */
    AdminUnitQuery limit(int limit){
    ...    
    }
    /**
     * Ustawia offset
     * @param offset
     * @return this
     */
    AdminUnitQuery offset(int offset){
    ...    
    }
 
    /**
     * Wykonuje zapytanie i zwraca wynikową listę
     * @return przefiltrowana i opcjonalnie posortowana lista (uwzględniamy także offset/limit) 
     */
    AdminUnitList execute() {...}
}
```

Przetestuj uzupełniony kod w zapytaniach typu:

```java
AdminUnitQuery query = new AdminUnitQuery()
        .selectFrom(list)
        .where(a->a.area>1000)
        .or(a->a.name.startsWith("Sz"))
        .sort((a,b)->Double.compare(a.area,b.area))
        .limit(100);
query.execute().list(out);
```
