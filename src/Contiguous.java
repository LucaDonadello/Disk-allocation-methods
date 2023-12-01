//Luca Donadello

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * The Contiguous class represents a contiguous file allocation strategy in a disk drive.
 * It extends the DiskDrive class and implements the FileOperation interface.
 * It provides methods for copying files, deleting files, and displaying file contents.
 */
public class Contiguous extends DiskDrive implements FileOperation {

  private DiskDrive disk;
  private File file;
  private Scanner input = new Scanner(System.in);
  private int length = 0;
  private int indexTable = 0;
  private static int counterTable = 0;

  /**
   * Represents a contiguous allocation strategy for storing files in a disk drive.
   *
   * @param disk The disk drive where the files are stored.
   * @param mode The mode of operation for the contiguous allocation strategy.
   */
  public Contiguous(DiskDrive disk, String mode) {
    this.disk = disk;
  }

  /**
   * Copies a file to the disk.
   *
   * @param fileName the name of the file to be copied
   */
  @Override
  public void copyFileReal(String fileName) {
    this.file = new File(fileName);
    byte[] fileContent = new byte[BLOCK_SIZE * 10]; //10 blocks of 512 bytes maximum
    int temp = diskIndex;
    String tableData = "";
    String fileNameCopy = "."; //placeholder to trigger loop
    int counter = 1;

    try {
      long numBytes = Files.size(Paths.get(file.getAbsolutePath()));

      if (file.length() > BLOCK_SIZE * 10) {
        System.out.println("File is too big.");
        return;
      }

      //check if file is too big to enter the disk
      int counterFile = 0;
      for (int i = 0; i < NUM_BLOCKS; i++) {
        if (disk.disk[1][i] == 0) {
          counterFile++;
        }
      }
      if (counterFile * BLOCK_SIZE < numBytes) {
        System.out.println(
          "File is too big it does not fit the storage space."
        );
        return;
      }

      //check fist available position on bitmap
      diskIndex = 0;
      while (disk.disk[1][diskIndex] == 1) {
        diskIndex++;
      }
      temp = diskIndex;

      //copy file to disk
      fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
      for (int i = 0, j = 0; i < fileContent.length; i++, j++) {
        if (i % (BLOCK_SIZE - 1) == 0 && i != 0) {
          counter++;
          //update bitmap
          disk.disk[1][diskIndex] = 1;
          diskIndex++;
          j = 0;
        }
        disk.disk[diskIndex][j] = fileContent[i];
      }
      //update bitmap
      disk.disk[1][diskIndex] = 1;
      diskIndex++;

      //update table

      while (fileNameCopy.contains(".")) {
        System.out.print("Copy to: ");
        fileNameCopy = input.next();
        if (fileNameCopy.length() > 8) {
          System.out.println("Error: file name cannot be longer than 8 characters");
          fileNameCopy = ".";
        } else if (!fileNameCopy.matches("[a-z]+")) {
          System.out.println("Error: file name can contain only lower case letters");
          fileNameCopy = ".";
        } else {
          // Check if file name is unique
          String s = new String(disk.disk[0], StandardCharsets.UTF_8);
          String[] table = s.split("\n");
          for (int i = 0; i < table.length; i++) {
            if (table[i].contains(" ")) {
              String test = table[i].substring(0, table[i].indexOf(" "));
              test = test.replace("\0", ""); //get rid of the ending character
              if (test.equals(fileNameCopy)) {
                System.out.println("File name already exists.");
                fileNameCopy = ".";
                break;
              }
            }
          }
        }
      }
      //update table
      tableData = fileNameCopy + " " + temp + " " + counter + "\n";
      byte tableDataContent[] = tableData.getBytes();
      for (int i = 0; i < tableDataContent.length; i++, counterTable++) {
        disk.disk[0][counterTable] = tableDataContent[i];
      }
      //shift left when "/0" encountered
      for (int i = 0; i < disk.disk[0].length; i++) {
        if (disk.disk[0][i] == 0) {
          for (int j = i; j < disk.disk[0].length - 1; j++) {
            disk.disk[0][j] = disk.disk[0][j + 1];
          }
          disk.disk[0][disk.disk[0].length - 1] = 0;
        }
      }

      System.out.println("File " + fileName + " copied.");
    } catch (IOException e) {
      System.out.println("File not found.");
    }
  }

  /**
   * Copies a file from the disk to the real system.
   *
   * @param fileName the name of the file to be copied
   */
  @Override
  public void copyFileSim(String fileName) {
    //search name in table
    String s = new String(disk.disk[0], StandardCharsets.UTF_8);
    String[] table = s.split("\n");
    boolean found = false;

    //check if name is in table
    for (int i = 0; i < table.length; i++) {
      if (table[i].contains(fileName)) {
        String[] temp = table[i].split(" ");
        indexTable = Integer.parseInt(temp[1]);
        length = Integer.parseInt(temp[2]);
        found = true;
        break;
      }
    }
    if (found == false) {
      System.out.println("File not found.");
    } else {
      while (!fileName.contains(".txt")) {
        System.out.print("Copy to: ");
        fileName = input.next();
        if (!fileName.contains(".txt")) {
          System.out.println("Error: file name has to contain .txt extension");
        }
      }
      //copy file from disk to real system
      try {
        //copy and append disktable content to string
        FileOutputStream fos = new FileOutputStream(fileName);
        Writer out = new OutputStreamWriter(fos);
        for (int i = 0; i < length; i++) {
          String stringCopy = new String(
            disk.disk[indexTable],
            StandardCharsets.UTF_8
          );
          stringCopy = stringCopy.replace("\0", ""); //get rid of the ending character
          out.write(stringCopy);
          indexTable++;
        }
        out.close();
        System.out.println("File " + fileName + " copied.");
      } catch (FileNotFoundException e) {
        System.out.println("File not found.");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("An error occurred while reading the file.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Deletes a file from the disk and updates the disk table accordingly.
   * If the file is found, it removes the file entry from the table and clears the corresponding blocks on the disk.
   * If the file is not found, it prints a message indicating that the file was not found.
   *
   * @param fileName the name of the file to be deleted
   */
  @Override
  public void deleteFile(String fileName) {
    indexTable = 0;
    int lengthDelete = 0;
    //search in table if file is existing
    String s = new String(disk.disk[0], StandardCharsets.UTF_8);
    String[] table = s.split("\n");
    boolean found = false;

    for (int i = 0; i < table.length; i++) {
      if (table[i].contains(" ")) {
        String test = table[i].substring(0, table[i].indexOf(" "));
        test = test.replace("\0", ""); //get rid of the ending character
        if (test.equals(fileName)) {
          String[] temp = table[i].split(" ");
          indexTable = Integer.parseInt(temp[1]);
          lengthDelete = Integer.parseInt(temp[2]);
          table[i] = ""; //remove line from table
          found = true;
          break;
        }
      }
    }

    if (found == true) {
      //update disk table using the table array
      String temp1 = "";
      for (int i = 0; i < table.length; i++) {
        if (table[i] != "") {
          if(table[i].contains(" "))
            temp1 += table[i] + "\n";
        }
      }

      byte[] tableDataContent = temp1.getBytes();
      //delete all content of the table
      Arrays.fill(disk.disk[0], (byte) 0);
      for (int i = 0; i < tableDataContent.length; i++) {
        disk.disk[0][i] = tableDataContent[i];
      }

      //delete file from disk
      for (int i = 0; i < lengthDelete; i++) {
        Arrays.fill(disk.disk[indexTable], (byte) 0);
        disk.disk[1][indexTable] = 0; //clear bitmap
        indexTable++;
      }

      System.out.println("File " + fileName + " deleted.");
    } else System.out.println("File not found.");
  }

  /**
   * Displays the contents of a file with the given file name.
   *
   * @param fileName the name of the file to display
   */
  @Override
  public void displayFile(String fileName) {
    //search name in table
    String s = new String(disk.disk[0], StandardCharsets.UTF_8);
    String[] table = s.split("\n");
    boolean found = false;
    //assuming that the file is in the disk and file name is unique
    for (int i = 0; i < table.length; i++) {
      if (table[i].contains(" ")) {
        String test = table[i].substring(0, table[i].indexOf(" "));
        test = test.replace("\0", ""); //get rid of the ending character
        if (test.equals(fileName)) {
          String[] temp = table[i].split(" ");
          indexTable = Integer.parseInt(temp[1]);
          length = Integer.parseInt(temp[2]);
          found = true;
          break;
        }
      }
    }
    if (found == true) {
      for (int i = 0; i < length; i++) {
        String stringCopy = new String(disk.disk[indexTable],StandardCharsets.UTF_8);
        stringCopy = stringCopy.replace("\0", ""); //get rid of the ending character
        System.out.print(stringCopy);
        indexTable++;
      }
      System.out.println();
    } else System.out.println("File not found.");
  }
}
