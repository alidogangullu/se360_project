import javax.swing.*;

public class EmptyButtonException extends Exception{
    public EmptyButtonException() {
        super("One or more buttons are empty.");
        JOptionPane.showMessageDialog(null, "One or more buttons are not selected.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public EmptyButtonException(String errorMessage) {
        super(errorMessage);
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
