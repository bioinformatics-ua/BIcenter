package models;

import javax.persistence.*;

@Entity
public class KeyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DataRow dataRow;

    private String colKey;
    private String colValue;

    public KeyValue() {
    }

    public KeyValue(String key, String value) {
        this.colKey = key;
        this.colValue = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataRow getDataRow() {
        return dataRow;
    }

    public void setDataRow(DataRow dataRow) {
        this.dataRow = dataRow;
    }

    public String getColKey() {
        return colKey;
    }

    public void setColKey(String colKey) {
        this.colKey = colKey;
    }

    public String getColValue() {
        return colValue;
    }

    public void setColValue(String value) {
        this.colValue = value;
    }
}
