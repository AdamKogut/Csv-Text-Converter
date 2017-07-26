import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The view component of this GUI program
 */
public class NewViews {
  private JButton fromFileButton;
  private JButton toFileButton;
  private JButton submitButton;
  private JLabel fromFileLbl;
  private JLabel toFileLbl;
  private JButton convertAnother;
  private JTextField toFileName;
  private JLabel statusLbl;
  private JPanel panel;
  private JLabel recentConvLbl;
  private JButton recentButton4;
  private JButton recentButton3;
  private JButton recentButton2;
  private JButton recentButton1;
  private JLabel recentLbl1;
  private JLabel recentLbl2;
  private JLabel recentLbl3;
  private JLabel recentLbl4;
  private JTextField sidFilter;
  private MainConversion mc;
  private File fromFile;
  private File toFile;
  private JFileChooser fc;
  private static JFrame frame;
  private JButton[] recentButtons;
  private JLabel[] recentLabels;

  public static void main(String[] args) {
    frame = new JFrame("Converter");
    frame.setContentPane(new NewViews().panel);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private NewViews() {
    //allows the panel to be set focusable so the text boxes can be cleared and overwritten
    panel.setFocusable(true);
    mc = new MainConversion();

    //creates a button array that allows better componentization
    recentButtons = new JButton[4];
    recentButtons[0] = recentButton1;
    recentButtons[1] = recentButton2;
    recentButtons[2] = recentButton3;
    recentButtons[3] = recentButton4;

    //sets all recent buttons to be disabled and to be invisible
    for (int i = 0; i < recentButtons.length; i++) {
      recentButtons[i].setEnabled(false);
      recentButtons[i].setVisible(false);
    }

    //creates a label array that makes it easier to componentize
    recentLabels = new JLabel[4];
    recentLabels[0] = recentLbl1;
    recentLabels[1] = recentLbl2;
    recentLabels[2] = recentLbl3;
    recentLabels[3] = recentLbl4;

    //sets the text box letters to gray
    toFileName.setForeground(Color.gray);
    sidFilter.setForeground(Color.gray);

    //creates the array to store the recent conversions
    String[][] recentArray = new String[4][3];
    for (int i = 0; i < recentArray.length; i++) {
      for (int j = 0; j < recentArray[0].length; j++) {
        recentArray[i][j] = null;
      }
    }

    //this happens when the Convert Another is clicked
    convertAnother.addActionListener(e -> {
      //sets the text boxes to their original text and makes them gray
      toFileName.setText("Enter text file name here");
      toFileName.setForeground(Color.gray);
      sidFilter.setText("SID Filter (Optional)");
      sidFilter.setForeground(Color.gray);

      //clears out the current files and resets their text
      fromFile = null;
      toFile = null;
      toFileLbl.setText("Target Directory Path");
      fromFileLbl.setText("CSV file Location");

      //resets the buttons to their original enabled states
      submitButton.setEnabled(true);
      toFileButton.setEnabled(true);
      fromFileButton.setEnabled(true);
      convertAnother.setEnabled(false);
      statusLbl.setText("Waiting...");
    });
    convertAnother.setEnabled(false);

    //this happens when the Submit is clicked
    submitButton.addActionListener(e -> {
      //checks that all of the mandatory fields are filled in
      if (!toFileName.getText().equals("Enter text file name here") && toFile != null && fromFile != null) {
        try {
          sendToConverter();

          //sets buttons and fields to the submitted set up
          panel.requestFocus();
          submitButton.setEnabled(false);
          toFileButton.setEnabled(false);
          fromFileButton.setEnabled(false);
          convertAnother.setEnabled(true);
          statusLbl.setText("Done.");

          //changes the recent selections array to add the most current one
          setRecentSelections(recentArray);

          //sets the recent selections labels to show the user the recent selections
          recentLblSetting(recentArray);

          frame.pack();
        } catch (FileNotFoundException err) {
          //shows the user that there was an error
          statusLbl.setText("ERROR: CANNOT CONVERT .CSV FILE TO .TXT FILE");
        }
      } else {
        //if the user doesn't fill in all of the fields then it tells the user to try again
        statusLbl.setText("Please fill in all of the fields and submit again");
      }
    });

    //brings up the directory file chooser when the user clicks on the destination button
    toFileButton.addActionListener(e -> {
      fc = new JFileChooser();
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = fc.showOpenDialog(panel);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        toFile = fc.getSelectedFile();
        toFileLbl.setText(toFile.getPath() + '\\');
      }
    });

    //brings up the csv file chooser when the user clicks on the source destination button
    fromFileButton.addActionListener(e -> {
      fc = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter(".csv extensions", "csv");
      fc.setFileFilter(filter);
      int returnVal = fc.showOpenDialog(panel);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        fromFile = fc.getSelectedFile();
        fromFileLbl.setText(fromFile.getAbsolutePath());
      }
    });

    //each of these are fired when one of the select buttons are clicked
    recentButton1.addActionListener(e -> selectConversion(0, recentArray));

    recentButton2.addActionListener(e -> selectConversion(1, recentArray));

    recentButton3.addActionListener(e -> selectConversion(2, recentArray));

    recentButton4.addActionListener(e -> selectConversion(3, recentArray));

    //clears the file name text box when the user hasn't changed the file name
    // and the text box is clicked
    toFileName.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (toFileName.getText().equals("Enter text file name here")) {
          toFileName.setText("");
        }
        toFileName.setForeground(Color.BLACK);
      }
    });

    //clears the filter text box when the user hasn't changed the filter and the text box is clicked
    sidFilter.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (sidFilter.getText().equals("SID Filter (Optional)")) {
          sidFilter.setText("");
        }
        sidFilter.setForeground(Color.BLACK);
      }
    });
  }

  /**
   * Method that takes the inputted file namesand sends them to the converter
   *
   * @throws FileNotFoundException thrown from the main conversion csvFileToText method
   */
  private void sendToConverter() throws FileNotFoundException {
    //checks if the filter text box has been changed
    if (sidFilter.getText().equals("") || sidFilter.getText().equals("SID Filter (Optional)")) {
      //checks if the user inputted file name includes the .txt extension
      if (toFileName.getText().length() < 5 || !toFileName.getText().substring(toFileName.getText().length() - 4).equals(".txt")) {
        //adds the .txt extension and sends the files to the converter
        mc.csvFileToText(fromFile.getAbsolutePath(), toFile.getPath() + '\\' + toFileName.getText() + ".txt", null);
      } else {
        //sends the files to the converter
        mc.csvFileToText(fromFile.getAbsolutePath(), toFile.getPath() + '\\' + toFileName.getText(), null);
      }
    } else {
      //checks if the user inputted file name includes the .txt extension
      if (toFileName.getText().length() < 5 || !toFileName.getText().substring(toFileName.getText().length() - 4).equals(".txt")) {
        //adds the .txt extension and sends the files to the converter
        mc.csvFileToText(fromFile.getAbsolutePath(), toFile.getPath() + '\\' + toFileName.getText() + ".txt", sidFilter.getText());
      } else {
        //sends the files to the converter
        mc.csvFileToText(fromFile.getAbsolutePath(), toFile.getPath() + '\\' + toFileName.getText(), sidFilter.getText());
      }
    }
  }

  /**
   * Called if a select button has been clicked, it enables all of the buttons except it disables
   * convert another, it also sets the text fields to what the select button dictates
   *
   * @param chosenNum   is the array index of the chosen button
   * @param recentArray holds the array of the last 4 querys
   */
  private void selectConversion(int chosenNum, String[][] recentArray) {
    //assigns the previous files to the files and directories
    fromFile = new File(recentArray[chosenNum][0]);
    toFile = new File(recentArray[chosenNum][1].substring(0, recentArray[chosenNum][1].lastIndexOf('\\')));
    //sets the labels to the previous files and filters
    toFileName.setText(recentArray[chosenNum][1].substring(recentArray[chosenNum][1].lastIndexOf('\\') + 1));
    toFileLbl.setText(recentArray[chosenNum][1].substring(0, recentArray[chosenNum][1].lastIndexOf('\\') + 1));
    fromFileLbl.setText(fromFile.getAbsolutePath());
    if (recentArray[chosenNum][2] != null) {
      sidFilter.setText(recentArray[chosenNum][2]);
      sidFilter.setForeground(Color.BLACK);
    } else {
      sidFilter.setForeground(Color.gray);
      sidFilter.setText("SID Filter (Optional)");
    }
    //resets the buttons to their original set up
    toFileName.setForeground(Color.BLACK);
    submitButton.setEnabled(true);
    toFileButton.setEnabled(true);
    fromFileButton.setEnabled(true);
    convertAnother.setEnabled(false);
    statusLbl.setText("Waiting...");
  }

  /**
   * adds the submitted query into the array
   *
   * @param recentArray the array that holds the past 4 submitted querys
   */
  private void setRecentSelections(String[][] recentArray) {
    //assigns and moves the fields down the array in the background
    recentArray[3][0] = recentArray[2][0];
    recentArray[3][1] = recentArray[2][1];
    recentArray[3][2] = recentArray[2][2];
    recentArray[2][0] = recentArray[1][0];
    recentArray[2][1] = recentArray[1][1];
    recentArray[2][2] = recentArray[1][2];
    recentArray[1][0] = recentArray[0][0];
    recentArray[1][1] = recentArray[0][1];
    recentArray[1][2] = recentArray[0][2];
    recentArray[0][0] = fromFile.getAbsolutePath();
    recentArray[0][1] = toFile.getPath() + '\\' + toFileName.getText();
    if (sidFilter.getText() != null && !sidFilter.getText().equals("SID Filter (Optional)")) {
      recentArray[0][2] = sidFilter.getText();
    } else {
      recentArray[0][2] = null;
    }
  }

  private void recentLblSetting(String[][] recentArray) {
    for (int i = 0; i < recentButtons.length; i++) {
      //checks if the current array item is populated
      if (recentArray[i][0] != null) {
        //sets the select button to be enabled and visible
        recentButtons[i].setEnabled(true);
        recentButtons[i].setVisible(true);
        //checks to see if the filter is populated
        if (recentArray[i][2] == null) {
          recentLabels[i].setText("From: " + recentArray[i][0].substring(recentArray[i][0].lastIndexOf('\\') + 1)
                  + "        To: " + recentArray[i][1].substring(recentArray[i][1].lastIndexOf('\\') + 1));
        } else {
          recentLabels[i].setText("From: " + recentArray[i][0].substring(recentArray[i][0].lastIndexOf('\\') + 1)
                  + "        To: " + recentArray[i][1].substring(recentArray[i][1].lastIndexOf('\\') + 1)
                  + "        Filter: " + recentArray[i][2]);
        }
      }
    }
  }
}