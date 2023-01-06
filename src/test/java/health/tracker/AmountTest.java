package health.tracker;

import org.junit.jupiter.api.Test;

import static health.tracker.Amount.parseAmount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AmountTest {

    @Test
    public void shouldParseZeroGrams() {
        var amount = parseAmount("0g");
        assertThat(amount, is(equalTo(new Amount(0, Unit.Gram))));
    }

    @Test
    public void shouldParseDecimalGrams() {
        var amount = parseAmount("5.25g");
        assertThat(amount, is(equalTo(new Amount(5.25, Unit.Gram))));
    }

    @Test
    public void shouldParseDecimalKilograms() {
        var amount = parseAmount("75.5kg");
        assertThat(amount, is(equalTo(new Amount(75.5, Unit.Kilogram))));
    }

    @Test
    public void shouldParseIntegerPercentage() {
        var amount = parseAmount("5%");
        assertThat(amount, is(equalTo(new Amount(5, Unit.Percentage))));
    }

    @Test
    public void shouldParseNegativeDecimalPercentage() {
        var amount = parseAmount("-75.25%");
        assertThat(amount, is(equalTo(new Amount(-75.25, Unit.Percentage))));
    }

    @Test
    public void shouldThrowForInvalidUnit() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("7invalid"));
    }

    @Test
    public void shouldThrowForValueWithoutUnit() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("66"));
    }

    @Test
    public void shouldThrowForUnitWithoutValue() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount("kg"));
    }

    @Test
    public void shouldThrowForNullText() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount(null));
    }

    @Test
    public void shouldThrowForEmptyText() {
        assertThrows(
                RuntimeException.class,
                () -> parseAmount(""));
    }
}