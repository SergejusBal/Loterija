package org.example;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Custom {

    private static String ERRORZINUTE = "Įvyko klaida. Bandykite ivesti dar kartą.";

    //////////////////////////////////////////////////////////////
    ////////// Simple Scanner with ERROR message from Console
    public static boolean TaipNeBooleanCon() {
        while(true) {
            String scVerte = nuskaitytiCon();
            if(scVerte.equalsIgnoreCase("T") || scVerte.equalsIgnoreCase("TAIP")) return true;
            else if (scVerte.equalsIgnoreCase("N") || scVerte.equalsIgnoreCase("NE")) return false;
            System.out.println(ERRORZINUTE);
        }
    }

    public static String nuskaitytiStringVerteCon(){
        return nuskaitytiCon();
    }

    public static boolean nuskaitytiBooleanVerteCon(){
        while(true) {
            String scVerte = nuskaitytiCon();
            if(scVerte.equalsIgnoreCase("TRUE")) return true;
            else if (scVerte.equalsIgnoreCase("FALSE")) return false;
            System.out.println(ERRORZINUTE);
        }
    }

    public static long nuskaitytiLongVerteCon(){
        long i;
        try {
            i = Long.parseLong(nuskaitytiCon());
        } catch (NumberFormatException e) {
            System.out.println(ERRORZINUTE);
            return nuskaitytiLongVerteCon();
        }
        return i;
    }

    public static double nuskaitytiDoubleVerteCon(){
        double i;
        try {
            i = Double.parseDouble(nuskaitytiCon());
        } catch (NumberFormatException e) {
            System.out.println(ERRORZINUTE);
            return nuskaitytiDoubleVerteCon();
        }
        return i;
    }

    public static int nuskaitytiIntVerteCon(){
        int i;
        try {
            i = Integer.parseInt(nuskaitytiCon());
        } catch (NumberFormatException e) {
            System.out.println(ERRORZINUTE);
            return nuskaitytiIntVerteCon();
        }
        return i;
    }

    private static String nuskaitytiCon(){
        String verte = "";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            verte = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return verte;
    }


    //////////////////////////////////////////////////////////////
    ////////// Simple file manager methods (writer, reader)
    public static void redaguotiEilute(String naujaEilute, int index, String path){
        List<String> nuskaitytosEilutes = nuskaitytiFaila(path);
        nuskaitytosEilutes.set(index,naujaEilute);
        istrintiFaila(path);
        if(nuskaitytosEilutes.isEmpty()) sukurtiFaila(path);
        for(String s : nuskaitytosEilutes) irasytiEiluteIFaila(s, path);

    }

    public static void pasalintiEilute(int index, String path){
        List<String> nuskaitytosEilutes = nuskaitytiFaila(path);
        nuskaitytosEilutes.remove(index);
        istrintiFaila(path);
        if(nuskaitytosEilutes.isEmpty()) sukurtiFaila(path);
        for(String s : nuskaitytosEilutes) irasytiEiluteIFaila(s, path);

    }

    public static void kopijotiFaila(String pathoriginal, String pathCopy){
        List<String> nuskaitytosEilutes = nuskaitytiFaila(pathoriginal);
        for(String s : nuskaitytosEilutes) irasytiEiluteIFaila(s, pathCopy);
    }

    public static void irasytiEiluteIFaila(String line, String path){
        try {
            FileWriter fileWriter = new FileWriter(path, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(line);
            bufferedWriter.newLine();

            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            System.err.println("Nepavyko įrašyti į failą! Klaida: " + e.getMessage());
        }
    }

    public static List<String> nuskaitytiFaila(String path){
        List<String> nuskaitytosEilutes = new ArrayList<>();
        String line;

        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null){
                nuskaitytosEilutes.add(line);
            }
            fileReader.close();
            bufferedReader.close();

        }catch (IOException e) {
            System.err.println("Nepavyko nuskaityti failą! Klaida: " + e.getMessage());
        }
        return nuskaitytosEilutes;
    }

    public static void sukurtiFaila(String path){
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public static void istrintiFaila(String path){
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}