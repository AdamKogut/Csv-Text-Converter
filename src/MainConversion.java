import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by C251331 on 7/14/2017.
 */

public class MainConversion {
  public static void main(String[] args) {
    //create scanner and call the constructor for this class
    Scanner k = new Scanner(System.in);
    MainConversion mc = new MainConversion();

    //gets the csv file from the user
    System.out.println("Program that transfers and formats .csv into .txt");
    System.out.print("Enter the csv file to format: ");
    String filename = k.nextLine();
    System.out.println(filename+":");

    //gets the text file from the user
    System.out.print("Enter the text file to write to: ");
    String textFileName = k.nextLine();

    //calls the needed method
    try {
      mc.csvFileToText(filename, textFileName,null);
    }catch (FileNotFoundException e){
      e.printStackTrace();
    }
  }

  public void csvFileToText(String  filename, String textFileName, String filterBox) throws FileNotFoundException {
    //makes file out of the csv filename
    File file = new File(filename);
    Vector<Vector> wholeFile = new Vector<>();
    Scanner inputFile = new Scanner(file);
    int maxChildNodes = 4;

    //scans the csv file and breaks it into a vector of a vector
    while (inputFile.hasNext()) {
      wholeFile.add(splitf(inputFile.nextLine(),","));
    }

    //gets the index that the childnodes end on
    while(true) {
      if(wholeFile.get(0).get(maxChildNodes).toString().substring(0,9).equalsIgnoreCase("ChildNode")){
        maxChildNodes++;
      }else {
        break;
      }
    }

    //check to see which lines are parents
    Boolean[] isParent = new Boolean[wholeFile.size()];

    for(int i = 1; i < wholeFile.size(); i++) {
      isParent[i] = true;
      for(int j = 1; j < wholeFile.size(); j++) {
        if(i!=j && wholeFile.get(j).contains(wholeFile.get(i).get(1))) {
          isParent[i] = false;
          break;
        }
      }
    }

    //puts the vector gained from the csv file in the correct order
    Vector<Vector> inOrder = new Vector<>();
    inOrder.add(wholeFile.get(0));

    while(inOrder.size()<wholeFile.size()) {
      int currentParent = 0;
      for (int i = 1; i < wholeFile.size(); i++) {
        if (isParent[i]) {
          currentParent = i;
          isParent[i] = false;
          break;
        }
      }

      //adds the parent
      inOrder.add(wholeFile.get(currentParent));
      //adds all the children of the previously added parent
      recurOrdering(wholeFile.get(currentParent), wholeFile, inOrder);
    }

    try {
      PrintWriter oFile = new PrintWriter(textFileName, "Cp1252");

      //writing SID
      if(filterBox==null){
        oFile.println(inOrder.get(1).get(0));
      }else {
        oFile.println(filterBox);
      }
      oFile.println();

      for (int i = 1; i < inOrder.size(); i++) {
        Vector<String> currLine = inOrder.get(i);

        if (filterBox == null || filterBox.equals(currLine.get(0))) {
          //writes parent node
          oFile.println(currLine.get(1));

          //writes preferred term
          if (!"".equals(currLine.get(2))) {
            oFile.println("\tPT " + currLine.get(2));
          }

          //writes synonyms
          Vector<String> synGroup = splitf(currLine.get(3), "::");
          for (int j = 0; j < synGroup.size(); j++) {
            if (!"".equals(synGroup.get(j))) {
              oFile.println("\tSYN " + synGroup.get(j));
            }
          }

          //writes child nodes
          for (int j = 4; j < maxChildNodes; j++) {
            if (!"".equals(currLine.get(j))) {
              oFile.println("\tNT " + currLine.get(j));
            }
          }
          oFile.println("");
        }
      }
      //closes text file
      oFile.close();
    } catch (IOException e) {
      System.out.println("ERROR OPENING FILE");
    }
    inputFile.close();
  }

  /**
   * splits the given string
   *
   * @param s given string to split
   * @param c string to split the string s on
   * @return a vector of strings
   */
  private static Vector<String> splitf(String s, String c) {
    int i = 0;
    int j = s.indexOf(c);
    Vector<String> a = new Vector<>();

    while(j!=-1) {
      a.add(s.substring(i,j));
      j+=c.length();
      i=j;
      j=s.indexOf(c,j);
      if(j==-1){
        a.add(s.substring(i));
      }
    }
    if(i==0){
      a.add(s);
    }
    return a;
  }

  /**
   * method to add all the children of a given parent recursively
   *
   * @param curr the current parent that must add all the children
   * @param wholeFile the vector of the whole csv file that has all of the children and parents
   * @param inOrder the vector that you add the parents and children to
   */
  private void recurOrdering(Vector<String> curr, Vector<Vector> wholeFile, Vector<Vector> inOrder) {
    for(int i = 4; i < curr.size(); i++) {
      for(int j = 1; j < wholeFile.size(); j++) {
        if(curr.get(i).equals(wholeFile.get(j).get(1))){
          inOrder.add(wholeFile.get(j));
          if(!wholeFile.get(j).get(4).equals("")){
            recurOrdering(wholeFile.get(j),wholeFile,inOrder);
          }
        }
      }
    }
  }
}
