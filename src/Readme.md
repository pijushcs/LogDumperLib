#### `LogDumperLib Library:`
The library provides a simple interface to the Client applications for registering to LogDumper and using the logger provided by the library. The LogDumperLib supports clients to add logs asynchronously and doesn't block the client application.

<b>Usage:</b>   
```Java
// Create LogDumper
LogDumper logDumper = LogDumperImpl.getDefaultLogger();

// Register Application
logDumper.registerApp("Test-Application");              

// Add logs
logDumper.info("Test Log");                        
logDumper.error("Error Log");
```

---

#### `Some Design Notes`:
> Clients have been provided a LogDumper Interface and can use the same for registering the application and adding logs.

> The logger instance is a singleton as only once such logger should be there for a particular cleint.

> While generating a log, the library calls the logging method in async mode that takes an exclusive lock on the client log file before appending the log.

---

#### `Some Class Specific Notes:`
> <b>LogDumper Interface:</b>  
The interface the client should use and defines the required methods to be implemented.

> <b>LogDumperImpl:</b>   
The default implementation fo the LogDumper that takes care of the backgrond workog logging.

> <b>Constants:</b>   
The class has a few Constants and Enums.

---
