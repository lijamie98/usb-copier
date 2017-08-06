package net.m4christ.usb.ui;

public abstract interface ProgressListener
{
  public abstract void changed(int paramInt);
  
  public abstract void completed();
}
