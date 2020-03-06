# XML Json Converter App
A Spring Boot Application for converting between json and xml
- Apache Camel
  - camel-jackson
  - camel-jaxb
- Spring MVC

### Start the app:
```bash 
mvn spring-boot:run
``` 

### Test the app:

* test.json template
  ```json
  {
    "name": "hello",
    "description": "world",
    "dt_added": "2020-03-05T15:00:00-05:00"
  }
  ```

* test.xml
  ```xml
  ```

```bash
# test the sql
curl -XPOST -H "Content-Type: application/json" -d @test.json http://localhost:8080/sql2csv/

# result
NAME;DESCRIPTION
sql-to-csv;app to convert sql output into csv format

```

### References
* [test.json](test.json)
* [projects records](src/main/resources/data.sql)

