package picocli.codegen.aot.graalvm;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Unmatched;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "example", version = "3.7.0",
        mixinStandardHelpOptions = true, subcommands = CommandLine.HelpCommand.class)
public class Example implements Runnable {
    @Option(names = "-t")
    TimeUnit timeUnit;

    @Parameters(index = "0")
    File file;

    @Spec
    CommandSpec spec;

    @Unmatched
    List<String> unmatched;

    private int minimum;
    private File[] otherFiles;

    @Command
    int multiply(@Option(names = "--count") int count,
                 @Parameters int multiplier) {
        System.out.println("Result is " + count * multiplier);
        return count * multiplier;
    }

    @Option(names = "--minimum")
    public void setMinimum(int min) {
        if (min < 0) {
            throw new ParameterException(spec.commandLine(), "Minimum must be a positive integer");
        }
        minimum = min;
    }

    @Parameters(index = "1..*")
    public void setOtherFiles(File[] otherFiles) {
        for (File f : otherFiles) {
            if (!f.exists()) {
                throw new ParameterException(spec.commandLine(), "File " + f.getAbsolutePath() + " must exist");
            }
        }
        this.otherFiles = otherFiles;
    }

    public void run() {
        System.out.printf("timeUnit=%s, file=%s, unmatched=%s, minimum=%s, otherFiles=%s%n",
                timeUnit, file, unmatched, minimum, Arrays.toString(otherFiles));
    }

    public static void main(String[] args) {
        CommandLine.run(new Example(), args);
    }
}
