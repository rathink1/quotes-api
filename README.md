# Quotes-api

REST API for posting and retrieving quotes with user authorization and storing it in-memory h2 database.

## Prerequisites
- Java 17
- Maven 3.6+

## Running Locally

### Command Line
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

### IntelliJ
1. Import as Maven project
2. Run `QuotesApiApplication.java`

## API Endpoints
2 Users :  
Username : admin  
password : admin123  
Username : reader  
password : reader123  

Note: These users are for local testing only. 
In production, export environment variables in startup script :
```bash
export ADMIN_USERNAME=prod_admin
export ADMIN_PASSWORD=secure_password
export READER_USERNAME=prod_reader
export READER_PASSWORD=reader_password
```
Or use AWS Secrets Manager or Parameter Store for sensitive credentials.

Also note that since IntelliJ Http Format comes with IntelliJ Ultimate, which I don't have (using community edition), 
hence providing cURL requests here.

### Create Quote (Admin only)
```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "Jeff Winger",
    "content": "I am always willing to go the extra mile to avoid doing something."
  }'
```
Expected Response (201 Created)
```json 
{
    "id": 1,
    "author": "Jeff Winger",
    "content": "I am always willing to go the extra mile to avoid doing something.",
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/quotes?author=Jeff%20Winger"
        }
    }
}
```

### Get All Quotes (Admin/Reader)
```bash
curl http://localhost:8080/api/quotes?page=0&size=10 \
  -H "Authorization: Basic $(echo -n 'reader:reader123' | base64)"
```
Expected Response (200 OK)
```json
{
    "_embedded": {
        "quoteResponseList": [
            {
                "id": 1,
                "author": "Jeff Winger",
                "content": "I am always willing to go the extra mile to avoid doing something."
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/quotes?page=0&size=10"
        }
    },
    "page": {
        "size": 10,
        "totalElements": 1,
        "totalPages": 1,
        "number": 0
    }
}
```

### Get Quotes by Author (Admin/Reader)
```bash
curl http://localhost:8080/api/quotes?author=Jeff%20Winger&page=0&size=10 \
  -H "Authorization: Basic $(echo -n 'reader:reader123' | base64)"
```
Expected Response (200 OK)
```json
{
    "_embedded": {
        "quoteResponseList": [
            {
                "id": 1,
                "author": "Jeff Winger",
                "content": "I am always willing to go the extra mile to avoid doing something."
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/api/quotes?author=Jeff%20Winger&page=0&size=10"
        }
    },
    "page": {
        "size": 10,
        "totalElements": 1,
        "totalPages": 1,
        "number": 0
    }
}
```


## Testing

### Run All Tests
```bash
mvn test
```

### Use h2-console to verify createdby information
Access at: http://localhost:8080/h2-console/

JDBC URL: jdbc:h2:mem:quotesdb  
Username: sa  
Password: (leave empty)



### Error Responses

#### Unauthorized (401) : Provide valid username password.
```bash
curl http://localhost:8080/api/quotes?page=0&size=10
```
```json
{
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication is required to access this resource",
    "timestamp": "2024-11-29T10:00:00.000Z"
}
```

#### Forbidden (403) : User with reader role cannot post quotes.
```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Authorization: Basic $(echo -n 'reader:reader123' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "Jeff Winger",
    "content": "I am always willing to go the extra mile to avoid doing something."
  }'
```
```json
{
    "status": 403,
    "error": "Forbidden",
    "message": "You don't have permission to access this resource",
    "timestamp": "2024-11-29T10:00:00.000Z"
}
```

#### Bad Request (400) : Author name and Quote content is mandatory to post a new quote.
```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "",
    "content": ""
  }'
```
```json
{
    "status": 400,
    "error": "Validation failed",
    "message": "{author=Author name is required, content=Quote content is required}",
    "timestamp": "2024-11-29T10:00:00.000Z"
}
```
#### Bad Request (400) : Author name max length is 50 characters, quote content max length 1000 characters.
```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Authorization: Basic $(echo -n 'admin:admin123' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "Jeff WingerJeff WingerJeff WingerJeff WingerJeff WingerJeff Winger",
    "content": "I am always willing to go the extra mile to avoid doing something."
  }'
```
```json
{
    "status": 400,
    "error": "Validation failed",
    "message": "{author=Author name must not exceed 50 characters}",
    "timestamp": "2024-11-29T00:04:24.264Z"
}
```

#### Not Found (404)
```bash
curl http://localhost:8080/api/quotes?author=NonexistentAuthor&page=0&size=10 \
  -H "Authorization: Basic $(echo -n 'reader:reader123' | base64)"
```
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "No quotes found for author: NonexistentAuthor",
    "timestamp": "2024-11-29T10:00:00.000Z"
}
```