# Application to find frequent flyers from list of Customers.

Start application by running from CLI.
```shell
mvn clean install -f pom.xml
java -jar ./target/airlines-*.jar com.example.airlines.AirlinesApplication 
```
### Load customer data as Input
```shell
curl -X POST http://localhost:8080/airline/load/customers -d "[{ \"customerName\": \"Aaron Wright\",\"miles\": \"100108\"},{\"customerName\": \"Om Sterling\", \"miles\": \"102518\"}]" -H "Content-Type: application/json; charset=utf-8"
```

### Load customer data randomly by service itself.

`noOfCustomers - integer between 1 - 100000`
```shell
curl -X POST http://localhost:8080/airline/load/customers/{noOfCustomers}
```

### Get Frequent flyers
```shell
curl -X GET http://localhost:8080/airline/frequentflyers
```
    Success Response: [{"customerName":"Aaron Godwin","miles":101011},{"customerName":"Om Hasan","miles":101640}]
    Error Response: {"errorMessage":"Customer data list is empty. Please run /flyers/load/{noOfCustomers}","code":4040}

### Delete customer data
```shell
curl -X DELETE http://localhost:8080/airline/customers/delete
```
