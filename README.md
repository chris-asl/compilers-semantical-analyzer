    **********************************************************************
    ** National and Kapodistrian University of Athens                   **  
    ** Department of Informatics and Telecommunications                 **
    ** Compilers - Spring Semester 2014                                 **
    ** Semester Project: MiniJava (subset of Java) compiler             **
    ** Part 1 out of 4: Semantic Check                                  **
    **********************************************************************
### Semantical Analyzer for miniJava, a subset of Java
_Project assignment with more information on MiniJava can be found [here](http://cgi.di.uoa.gr/~thp06/13_14/project.html#Homework_2_-_Semantic_Analysis_)._

This is the first out of four parts of the semester project for the Compilers course.  
Input is MiniJava source files which are semantically checked, throughout three phases,   
namely _class names collection_, _class{Members, Methods} information collection_,   
and finally _Type checking phase_ (described elaborately in the javadoc of `Driver` class).

We were provided with the MiniJava grammar in JavaCC form and used JavaCC and JTB tools to generate   
parser code, abstract syntax trees and visitors.   
For traversing the AST (which represents a MiniJava program), the Visitor pattern is employed.

##### A. Build & package to jar
Run `mvn package`.  
File `minijava-minijava-semantical-analyzer-VERSION.jar` will be created under `target` directory.  

##### B. Run
The semantical analyzer expects Java source files as input (any number of Java files).  
These, will be semantically checked one by one.  

###### B.1 Usage example   
`java [MainClassName] [file1] [file2] ... [fileN]`

###### B.2 How to run  
`java -jar /path/to/jar/file/minijava-semantical-analyzer-1.0.jar [javaSrcFile1] .. [javaSrcFileN]` 

###### B.3 Tests  
We were provided with some test files (located under `src/test/resources/minijava-test-files`)  
and created some of our own during development (the latter can be found under `src/test/resources/minijava-dev-tests`).

To run all tests (from both directories), simply run `tests.sh`.