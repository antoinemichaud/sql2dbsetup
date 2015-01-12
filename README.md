#SQL2DbSetup
![SQL2DbSetup build status](https://travis-ci.org/antoinemichaud/sql2dbsetup.svg)

This project aims to make easier the transition from SQL scripts fixtures to DbSetup fixtures.

##How to use DbSetup
- Clone this repository or download the zip file
- Go into the root directory of the project
- Build the jar with :
```
mvn package
```
- In target you will find the executable jar `sql2dbsetup-0.1-SNAPSHOT.jar`.
- Move it to the location you want it and launch :
```
java -jar sql2dbsetup-0.1-SNAPSHOT.jar \<path_to_your_sql_file\>
```

The result will be then printed in your console.

##The future of this project
This project is still pretty simple and could be not enough to your needs. That's why you can help it in either creating issues or sending pull requests.