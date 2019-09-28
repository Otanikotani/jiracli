package jiracli;

import com.mashape.unirest.http.Unirest;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0",
    description = "Prints the checksum (MD5 by default) of a file to STDOUT.")
class CheckSum implements Callable<Integer> {

  @Parameters(index = "0", description = "The file whose checksum to calculate.")
  private File file;

  @Option(names = {"-a", "--algorithm"}, description = "MD5, SHA-1, SHA-256, ...")
  private String algorithm = "MD5";

  // this example implements Callable, so parsing, error handling and handling user
  // requests for usage help or version help can be done with one line of code.
  public static void main(String... args) throws Exception {
    int exitCode = new CommandLine(new CheckSum()).execute(args);

    String response = Unirest.get("https://httpstat.us/200").asString().getStatusText();
    System.out.println(response);

    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception { // your business logic goes here...
    byte[] fileContents = Files.readAllBytes(file.toPath());
    byte[] digest = MessageDigest.getInstance(algorithm).digest(fileContents);
    System.out.printf("%0" + (digest.length * 2) + "x%n", new BigInteger(1, digest));
    return 0;
  }
}

@TargetClass(org.apache.commons.logging.LogFactory.class)
final class Replace_Appache_Log_Factory {

  @Substitute
  protected static Object createFactory(String factoryClass, ClassLoader classLoader) {
    return new LogFactoryImpl();
  }
}

@TargetClass(org.apache.commons.logging.LogFactory.class)
final class Replace_Apache_Log_Factory_getLog_method {

  @Substitute
  public static Log getLog(Class clazz) throws LogConfigurationException {
    return new SimpleLog(clazz.getName());
  }

  @Substitute
  public static Log getLog(String name) throws LogConfigurationException {
    return new SimpleLog(name);
  }
}