package org.dew.dev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public 
class CountLOC 
{
  public static int totalLOC = 0;
  
  public static int SX = 90;
  public static int DX = 10;
  
  public static boolean EXCLUDE_EMPTY_ROW = false;
  public static boolean EXCLUDE_COMMENTS  = false;
  
  public static String[] SOURCES = {
      "java","cs","c","h","cpp","pas","py","bas","vb","inc",
      "txt","xml","json","yaml","yml",
      "html","htm","jsp","tld","asp","aspx","js","css","ts",
      "cmd","bat","ps1","sh","sql",
      "md","cfg","conf","ini","properties","jrxml"
  };
  
  public static 
  void main(String[] args) 
  {
    if(args == null || args.length == 0) {
      System.out.println("Usage: CountLOC file_or_folder [extension] [include] [exclude] [lengthColFile]");
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
    String lengthCol = args != null && args.length > 4 ? args[4] : null;
    if(lengthCol != null && lengthCol.length() > 0) {
      try {
        int l = Integer.parseInt(lengthCol);
        if(l < 20) l = 20;
        SX = l;
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
      if(fileOrFolder == null) {
        return false;
      }
      String name = fileOrFolder.getName();
      if(name.length() > 1) {
        char c0 = name.charAt(0);
        char c1 = name.charAt(1);
        if(c0 == '.' && c1 != '/' && c1 != '\\') {
          // Hidden folders
          return false;
        }
      }
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
        
        if(fileName.length() > 1) {
          char c0 = fileName.charAt(0);
          char c1 = fileName.charAt(1);
          if(c0 == '.' && c1 != '/' && c1 != '\\') {
            // Hidden files
            continue;
          }
        }
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
          if(extension.equals("*")) {
            totalLOC += countLOCFile(file);
          }
          else if(fileName.endsWith("." + extension)) {
            totalLOC += countLOCFile(file);
          }
        }
        else {
          int sep = fileName.lastIndexOf('.');
          if(sep < 0 || sep == fileName.length() - 1) continue;
          
          String fileExtension = fileName.substring(sep + 1).toLowerCase();
          boolean found = false;
          for(int x = 0; x < SOURCES.length; x++) {
            if(SOURCES[x].equals(fileExtension)) {
              found = true;
              break;
            }
          }
          if(!found) continue;
          
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
        if(EXCLUDE_EMPTY_ROW || EXCLUDE_COMMENTS) {
          line = line.trim();
          
          if(EXCLUDE_EMPTY_ROW) {
            if(line.length() == 0)    continue;
          }
          if(EXCLUDE_COMMENTS) {
            if(line.startsWith("//")) continue;
            if(line.startsWith("/*")) continue;
            if(line.startsWith("*"))  continue;
          }
        }
        result++;
      }
    }
    catch(Exception ex) {
      System.err.println("Exception during read " + file.getAbsolutePath() + ": " + ex);
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