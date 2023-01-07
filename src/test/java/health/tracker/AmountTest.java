package health.tracker;

import org.junit.jupiter.api.Test;

import static health.tracker.Amount.parseAmount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountTest {

    @Test
    void shouldParseZeroGrams() {
        var amount = parseAmount("0g");
        assertThat(amount, is(equalTo(new Amount(0, Unit.Gram))));
    }

    @Test
    void shouldParseDecimalGrams() {
        var amount = parseAmount("5.25g");
        assertThat(amount, is(equalTo(new Amount(5.25, Unit.Gram))));
    }

    @Test
    void shouldParseDecimalKilograms() {
        var amount = parseAmount("75.5kg");
        assertThat(amount, is(equalTo(new Amount(75.5, Unit.Kilogram))));
    }

    @Test
    void shouldParseIntegerPercentage() {
        var amount = parseAmount("5%");
        assertThat(amount, is(equalTo(new Amount(5, Unit.Percentage))));
    }

    @Test
    void shouldParseNegativeDecimalPercentage() {
        var amount = parseAmount("-75.25%");
        assertThat(amount, is(equalTo(new Amount(-75.25, Unit.Percentage))));
    }

    @Test
    void shouldThrowForInvalidUnit() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("7invalid"));
    }

    @Test
    void shouldThrowForValueWithoutUnit() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("66"));
    }

    @Test
    void shouldThrowForUnitWithoutValue() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("kg"));
    }

    @Test
    void shouldThrowForNullText() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount(null));
    }

    @Test
    void shouldThrowForEmptyText() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount(""));
    }
}