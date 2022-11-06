# Vending Machine


## Getting started

This service simulates a vending machine, with some endpoints to perform typical vending machine operations, like buy products or check the information of a given product.

This is an API, whose documentation can be checked by [checked here](./docs/mvp-vending-machine-api.pdf).

## Start the application

#### Running the app

```
 docker-compose build
 docker-compose up app
```

The above commands, will start build a docker container, starting the web service, on localhost, port 8081.

#### Supported endpoints

Please check [this documentation](./docs/mvp-vending-machine-api.pdf) to see all endpoints this service supports.

#### Calling the API

Before using the API, you must be authenticated into the service. To do that, you need to call the `/api/login` endpoint, providing the `username` and `password` fields.

Example:
```
POST http://localhost:8081/api/login

application/x-www-form-urlencoded
 - "username": "andre"
 - "password": "123"
}
```

The service will respond you an `access_token` and a `refresh_token`. You just need to grab the `access_token` and and the following configuration to the request header:

```
Authorization: Bearer <your_access_token>
```

The application already starts with some populated users into the service. So you can login into the already created users or create your own user for yourself. You just have to call the endpoint responsible to create a new user.

Example:
```
POST http://localhost:8081/user
Header: 
  Authorization: Bearer <your_access_token>
  
application/json
{
    "name": "John Doe",
    "username": "john_doe",
    "password": "this_is_my_password"
}
```


If you want to call the endpoint responsible to return all the current products, you can perform the following request:

```
GET http://localhost:8081/products
Header: 
  Authorization: Bearer <your_access_token>
```
