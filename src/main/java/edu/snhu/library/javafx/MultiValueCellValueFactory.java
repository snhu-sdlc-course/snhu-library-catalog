package edu.snhu.library.javafx;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.StringUtils;

public class MultiValueCellValueFactory<S> extends PropertyValueFactory<S, String> {
    private final String separator;

    /**
     * Creates a default PropertyValueFactory to extract the value from a given
     * TableView row item reflectively, using the given property name.
     *
     * @param property The name of the property with which to attempt to
     *                 reflectively extract a corresponding value for in a given object.
     */
    public MultiValueCellValueFactory(@NamedArg("property") String property, @NamedArg(value = "separator", defaultValue = ";") String separator) {
        super(property);
        this.separator = separator;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<S, String> param) {
        final ObservableValue<String> unformattedValue = super.call(param);
        final SimpleStringProperty formattedValue = new SimpleStringProperty(formatToSingleLine(unformattedValue.getValue()));
        unformattedValue.addListener((observable, oldValue, newValue) -> {
            formattedValue.set(formatToSingleLine(newValue));
        });
        return formattedValue;
    }

    String formatToSingleLine(final String originalValue) {
        if(StringUtils.isNotBlank(originalValue)) {
            return StringUtils.replace(originalValue, "\n", separator);
        }
        return "";
    }
}
