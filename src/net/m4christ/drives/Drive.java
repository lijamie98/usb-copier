package net.m4christ.drives;

import java.io.File;

public class Drive
{
  public static final int LD_DRIVE_TYPE_UNKNOWN = 0;
  public static final int LD_DRIVE_TYPE_NO_ROOT_DIR = 1;
  public static final int LD_DRIVE_TYPE_REMOVABLE_DISK = 2;
  public static final int LD_DRIVE_TYPE_LOCAL_DISK = 3;
  public static final int LD_DRIVE_TYPE_NETWORK_DRIVE = 4;
  public static final int LD_DRIVE_TYPE_COMPACT_DISK = 5;
  public static final int LD_DRIVE_TYPE_RAM_DISK = 6;
  public String DD_DeviceID;
  public String DD_Model;
  public String DD_Caption;
  public String DD_PNPDeviceID;
  public String DD_Name;
  public String DP_DeviceID;
  public String LD_VolumeName;
  public String LD_DeviceID;
  public String LD_FileSystem;
  public int LD_DriveType;
  public long LD_Size;
  public long LD_FreeSpace;
  
  public Drive() {}
  
  public File getRootPath()
  {
    return new File(this.LD_DeviceID + "/");
  }
}
