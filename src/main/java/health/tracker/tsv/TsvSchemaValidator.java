package health.tracker.tsv;

public class TsvSchemaValidator {
    private final String[] columns;

    public TsvSchemaValidator(String... columns) {
        this.columns = columns;
    }

    public void validate(TsvReader tsvReader) {

        // count
        var columns = tsvReader.getColumns();
        var size = this.columns.length;
        if (columns.length != size) {
            throw new RuntimeException(
                    "Expected " + this.columns.length + " columns in time series TSV file: " + tsvReader.getPath());
        }

        // names
        for (var i = 0; i < size; i++) {
            if (!columns[i].equals(this.columns[i])) {
                throw new RuntimeException(
                        "Expected '" + this.columns[i] + "' column in TSV file: " + tsvReader.getPath());
            }
        }
    }
}