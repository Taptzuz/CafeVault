package me.finn.cafevault.gui;

import me.finn.cafevault.CafeVault;
import me.finn.cafevault.util.CBPoolUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class CVGui {
    private JPanel cvPane;
    private JLabel lblInput;
    private JTextField inputField;
    private JButton btnInput;

    private JLabel lblOutput;
    private JTextField outputField;
    private JButton btnOutput;

    private JButton btnCrypt;
    private JLabel lblProgress;
    private JCheckBox executeBox;
    private JLabel lblMain;
    private JTextField mainField;
    private JSlider lenSlider;
    private JLabel lblLength;
    private JButton btnDetect;

    private File input, output;
    private String mainClass;

    private CafeVault cafeVault;

    public CVGui() {
        cafeVault = new CafeVault();
        JFrame cvFrame = new JFrame("CafeVault v" + CafeVault.version);
        cvFrame.setContentPane(this.cvPane);
        cvFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cvFrame.setBounds(0, 0, 320, 200);
        cvFrame.pack();
        cvFrame.setVisible(true);

        lblLength.setText(String.valueOf(lenSlider.getValue()));

        btnInput.addActionListener(e -> {
            JFileChooser inputChooser = new JFileChooser("Select a .jar File");
            inputChooser.setFileFilter(new FileNameExtensionFilter("Jar-File", "jar"));
            inputChooser.showDialog(cvFrame, "Select");
            inputField.setText(inputChooser.getSelectedFile().getAbsolutePath());
        });

        btnOutput.addActionListener(e -> {
            JFileChooser outputSelect = new JFileChooser("Set Output-File");
            outputSelect.showSaveDialog(cvFrame);
            outputField.setText(outputSelect.getSelectedFile().getAbsolutePath());
        });

        btnDetect.addActionListener(e -> {
            if (inputField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill out field \"Input\"!");
                return;
            }
            setProgress("Fetching Main-Class...");
            freeze(true);
            String mainClass = null;
            try {
                mainClass = cafeVault.getMainClass(new File(inputField.getText()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            if (mainClass == null) {
                JOptionPane.showMessageDialog(null, "Failed to fetch Main-Class, please type it manually!");
                freeze(false);
                return;
            }

            mainField.setText(mainClass);
            freeze(false);
            setProgress("");
        });

        lenSlider.addChangeListener(e -> lblLength.setText(String.valueOf(lenSlider.getValue())));

        btnCrypt.addActionListener(e -> {
            if (inputField.getText().isEmpty() || outputField.getText().isEmpty() || mainField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill out all fields!");
                return;
            }
            input = new File(inputField.getText());
            String outputPath = outputField.getText();
            output = new File(outputPath.endsWith(".jar") ? outputPath : outputPath + ".jar");
            mainClass = mainField.getText().replace("/", ".");
            CafeVault.mainClass = mainClass;
            CBPoolUtils.refreshList();

            freeze(true);
            setProgress("Encrypting...");
            boolean crypt = cafeVault.crypt(input, output, lenSlider.getValue(), executeBox.isSelected());
            if (!crypt) {
                setProgress("Failed!");
                freeze(false);
                return;
            }
            setProgress("Successful, saved to: " + output.getAbsolutePath());
            freeze(false);
        });
    }

    private void freeze(boolean state) {
        SwingUtilities.invokeLater(() -> {
            inputField.setEnabled(!state);
            btnInput.setEnabled(!state);

            outputField.setEnabled(!state);
            btnOutput.setEnabled(!state);
            executeBox.setEnabled(!state);

            btnDetect.setEnabled(!state);
            btnCrypt.setEnabled(!state);
        });
    }

    private void setProgress(String text) {
        SwingUtilities.invokeLater(() -> lblProgress.setText(text));
    }
}
