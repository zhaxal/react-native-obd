package com.reactnativeobd.models;

public class ObdData {
  String id;
  String name;
  String result;

  public ObdData(String id, String name, String result){
    this.id = id;
    this.name = name;
    this.result = result;
  }
}
