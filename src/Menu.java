//Luca Donadello

import java.util.*;

public class Menu extends DiskDrive {

  Scanner input = new Scanner(System.in);
  public int num = 0;
  String fileName = "";

  /**
   * Displays the menu options and handles user input to perform various operations on the file system.
   * 
   * @param fileSystem The file system object.
   * @param mode The mode of the file system.
   */
  public void display(FileSystem fileSystem, String mode) {
    while (true) {
      System.out.println(
        "\n1) Display a file\n" +
        "2) Display the file table\n" +
        "3) Display the free space bitmap\n" +
        "4) Display a disk block\n" +
        "5) Copy a file from the simulation to a file on the real system\n" +
        "6) Copy a file from the real system to a file in the simulation\n" +
        "7) Delete a file\n" +
        "8) Exit\n"
      );
      try {
        System.out.print("Choice: ");
        if (input.hasNextInt()) {
          this.num = input.nextInt();
          switch (num) {
            case 1:
              System.out.print("Enter file name: ");
              fileName = input.next();
              fileSystem.displayFile(fileName);
              break;
            case 2:
              fileSystem.showTable();
              break;
            case 3:
              fileSystem.showBitmap();
              break;
            case 4:
              System.out.print("Enter block number: ");
              int blockNumber = input.nextInt();
              fileSystem.showBlock(blockNumber);
              break;
            case 5:
              System.out.print("Copy from: ");
              fileName = input.next();
              fileSystem.copyFileSim(fileName);
              break;
            case 6:
              if (fileSystem.checkFull()) {
                System.out.println("Disk is full.");
                break;
              }
              System.out.print("Copy from: ");
              fileName = input.next();
              fileSystem.copyFileReal(fileName);
              break;
            case 7:
              System.out.print("Enter file name to delete: ");
              fileName = input.next();
              fileSystem.deleteFile(fileName);
              break;
            case 8:
              System.out.println("Exiting simulation...");
              System.exit(0);
              break;
            default:
              System.out.println("Invalid input try again.");
              break;
          }
        } else {
          System.out.println("Invalid input try again.");
          input.next(); // Clear the invalid input
        }
      } catch (InputMismatchException e) {
        System.out.println("Fatal Error: invalid input.");
        System.exit(0);
      }
    }
  }
}
