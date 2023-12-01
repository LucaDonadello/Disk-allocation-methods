//Luca Donadello


/**
 * The DiskDrive class represents a disk drive with a fixed number of blocks and a fixed block size.
 * It provides methods to access and manipulate the disk blocks.
 */
public class DiskDrive {

  public static int diskIndex = 2;
  public static final int BLOCK_SIZE = 512;
  public static final int NUM_BLOCKS = 256;

  public byte[][] disk = new byte[NUM_BLOCKS][BLOCK_SIZE]; //array of blocks of size 512 bytes
  public byte[] bytes = new byte[NUM_BLOCKS];

  
  public DiskDrive() {
    //initialize disk table is empty and initialize bitmap
    disk[1][0] = 1; //first block is occupied by the table
    disk[1][1] = 1; //second block is occupied by the bitmap
    for (int i = 2; i < NUM_BLOCKS; i++) {
      disk[1][i] = 0;
    }
  }
}
