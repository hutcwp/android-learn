package com.ft.friend.model;

import com.lib.base.audio.model.CommonAudioBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @文件描述：朋友实体
 */
public class FriendBodyValue implements Serializable {

  public int type;
  public String avatr;
  public String name;
  public String fans;
  public String text;
  public ArrayList<String> pics;
  public String videoUrl;
  public String zan;
  public String msg;
  public CommonAudioBean audioBean;
}
