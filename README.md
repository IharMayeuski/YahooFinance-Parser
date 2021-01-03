# yahooParser
This app is developed for downloading rates from YahooFinance (look description in below).
This app was made in two variant (one thread and multythread), you can find file of perfomance inside (83 secs vs 5secs on 100 urls).
Also you can find inside two jar files for comparing by yourself.
Steps:
1) Download file UK100.xlsx (there you will find yahoo instruments for searching in column C).
2) Download .jar file and .bat file in one directory.
3) Open .bat file and choose: your file UK100.xlsx and course on date what do you want.
4) Open generated file with name UK100-{your choosen date}.xlsx and find your coursed in column L.

Best regards
Igor.




Description:
The program will download several csv files and get a price number out of each CSV file.

- A file "country 3" is present (like Japan 3 or HK 3) (ATTACHED to the job specifications)
with security names in column B and Yahoo codes in column C.

- A settings file with a start date, an end date and a close date.
(file TO BE CREATED by the freelancer)

The program will:

1- create the url to historical prices

example UK

example for the first one in line 2

https://finance.yahoo.com/quote/HSBA.L/history?p=HSBA.L

with

https://finance.yahoo.com/quote/

+
HSBA.L (=C2 in UK 3)
+
/history?p=
+
HSBA.L (=C2 in UK 3)


to get the concatenated
https://finance.yahoo.com/quote/HSBA.L/history?p=HSBA.L


2- then the program will download the csv files as if it were working the following way (it will not actually use the browser):  it will insert the url newly created into the browser and run it.

3- it will insert the right start date and end date into the 2 ranges (in DD/MM/YYYY)
(the start date and the end date will be inserted by the user into a settings file before running the program)

4-the program will press "apply" then "download data"

5- it will download a file called "HSBA.L.csv".

6-it will open the CSV file.
it will consider the close date in the settings file.
(the close date will be inserted by the user into a settings file before running the program)

it will get the "close" number on the same date as the close date in the settings file and insert the close number into the file "UK 3", in the line where the CSV file name is the same as in column B, and it will insert the close number into column L, on the same line.

7-the program will stop when all lines are done in country 3 file.

The output file is named "HK 3-close_date.xlsx" for HK 3.xlsx, or "Japan 3-close_date.xlsx" for Japan 3.xlsx.
