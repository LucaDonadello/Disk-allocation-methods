//Luca Donadello

import java.nio.charset.*;

/**
 * The FileSystem class represents a file system that extends DiskDrive and implements FileOperation.
 * It provides methods for displaying, copying, deleting files, as well as checking disk status.
 */
public class FileSystem extends DiskDrive implements FileOperation {

  public DiskDrive disk;
  public FileOperation fileOperation;

  /**
   * Constructs a FileSystem object with the specified DiskDrive and mode.
   * The mode determines the type of file operation to be performed.
   *
   * @param disk the DiskDrive object representing the disk
   * @param mode the mode of file operation (contiguous, chained, indexed)
   */
  public FileSystem(DiskDrive disk, String mode) {
    this.disk = disk;
    switch (mode) {
      case "contiguous":
        fileOperation = new Contiguous(disk, mode);
        break;
      case "chained":
        fileOperation = new Chained(disk, mode);
        break;
      case "indexed":
        fileOperation = new Indexed(disk, mode);
        break;
      default:
        break;
    }
  }

  /**
   * Displays the contents of the specified file.
   *
   * @param fileName the name of the file to be displayed
   */
  @Override
  public void displayFile(String fileName) {
    fileOperation.displayFile(fileName);
  }

  /**
   * Copies a file in a simulated manner.
   *
   * @param fileName the name of the file to be copied
   */
  @Override
  public void copyFileSim(String fileName) {
    fileOperation.copyFileSim(fileName);
  }

  /**
   * Copies a file with the given file name.
   *
   * @param fileName the name of the file to be copied
   */
  @Override
  public void copyFileReal(String fileName) {
    fileOperation.copyFileReal(fileName);
  }

  /**
   * Deletes a file with the specified file name.
   *
   * @param fileName the name of the file to be deleted
   */
  @Override
  public void deleteFile(String fileName) {
    fileOperation.deleteFile(fileName);
  }

  /**
   * Displays the specified block in string format.
   * If the block number is 1, it displays the bitmap.
   *
   * @param numBlock the block number to be displayed
   */
  public void showBlock(int numBlock) {
    if (numBlock == 1) {
      showBitmap();
    } else {
      String s = new String(disk.disk[numBlock], StandardCharsets.UTF_8);
      System.out.println(s);
    }
  }

  /**
   * Displays the bitmap of the disk.
   */
  public void showBitmap() {
    for (int i = 0; i < NUM_BLOCKS; i++) {
      if (i % 32 == 0 && i != 0) System.out.println();
      System.out.print(disk.disk[1][i]);
    }
    System.out.println();
  }

  /**
   * Displays the file table in string format.
   * If the file table is empty, it prints a message indicating that.
   */
  public void showTable() {
    String s = new String(disk.disk[0], StandardCharsets.UTF_8);
    s = s.replace("\0", "");
    if (s.isEmpty()) System.out.println(
      "File table is empty."
    ); else System.out.print(s);
  }

  /**
   * Checks if the disk is full.
   *
   * @return true if the disk is full, false otherwise
   */
  public boolean checkFull() {
    int counter = 0;
    for (int i = 0; i < NUM_BLOCKS; i++) {
      if (disk.disk[1][i] == 1) counter++;
    }
    if (counter == NUM_BLOCKS) return true; else return false;
  }
}
