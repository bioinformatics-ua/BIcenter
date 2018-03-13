package configuration;

import java.util.List;

/**
 * Created by david on 02/07/15.
 */
public class CPermission {
    private String category;
    private List<String> operations;

    public CPermission() {
    }

    public CPermission(final String category, final List<String> operations) {
        this.category = category;
        this.operations = operations;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(final List<String> operations) {
        this.operations = operations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CPermission that = (CPermission) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        return !(operations != null ? !operations.equals(that.operations) : that.operations != null);

    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (operations != null ? operations.hashCode() : 0);
        return result;
    }
}
