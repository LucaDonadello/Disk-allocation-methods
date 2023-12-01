//lxd210013 - Luca Donadello - CS 4348.001

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * The Indexed class represents a file system implementation using indexed allocation method.
 * It extends the DiskDrive class and implements the FileOperation interface.
 * It provides methods for copying files, deleting files, and displaying file contents.
 */
public class Indexed extends DiskDrive implements FileOperation {

  private DiskDrive disk;
  private Scanner input = new Scanner(System.in);
  private File file;
  private int length = 0;
  private int indexTable = 0;
  private int counter = 0;

  /**
   * Represents an indexed file in a disk drive.
   * 
   * @param disk the disk drive containing the file
   * @param mode the mode in which the file is accessed
   */
  public Indexed(DiskDrive disk, String mode) {
    this.disk = disk;
  }

  /**
   * Copies a file to the disk.
   * 
   * @param fileName the name of the file to be copied
   */
  @Override
  public void copyFileReal(String fileName) {
    Random random = new Random();
    this.file = new File(fileName);
    int indexTable = 0;
    String tableData = "";
    String fileNameCopy = "."; //placeholder to trigger loop

    byte[] fileContent = new byte[BLOCK_SIZE * 10]; //10 blocks of 512 bytes maximum

    try {
      // size of a file (in bytes)
      long numBytes = Files.size(Paths.get(file.getAbsolutePath()));
      if (file.length() > BLOCK_SIZE * 10) {
        System.out.println("File is too big.");
        return;
      }

      int counterB = 0;
      for (int i = 0; i < NUM_BLOCKS; i++) {
        if (disk.disk[1][i] == 0) {
          counterB++;
        }
      }
      if (counterB * BLOCK_SIZE < numBytes) {
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

      //copy file to disk
      fileContent = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

      //take random position in the disk to store the data
      int temp = random.nextInt(NUM_BLOCKS - 1);
      //check if bitmap is empty at given position
      while (disk.disk[1][temp] == 1) {
        temp = random.nextInt(NUM_BLOCKS - 1);
      }

      disk.disk[diskIndex][indexTable] = (byte) temp; //pointer to next block
      indexTable++;

      //update bitmap
      disk.disk[1][diskIndex] = 1;
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

      tableData = fileNameCopy + " " + diskIndex + "\n";

      byte tableDataContent[] = tableData.getBytes();
      for (int i = 0; i < tableDataContent.length; i++, counter++) {
        disk.disk[0][counter] = tableDataContent[i];
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

      for (int i = 0, j = 0; i < fileContent.length; i++, j++) {
        if (i % (BLOCK_SIZE - 1) == 0 && i != 0) {
          //update bitmap
          disk.disk[1][temp] = 1;
          temp = random.nextInt(NUM_BLOCKS - 1);
          //check if bitmap is empty at given position
          while (disk.disk[1][temp] == 1) {
            temp = random.nextInt(NUM_BLOCKS - 1);
          }
          j = 0;
          //update index table
          disk.disk[diskIndex][indexTable] = (byte) temp; //pointer to next block
          indexTable++;
        }
        disk.disk[temp][j] = fileContent[i];
      }
      //update bitmap
      disk.disk[1][temp] = 1;
      //update disk index based on first available position on bitmap
      while (disk.disk[1][diskIndex] == 1) {
        diskIndex++;
      }
      System.out.println("File " + fileName + " copied.");
    } catch (IOException e) {
      System.out.println("An error occurred while reading the file.");
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
    int length = 0;
    boolean found = false;
    //assuming that the file is in the disk and file name is unique
    for (int i = 0; i < table.length; i++) {
      if (table[i].contains(fileName)) {
        String[] temp = table[i].split(" ");
        indexTable = Integer.parseInt(temp[1]);
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

        //get the size of the array
        for (int i = 0; i < disk.disk[indexTable].length; i++) {
          if (disk.disk[indexTable][i] == 0) {
            length = i;
            break;
          }
        }

        for (int i = 0; i < length; i++) {
          //take the pointer and convert to int
          int pointer = Byte.toUnsignedInt(disk.disk[indexTable][i]);
          String stringCopy = new String(
            disk.disk[pointer],
            StandardCharsets.UTF_8
          );
          stringCopy = stringCopy.replace("\0", ""); //get rid of the ending character
          out.write(stringCopy);
        }
        out.close();
        System.out.println("File " + fileName + " copied.");
      } catch (FileNotFoundException e) {
        System.out.println("File not found.");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("File not found.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Deletes a file from the disk and updates the index table and disk table accordingly.
   * If the file is found and successfully deleted, a success message is printed.
   * If the file is not found, a message indicating that the file was not found is printed.
   *
   * @param fileName The name of the file to be deleted.
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

      byte tableDataContent[] = temp1.getBytes();
      Arrays.fill(disk.disk[0], (byte) 0);
      for (int i = 0; i < tableDataContent.length; i++) {
        disk.disk[0][i] = tableDataContent[i];
      }

      for (int i = 0; i < disk.disk[indexTable].length; i++) {
        if (disk.disk[indexTable][i] == 0) {
          lengthDelete = i;
          break;
        }
      }

      //delete file from disk
      for (int i = 0; i < lengthDelete; i++) {
        int temp = Byte.toUnsignedInt(disk.disk[indexTable][i]); //take the pointer
        Arrays.fill(disk.disk[temp], (byte) 0);
        disk.disk[1][temp] = 0; //clear bitmap
        disk.disk[1][indexTable] = 0; //clear index table
      }

      Arrays.fill(disk.disk[indexTable], (byte) 0);

      System.out.println("File " + fileName + " deleted.");
    } else System.out.println("File not found.");
  }

  /**
   * Displays the contents of a file with the given file name.
   * 
   * @param fileName the name of the file to be displayed
   */
  @Override
  public void displayFile(String fileName) {
    //search name in table
    String s = new String(disk.disk[0], StandardCharsets.UTF_8);
    String[] table = s.split("\n");
    //assuming that the file is in the disk and file name is unique
    boolean found = false;

    for (int i = 0; i < table.length; i++) {
      if (table[i].contains(" ")) {
        String test = table[i].substring(0, table[i].indexOf(" "));
        test = test.replace("\0", ""); //get rid of the ending character
        if (test.equals(fileName)) {
          String[] temp = table[i].split(" ");
          indexTable = Integer.parseInt(temp[1]);
          found = true;
          break;
        }
      }
    }
    if (found == true) {
      for (int i = 0; i < disk.disk[indexTable].length; i++) {
        if (disk.disk[indexTable][i] == 0) {
          length = i;
          break;
        }
      }
      for (int i = 0; i < length; i++) {
        //take the pointer and convert to int
        int pointer = Byte.toUnsignedInt(disk.disk[indexTable][i]);
        String stringCopy = new String(
          disk.disk[pointer],
          StandardCharsets.UTF_8
        );
        stringCopy = stringCopy.replace("\0", ""); //get rid of the ending character
        System.out.print(stringCopy);
      }
      System.out.println();
    } else {
      System.out.println("File not found.");
    }
  }
}
