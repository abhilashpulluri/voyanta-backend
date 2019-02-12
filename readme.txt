
Voyanta Back-End Exercise

This exercise contains 2 parts to be done in 1 to 2 hours:

The class XMLFileUnpackager.java converts XML files to CSV files following a specific mapping and doing a simple SUM calculation.
The file submission/outputFile.csv shows you what is expected from converting submission/inputFile.xml

1) Analysis

The method convertAndSave(File csvFile) in XMLFileUnpackager.java contains 2 bugs and a performance issue. Editing this one method only, fix it so that the tests in
ExerciseApplicationTests.compareGeneratedFiles() are passing.

/!\ Do not modify any other code than this method!

2) Architecture

Write a simple Rest API in the same module that will expose this feature, taking an xml file as parameter and returning the converted CSV file.
