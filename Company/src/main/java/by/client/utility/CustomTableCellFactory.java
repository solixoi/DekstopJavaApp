package by.client.utility;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CustomTableCellFactory {

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(String suffix) {
        return column -> new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString() + suffix);
                }
            }
        };
    }
}