# payments-handler
RESTful Microservice for Store/Browse payments in CSV files (with possiblity to change to other repo)

Overview:

Payment is described by: unique identifier created during persistence process, amount, currency, user ID and target bank account number


To get the list of all payments:
```GET /api/payments```


To get specific payment:
```GET /api/payments/{id}```
Response:
```HTTP 200
{
    "id": {id},
    "user": -138929,
    "bankAccount": "IBAN-HAHA",
    "currency": "USD",
    "amount": 5966.34,
    "_links": {
        "self": {
            "href": "http://localhost:8090/api/payments/999"
        },
        "UPDATE": {
            "href": "http://localhost:8090/api/payments/999"
        },
        "DELETE": {
            "href": "http://localhost:8090/api/payments/999"
        }
    }
}
```

When there is no payment with given ID, the response is:
```HTTP 404
{
    "message": "ResourceCannotBeFoundException",
    "errors": [
        "Payment with id: 9999999999999999 cannot be found"
    ]
}
```

Save a new payment:
```HTTP POST /api/payments/
{
    "user": 1329,
    "bankAccount": "IBAN-HAHA",
    "currency": "USD",
    "amount": 5966.34
}
```

Update existing payment:
```HTTP PUT /api/payments/
{
    "id": 132,
    "user": 1329,
    "bankAccount": "IBAN-HAHA",
    "currency": "USD",
    "amount": 5966.34
}
```

And in order to delete existing payment:
```DELETE /api/payments/{id}```
The response is:
```HTTP 200
{
    "id": 132,
    "user": 1329,
    "bankAccount": "IBAN-HAHA",
    "currency": "USD",
    "amount": 5966.34
}
```

## How to build it?
Prerequisite: JDK 14
You need to run 
```mvnw clean install```
and 
```java -jar target/payments-handler-0.0.1-SNAPSHOT.jar ```
and open your browser [http://localhost:8090/api/payments/](http://localhost:8090/api/payments/)

## Details:
Repository is based on OpenCsv library, it is synchronized on file in order to avoid race condition as multiple threads writing to file. 
WARNING: at present the microservice is not safe for scaling by adding additional instances with shared database.

Project is implemented based on the Ports/Adapters architecture, with ArchUnit which guards the loose coupling between the domain layer and API layer.

There are several Acceptance Tests, Integration Tests and many UnitTest but they cover about half of functonality. 
The low coverage is caused by lack of time, but it can be done with an additional day of work.

To change to another storage you have to add a command parameter: ``` -Dspring.profiles.active=oracle ```.
That repo is not implemented yet but there is no problem to implement it since it is totally separated in another class.

## To Do:
Better coverage
Add the mechanism of CSV-file locking "externally", to prevent writing to it by another process
