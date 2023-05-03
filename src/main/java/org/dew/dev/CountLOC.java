package org.dew.dev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public 
class CountLOC 
{
  public static int totalLOC = 0;
  
  public static int SX = 130;
  public static int DX = 10;

  public static 
  void main(String[] args) 
  {
    if(args == null || args.length == 0) {
      System.out.println("Usage: CountLOC file_or_folder [extension] [include] [exclude] [maxlength]");
      System.exit(1);
    }
    
    String fileOrFolder = args[0];
    if(fileOrFolder == null || fileOrFolder.length() == 0) {
      System.out.println("Invalid file_or_folder");
      System.exit(1);
    }
    
    String extension = args != null && args.length > 1 ? args[1] : null;
    String patternIn = args != null && args.length > 2 ? args[2] : null;
    String patternEx = args != null && args.length > 3 ? args[3] : null;
    String maxlength = args != null && args.length > 4 ? args[4] : null;
    if(maxlength != null && maxlength.length() > 0) {
      try {
        int iMaxLength = Integer.parseInt(maxlength);
        if(iMaxLength < 10) iMaxLength = 10;
      }
      catch(Exception ex) {
      }
    }
    
    start(new File(fileOrFolder), extension, patternIn, patternEx);
  }
  
  public static
  void start(File fileOrFolder)
  {
    start(fileOrFolder, null, null, null);
  }
  
  public static
  void start(File fileOrFolder, String extension)
  {
    start(fileOrFolder, extension, null, null);
  }
  
  public static
  void start(File fileOrFolder, String extension, String patternIn)
  {
    start(fileOrFolder, extension, patternIn, null);
  }
  
  public static
  void start(File fileOrFolder, String extension, String patternIn, String patternEx)
  {
    totalLOC = 0;
    
    countLOC(fileOrFolder, extension, patternIn, patternEx);
    
    String r1 = rpad("", '-', SX);
    String r2 = lpad("", '-', DX);
    System.out.println(r1 + r2);
    
    String s1 = rpad("Total Line of Code",     ' ', SX);
    String s2 = lpad(String.valueOf(totalLOC), ' ', DX);
    System.out.println(s1 + s2);
  }
  
  public static 
  boolean countLOC(File fileOrFolder, String extension, String patternIn, String patternEx)
  {
    try {
      if(fileOrFolder == null) return false;
      if(!fileOrFolder.exists()) {
        System.out.println(fileOrFolder.getAbsolutePath() + " NOT found");
        return false;
      }
      if(fileOrFolder.isFile()) {
        String sFileName = fileOrFolder.getName();
        if(sFileName.endsWith(".java")) {
          totalLOC += countLOCFile(fileOrFolder);
          return true;
        }
        return false;
      }
      if(!fileOrFolder.isDirectory()) {
        return false;
      }
      File[] files = fileOrFolder.listFiles();
      if(files == null || files.length == 0) return false;
      for(int i = 0; i < files.length; i++) {
        File file = files[i];
        if(file.isDirectory()) {
          countLOC(file, extension, patternIn, patternEx);
          continue;
        }
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        
        if(patternIn != null && patternIn.length() > 0) {
          if(patternIn.startsWith("_")) {
            if(!filePath.endsWith(patternIn.substring(1))) {
              continue;
            }
          }
          else if(patternIn.endsWith("_")) {
            if(!filePath.startsWith(patternIn.substring(0, patternIn.length()-1))) {
              continue;
            }
          }
          else {
            if(filePath.indexOf(patternIn) < 0) {
              continue;
            }
          }
        }
        if(patternEx != null && patternEx.length() > 0) {
          if(patternEx.startsWith("_")) {
            if(filePath.endsWith(patternEx.substring(1))) {
              continue;
            }
          }
          else if(patternEx.endsWith("_")) {
            if(filePath.startsWith(patternEx.substring(0, patternEx.length()-1))) {
              continue;
            }
          }
          else {
            if(filePath.indexOf(patternEx) > 0) {
              continue;
            }
          }
        }
        
        if(extension != null && extension.length() > 0) {
          if(fileName.endsWith("." + extension)) {
            totalLOC += countLOCFile(file);
          }
        }
        else {
          totalLOC += countLOCFile(file);
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }
  
  public static 
  int countLOCFile(File file) 
  {
    int result = 0;
    
    String line = null;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      while((line = br.readLine()) != null) {
        line = line.trim();
        
        if(line.length() == 0)              continue;
        if(line.startsWith("//"))           continue;
        if(line.startsWith("/*"))           continue;
        if(line.startsWith("*"))            continue;
        if(line.equals(";"))                continue;
        
//        if(line.equals("{"))                continue;
//        if(line.equals("}"))                continue;
//        if(line.startsWith("package "))     continue;
//        if(line.startsWith("import "))      continue;
//        if(line.equals("class"))            continue;
//        if(line.equals("interface"))        continue;
//        if(line.equals("enum"))             continue;
//        if(line.equals("implements"))       continue;
//        if(line.equals("public"))           continue;
//        if(line.equals("private"))          continue;
//        if(line.equals("protected"))        continue;
//        if(line.equals("public static"))    continue;
//        if(line.equals("private static"))   continue;
//        if(line.equals("protected static")) continue;
        
        result++;
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return result;
    }
    finally {
      if(br != null) try{ br.close(); } catch(Exception ex) {}
    }
    
    String s1 = rpad(file.getAbsolutePath(), ' ', SX);
    String s2 = lpad(String.valueOf(result), ' ', DX);
    System.out.println(s1 + s2);
    
    return result;
  }
  
  public static
  String rpad(String text, char c, int length)
  {
    if(text == null) text = "";
    int iTextLength = text.length();
    if(iTextLength >= length) return text;
    int diff = length - iTextLength;
    StringBuffer sb = new StringBuffer();
    sb.append(text);
    for(int i = 0; i < diff; i++) sb.append(c);
    return sb.toString();
  }
  
  public static
  String lpad(String text, char c, int length)
  {
    if(text == null) text = "";
    int iTextLength = text.length();
    if(iTextLength >= length) return text;
    int diff = length - iTextLength;
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < diff; i++) sb.append(c);
    sb.append(text);
    return sb.toString();
  }
}