package com.aquino.texasandroid.model;

import java.util.List;

public class GameList {

   private List<GameInfo> list;
   private int size;


   public List<GameInfo> getList(){
       return list;
   }

    public void setList(List<GameInfo> list) {
        this.list = list;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


}
