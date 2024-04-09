package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Fahrzeugnummern {
    static List<String> trainTypes = Arrays.asList("Elektrolokomotive", "Dampflokomotive", "Normalspurlokomotive", "Schmalspurlokomotive", "Verbrennungsmotorlokomotive", "Dampftriebwagen", "Verbrennungsmotortriebwagen", "Elektrotriebwagen");
    public static String locomotiveName;
    public static String engineType;
    public static String trainType;
    public static void main(String[] args) {
        vehicleInput();
        String vehicleNumber = getVehicleNumber();
        if (vehicleNumber.length()!=10){
            System.out.println("Fahrzeugnummer nicht generierbar.");
        }
        else System.out.println("Die generierte Fahrzeugnummer lautet: " + vehicleNumber);
    }

    private static void vehicleInput(){
        System.out.println("Wie ist die genaue Bezeichnung der Bahn?");
        Scanner sc = new Scanner(System.in).useDelimiter("\\n");
        String inputName = getValidInput(sc);
        String confirmedName = getConfirmation(sc, inputName);

        locomotiveName = confirmedName.toLowerCase();

        extractTrainType(sc);
        extractEngineType(sc);
    }

    private static String getValidInput(Scanner sc) {
        String input;
        do {
            input = sc.nextLine();
            if (input.isEmpty()) {
                System.out.println("Keine Eingabe erkannt, bitte erneut eingeben");
            }
            if (!input.isEmpty() && input.length() < 6){
                System.out.println("Zu kurze Eingabe, bitte erneut eingeben");
            }
        } while (input.length() < 6);
        return nameCorrecting(sc, input);
    }

    private static String nameCorrecting (Scanner sc, String inputName){
        //TODO add more testcases
        String correctedName = inputName.replaceAll("[^a-zA-Z]+","");
        for (var type : trainTypes){
            if (type.equalsIgnoreCase(correctedName)) {
                engineType = type.substring(0,4);
                System.out.println("Bitte die Art der "+ correctedName + " eingeben. (z.B. Schnellzuglokomotive)" );
                String name = sc.next();
                correctedName = nameCorrecting(sc, name);
            }
        }
        return correctedName;
    }

    private static String getConfirmation(Scanner sc, String inputName){
        String confirmation;
        String input = "";
        do {
            System.out.println("Ist die Eingabe \"" + inputName + "\" korrekt? (ja/nein)");
            confirmation = sc.nextLine();
            if (!confirmation.contains("j")) {
                System.out.println("Bitte geben Sie die Bezeichnung der Bahn erneut ein");
                inputName = getValidInput(sc);
            }
            else input = inputName;
        } while (!confirmation.contains("j"));
        return input;
    }

    private static void extractEngineType(Scanner sc) {
        if (engineType == null) {
            System.out.println("Um was für einen Antrieb handelt es sich (Dampf, Elektro, Verbrennungsmotor)?");
            engineType = sc.next();
        }
    }

    private static void extractTrainType(Scanner sc) {
        if (!locomotiveName.contains("lokomotive") && !locomotiveName.contains("triebwagen")){
            System.out.println("Um was für eine Art handelt es sich: Lokomotive oder Triebwagen?");
            trainType = sc.next();
        } else if (locomotiveName.contains("loko")) {
            trainType = "lokomotive";
        } else if (locomotiveName.contains("trieb")) {
            trainType = "triebwagen";
        }
    }

    public static String getVehicleNumber(){
        String seriesNumber = String.valueOf(getSeriesNumber());
        String serialNumber = getSerialNumber();
        int checkDigit = getCheckDigit(seriesNumber,serialNumber);

        //TODO: Vehicle number for "Triebfahrzeuge" with 12 digits (type, country code included)

        return(seriesNumber + " " + serialNumber + "-" + checkDigit);
    }

    private static StringBuilder getSeriesNumber(){
        StringBuilder seriesNumber = new StringBuilder();
        //Discription digit of the engine type (Dampf,Elektro,Verbrennungsmotor) or wagon type
        int grand = getGrand();
        int specialCases = specialCases(grand);
        if (!(specialCases==0)) {
            if (specialCases==999){
                seriesNumber.append(0).append(specialCases);
            }
            return seriesNumber.append(specialCases);
        }

        //Discription digit of country and construction differences
        int hundred = getHundred(grand);

        //Discription digits of the train type
        int trainType = getSeries();
        if (trainType == 0){
            return new StringBuilder();
        }
        if (trainType > 0 && trainType < 10) {
            return seriesNumber.append(grand).append(hundred).append(0).append(trainType);
        }

        seriesNumber.append(grand).append(hundred).append(trainType);
        return seriesNumber;
    }

    private static int specialCases(int grand){
        //"Dampftriebwagen" only has two types, one of them was only build once and isn't used anymore
        if (grand == 3){
            return 3071;
        }

        // 999 is reserved for "Dampf-Schmalspur-Zahnradlokomotiven"
        if (locomotiveName.contains("schmalspur")&&locomotiveName.contains("zahnrad")){
            return 999;
        }

        // 1046 is reserved for "Elektrische Gepäcklokomotive" for urban traffic
        if (locomotiveName.contains("gepäck")){
            return 1046;
        }
        return 0;
    }

    private static int getGrand (){
        engineType = engineType.toLowerCase();
        // "dampf" equals 0
        int grand = 0;
        // "elektro" equals 1
        if (engineType.startsWith("e")){
            grand = 1;
        }
        // "verbrennungsmotor" equals 2
        if (engineType.startsWith("v")){
            grand = 2;
        }
        // "triebwagen" have the same values +3 resulting in 3 (dampf), 4 (elektro) or 5 (verbrennungsmotor)
        if (trainType.contains("trieb")){
            grand += 3;
        }
        // "Steuerwagen" equals 6
        if (locomotiveName.contains("steuerwagen")){
            grand = 6;
        }
        // "Zwischenwagen" equals 6
        if (locomotiveName.contains("zwischenwagen")){
            grand = 7;
        }
        return grand;
    }

    private static int getHundred(int grand){
        if (grand == 0){
            System.out.println("Stammt die Bauart der Bahn aus Österreich, Deutschland oder Sonstige?");
            Scanner sc = new Scanner(System.in).useDelimiter("\\n");
            String country = sc.next();
            if (country.charAt(0) == 'ö' || country.charAt(0) == 'o') {
                return randomNumber(1,5);
            }
            if (country.charAt(0) == 'd') {
                return randomNumber(6,3);
            }
            else return 9;
        }
        //TODO add more cases for special features
        return 0;
    }

    private static int getSeries (){
        int series = 0;
        if (locomotiveName.contains("schnellzug") || locomotiveName.contains("schnelltrieb")){
            series = randomNumber(1,20);
            if (locomotiveName.contains("tender")){
                series = randomNumber(60,20);
            }
        }
        if (locomotiveName.contains("güter") && locomotiveName.contains("elektro") ||  locomotiveName.contains("personen") || locomotiveName.contains("schnellbahntrieb")){
            series = randomNumber(20,20);
            if (locomotiveName.contains("tender")){
                series = randomNumber(60,20);
            }
        }
        if (locomotiveName.contains("güter") && locomotiveName.contains("dampf") || locomotiveName.contains("universal") || locomotiveName.contains("altbau")){
            series = randomNumber(40,20);
            if (locomotiveName.contains("tender")){
                series = randomNumber(80,20);
            }
        }
        if (locomotiveName.contains("verschub")){
            series = randomNumber(60,10);
        }

        if (locomotiveName.contains("schmalspur")){
            series = randomNumber(90,10);
            if (engineType.contains("dampf")){
                series = randomNumber(98,2);
            }
        }
        if (locomotiveName.contains("zahnradlokomotive")){
            series = 97;
        }
        if (locomotiveName.contains("strecken")){
            System.out.println("Handelt es sich um eine große Streckenlokomotive oder um eine Streckenlokomotive für gemischte Einsätze (groß oder universal)");
            Scanner sc = new Scanner(System.in).useDelimiter("\\n");
            String urbanStat = sc.next();
            if (urbanStat.contains("groß")){
                series = randomNumber(0,40);
            }
            else {
                series = randomNumber(40,20);
            }
        }
        return series;
    }

    private static int randomNumber (int start, int size){
        Random random = new Random();
        return random.nextInt(size) + start;
    }

    private static String getSerialNumber(){
        //TODO Add calculation of serial number here
        return String.valueOf(randomNumber(100,100));
    }

    private static Integer getCheckDigit (String seriesNumber, String serialNumber){
        String number = seriesNumber+serialNumber;
        int digitSum = 0;
        for (int i = 0; i < number.length(); i++){
            int value = Integer.parseInt(number.substring(i,i+1));
            if (i % 2 == 0){
                value *= 2;
            }
            digitSum += value;
        }
        return digitSum % 10 == 0 ? 0 : 10 - (digitSum % 10);
    }
}