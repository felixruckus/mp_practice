package com.company;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class StreamReadFile {

    public static void main(String[] args) {

        boolean running = true;
        Scanner command = new Scanner(System.in);

        do {
            System.out.println("Please enter command or type 'exit' to quit program: ");

            String commandLine = command.nextLine();
            String[] commandList = commandLine.split(" ");
            if(commandList.length == 1 && commandList[0].equalsIgnoreCase("exit")){
                running = false;
            }
            else if (commandList.length != 2) {
                System.out.println ("Commands format error!");
                System.out.println ("Please follow below command: ");
                System.out.println ("Sort-names filename");
            }
            else {
                switch(commandList[0]) {
                    case "sort-names":
                        if(Files.exists(Paths.get(commandList[1]))) {
                            String outputFileName = generateOutFileName(commandList[1]);

                            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                                 Stream<String> lines = Files.lines(Paths.get(commandList[1]))) {
                                lines.map(line -> line.replaceAll("\\s", "").split(",")) //split each line
                                        .sorted(Comparator.comparing( (String[] e)-> e[0]) //sort by last name and then by first name
                                                .thenComparing( e -> e[1] ))
                                        .map( elements -> String.join(",", elements) ) //join back to a single string
                                        .forEach(line -> {
                                            try {
                                                writer.write(line);
                                                writer.newLine();
                                            } catch (IOException e) {
                                                System.out.println(e.getMessage());
                                            }
                                        });
                                System.out.println("Finished: created " + outputFileName);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
                        else {
                            System.out.println ("File not exist!");
                        }
                        break;
                    default:
                        System.out.println("Command not recognized!");
                        break;
                }
            }

        } while (running);
        command.close();
    }

    public static String generateOutFileName(String fileName) {
        Optional<String> extFileName = getExtensionByStringHandling(fileName);
        return extFileName.map(s -> fileName.substring(0, fileName.lastIndexOf('.')) + "-sorted." + s).orElseGet(() -> fileName + "-sorted");
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
