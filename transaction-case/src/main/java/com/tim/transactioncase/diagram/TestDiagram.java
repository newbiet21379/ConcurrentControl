package com.tim.transactioncase.diagram;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

public class TestDiagram {

    public static void main(String[] args) throws IOException {
        String source = "@startuml\n";
        source += "skinparam monochrome true\n";                       // Added for monochrome diagram
        source += "skinparam shadowing false\n";                      // Removing shadow for a cleaner look
        source += "[*] --> Start\n";
        source += "state \"Start\" as start #LightBlue\n";            // Colored for better looks
        source += "state \"Create JobFlow\" as createJobFlow #LightBlue\n";
        source += "state \"Update Job Status\" as updateJobStatus #LightBlue\n";
        source += "state \"Find Open Job\" as findOpenJob #LightBlue\n";
        source += "state \"End\" as end #LightBlue\n";
        source += "Start --> createJobFlow: action1\n";               // Defining actions
        source += "createJobFlow --> updateJobStatus: action2\n";
        source += "updateJobStatus --> findOpenJob: action3\n";
        source += "findOpenJob --> end: action4\n";
        source += "end --> [*]\n";
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);
        String desc = reader.generateImage(getOutputStream("transaction-case"));
        System.out.println(desc);
    }

    public static OutputStream getOutputStream(String name) throws IOException {
        return Files.newOutputStream(Paths.get(name + ".png"));
    }
}