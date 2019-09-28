package jiracli;

import static java.nio.file.Paths.get;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

public enum XDG {

  Cache("XDG_CACHE_HOME", () -> get(System.getenv("HOME"), ".cache", "jiracli")),
  Config("XDG_CONFIG_HOME", () -> get(System.getenv("HOME"), ".config", "jiracli")),
  ConfigDirs("XDG_CONFIG_DIRS", () -> get(File.separator, "etc", "xdg")),
  Data("XDG_DATA_HOME", () -> get(System.getenv("HOME"), ".local", "share", "jiracli")),
  RuntimeDir("XDG_RUNTIME_DIR", () -> get(System.getenv("XDG_RUNTIME_DIR")));

  public final String name;
  public final String path;

  XDG(String name, Supplier<Path> path) {
    this.name = name;
    String envPath = System.getenv(name);
    if (envPath != null && !envPath.isEmpty()) {
      this.path = envPath;
    } else {
      this.path = path.get().toString();
    }
  }

  Path toPath() {
    return get(path);
  }
}
