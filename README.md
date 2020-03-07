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
  <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
  <Project>
      <name>hello</name>
      <description>world</description>
      <dtAdded>2020-03-05T15:00:00-05:00</dtAdded>
  </Project>
  ```

```bash
# convert json to xml
curl -XPOST -F file=@test.json http://localhost:8080/xml

# convert xml to json
curl -XPOST -F file=@test.xml http://localhost:8080/json
```