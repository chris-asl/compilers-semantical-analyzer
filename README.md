    **********************************************************************
    ** National and Kapodistrian University of Athens                   **  
    ** Department of Informatics and Telecommunications                 **
    ** Compilers - Spring Semester 2014                                 **
    ** Semester Project: MiniJava (subset of Java) compiler             **
    ** Part 1 out of 4: Semantic Check                                  **
    **********************************************************************
##### Semantical Analyzer for miniJava, a subset of Java
_Project assignment with more information on MiniJava can be found [here](http://cgi.di.uoa.gr/~thp06/13_14/project.html#Homework_2_-_Semantic_Analysis_)._

###### A. Build & package to jar
Run `mvn package`.  
File `minijava-minijava-semantical-analyzer-VERSION.jar` will be created under `target` directory.  

###### B. Run
The semantical analyzer expects Java source files as input (any number of Java files).  
These, will be semantically checked one by one.  

**B.1 Usage example**   
`java [MainClassName] [file1] [file2] ... [fileN]`.

**B.2 How to run**  
`java -jar /path/to/jar/file/minijava-semantical-analyzer-1.0.jar [javaSrcFile1] .. [javaSrcFileN]` 

**B.3 Test files**  
We were provided with some test files to test our code (which are located under `src/test/resources/minijava-test-files`) and created some of our own during development (the latter can be found under `src/test/resources/minijava-my-tests`).

