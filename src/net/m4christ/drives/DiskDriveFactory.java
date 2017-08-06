package net.m4christ.drives;

import java.util.ArrayList;
import java.util.List;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.JacobObject;
import com.jacob.com.Variant;

public class DiskDriveFactory
{
  public DiskDriveFactory() {}
  
  public static void main(String[] args)
  {
    List<Drive> drives = getUSBDrives();
    for (Drive drive : drives) {
      System.out.println(drive.LD_VolumeName);
    }
  }
  
  public static List<Drive> getUSBDrives()
  {
    List<Drive> drives = getDrives();
    List<Drive> results = new ArrayList<Drive>();
    for (Drive drive : drives) {
      if (!drive.LD_DeviceID.toUpperCase().startsWith("C")) {
        if (drive.LD_DriveType == 2) {
          if (drive.DD_PNPDeviceID.startsWith("USBSTOR")) {
            results.add(drive);
          }
        }
      }
    }
    return results;
  }
  
  public static List<Drive> getDrives()
  {
    List<Drive> result = new ArrayList<Drive>();
    ActiveXComponent axWMI = new ActiveXComponent("winmgmts://");
    try
    {
      Variant wmiDiskDrive = axWMI.invoke("ExecQuery", new Variant[] { new Variant("Select * from Win32_DiskDrive") });
      EnumVariant dds = new EnumVariant(wmiDiskDrive.toDispatch());
      EnumVariant dps;
      for (; dds.hasMoreElements(); dps.hasMoreElements())
      {
        Dispatch dd = dds.nextElement().toDispatch();
        String dd_DeviceID = Dispatch.call(dd, "DeviceID").toString();
        
        Variant wmiDiskPartitions = axWMI.invoke("ExecQuery", new Variant[] { new Variant("ASSOCIATORS OF {Win32_DiskDrive.DeviceID='" + dd_DeviceID + "'} WHERE AssocClass = Win32_DiskDriveToDiskPartition") });
        dps = new EnumVariant(wmiDiskPartitions.toDispatch());
        Dispatch dp = dps.nextElement().toDispatch();
        String partitionDeviceID = Dispatch.call(dp, "DeviceID").toString();
        
        Variant wmiLogicalDisks = axWMI.invoke("ExecQuery", new Variant[] { new Variant("ASSOCIATORS OF {Win32_DiskPartition.DeviceID='" + partitionDeviceID + "'} WHERE AssocClass = Win32_LogicalDiskToPartition") });
        EnumVariant lds = new EnumVariant(wmiLogicalDisks.toDispatch());
        while (lds.hasMoreElements())
        {
          Dispatch ld = lds.nextElement().toDispatch();
          
          Drive drive = new Drive();
          

          drive.DD_DeviceID = Dispatch.call(dd, "DeviceID").toString();
          drive.DD_Model = Dispatch.call(dd, "Model").toString();
          drive.DD_Caption = Dispatch.call(dd, "Caption").toString();
          drive.DD_PNPDeviceID = Dispatch.call(dd, "PNPDeviceID").toString();
          drive.DD_Name = Dispatch.call(dd, "Name").toString();
          

          drive.DP_DeviceID = Dispatch.call(dp, "DeviceID").toString();
          

          drive.LD_VolumeName = Dispatch.call(ld, "VolumeName").toString();
          drive.LD_DeviceID = Dispatch.call(ld, "DeviceID").toString();
          drive.LD_FileSystem = Dispatch.call(ld, "FileSystem").toString();
          drive.LD_DriveType = Dispatch.call(ld, "DriveType").getInt();
          String size = Dispatch.call(ld, "Size").getString();
          drive.LD_Size = Long.parseLong((size != null)? size : "0");
          drive.LD_FreeSpace = Long.parseLong(Dispatch.call(ld, "FreeSpace").getString());
          
          result.add(drive);
        }
      }
      return result;
    }
    finally
    {
      closeQuietly(axWMI);
    }
  }
  
  private static void closeQuietly(JacobObject obj)
  {
    try
    {
      obj.safeRelease();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
