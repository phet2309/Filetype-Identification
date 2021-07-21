# Filetype-Identification
It is a program which finds the file type and other details about file, by maintaining various data sources.

## Problem Statement
With the enormous number of languages and file types used for writing logical source or for data purposes, it
is very important for a product like BlueOptima to effectively identify and categorize a file into its type. And
this has to be done solely based on Extension and Name of the file itself.

1. Short Description (explaining the usage of the file type)
2. Category (i.e. Logical Source, Configuration, Data, etc.)
3. Language Family (Java, Python, Perl, etc.)
4. Programming Paradigm (Procedural, OOP, Dynamic, etc)
5. Associated applications

## Solution

 1. Deliverable 1 :white_check_mark:

    - Identify relevant data sources from where a filetype information (as described above) can be extracted
    - List at least 5 relevant sources and explain the rationale on why it should be used.
    
 2. Deliverable 2 :white_check_mark:

    - Implementation of a program to fetch the required information from at least 3 (ideally) of the identified sources.
    - fileproinfo.com used as data source, data collected using web scraping.
    - Apache Tika used for data source, data obtained from parsing XML file.
    - fileinfo.com used as data source, data collected using web scraping.
    - Solution is feasible for large volumes for filetypes.
    - Data collection from source is in the form of json, and whole output is in .csv file.
    - Multi-threading is used.

## Execution Flow
1. Data collection using Web Scraping from fileproinfo.com, fileinfo.com and data extraction using XML parsing with Apache Tika
2. Collected data gets stored inside respective .json file
3. The main file executes the input for fileinfo, filrproinfo and for Apache Tika, and each will recieve their outputs in respective csv output file.

## Input
Default input is input2.csv, but user can change it according to need. 

Example input:
```
.php
.html
.txt
.htm
.aspx
.asp
.js
```

## Output
Three output files will be generated, (1) FileInfoSourceOutput.csv, (2) FileProSourceOutput.csv, (3) TikaSourceOutput.csv
Example output (Snippets of .csv file):

TikaSourceOutput.csv
```
Extension Category  Format
.php  text/x-php	PHP script
.html	text/html	HyperText Markup Language
```
FileProSourceOutput.csv
```
Extension	Category	Format	Developer	Mime Type	Programming Language
.php	Common File Types	Text	Not Available	text/x-pascal	PHP
.html	Common File Types	Text	Not Available	text/html	HTML
```
FileInfoSourceOutput.csv
```
Extension	Description	Category	Format	Programming Language	Program Support
.php	A PHP file is a webpage that contains PHP (Hypertext Preprocessor) code. It may include PHP functions that can process online forms, get the date and time, or access information from a database, such as a MySQL database.	Web Files	Text	PHP	File Viewer Plus, Adobe Dreamweaver 2020, Eclipse PHP Development Tools, Zend Studio, MPSoftware phpDesigner, ES-Computing EditPlus, Blumentals WeBuilder, Notepad++, Other text editor, Apache Web Server with PHP, WampServer, Any Web browser, MacroMates TextMate, Bare Bones BBEdit, MAMP, Chocolat, Sublime Text, Alexander Blach Textastic Code Editor, Firas Moussa phpwin, File Viewer for Android
.html	An HTML file contains Hypertext Markup Language (HTML), which is used to format the structure of a webpage. It is stored in a standard text format and contains tags that define the page layout and content of the webpage, including the text, tables, images, and hyperlinks displayed on the webpage. HTML files are widely used online and displayed in web browsers.	Web Files	Text	HTML	File Viewer Plus, Any web browser, Microsoft Visual Studio Code, W3C Amaya, Adobe Dreamweaver 2020, Adobe ColdFusion Builder, Microsoft Visual Studio 2019, Blumentals WeBuilder, KompoZer, Notepad++, Any text editor, SCREEM, Fornace Espresso HTML, Alexander Blach Textastic Code Editor, File Viewer for Android, Google Chrome Text
```


## Steps to run the Program

## Developers
* Het Patel
* Jhanvi Gouru



