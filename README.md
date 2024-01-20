# 📖 Book Store API 📖
## Description
Current API provides the ability to register, login and buy some books. This API supports CRUD (Create, Read, Update, Delete) operations with entities of the database. Admin user can add books, categories, update order status.
## Features
- JWT token to authenticate a user;
- Using roles of users to provide access to different API functions;
- Get a list of all available books;
- Get books by categories;
- Add books to order;
- Edit an order;
- Order status update;
- Get orders by status.
## Implemented technologies
- Maven
- MySQL
- Lombok
- Docker
- Java 17
- Mapstruct
- Liquibase
- Spring boot
- Spring security
- Test containers
- Jackson web token
## ENDPOINTS
## Available for all users:
- POST: /api/auth/register (To register a new user)
- POST: /api/auth/login (To get JWT tokens)
## Available for registered users
- GET: /api/books (To get all available books)
- GET: /api/books/{id} (To find book by id)
- GET: /api/books/search (To get books with certain parameters)
- GET: /api/categories (To get all categories)
- GET: /api/categories/{id} (To find category by id)
- GET: /api/categories/{id}/books (To get books by category id)
- GET: /api/orders (To view orders)
- POST: /api/orders (To place an order)
- GET: /api/orders/{orderId}/items (To get order items)
- GET: /api/orders/{orderId}/items/{itemId} (To get an order item by id)
- GET: /api/cart (To get a cart)
- POST: /api/cart (To add a book to the cart)
- PUT: /api/cart/cart-items/{id} (To update an order)
- DELETE: /api/cart/cart-items/{id} (To delete an order)
## Available for admin users
- POST: /api/books (To saves a new book to DB)
- DELETE: /api/books/{id} (To remove a book by id)
- PUT: /api/books/{id} (To update a certain book)
- POST: /api/categories (To add a new category)
- PUT: /api/categories/{id} (To update a category)
- DELETE: /api/categories/{id} (To delete a category)
- PATCH: /api/orders/{id} (To updates order status)
- GET: /api/orders/status/{status} (To get all orders by status)
## Important notice
Please note that endpoints with POST, PUT and PATCH methods require JSON body as an argument.
## API using steps
1. Upload this API to your server using docker.
2. Add book categories using admin user (please see an example below). This API implements liquibase so all needed tables will be created in the DB automatically after launching the API. Also, admin user will be added. Login: admin@user.com, password: 12345.
3. Add books to your book store (please see an example below).
4. Done. Now new users can register to your book store and order books. To see new orders you can use method GET and an endpoint /api/orders/status/pending (please see an example below). 
# Examples
## - Login
To log in you need to use a username (email) and a password of a registered user. On the screenshot below you can see how to log in as an admin
![Login example](https://drive.google.com/uc?export=view&id=1x81HiSEgzy0a47FeAv_MKUtAeiXaEjGU)
## - Add a new category
To add a new category you will need to send a json body as an argument
![Category creation example](https://drive.google.com/uc?export=view&id=1NrtbK3J_uJm1zRFTtRjn2MGdYJ2h6ROg)
## - Add a new book
To add a new book you will need to be logged in as an admin. You will also need to send a json body as an argument.
![Add book example](https://drive.google.com/uc?export=view&id=1_VwzwKV4bhBVkuRt8VqTwp9OFhQgh_aR)
## - Get orders by status
To get orders by status you will need to use get method and endpoint /api/orders/status/{status}. Status has the following states: pending - new orders, on_the_way - orders on the way to the user, delivered - delivered to the customer.
![Get orders by status example](https://drive.google.com/uc?export=view&id=1S7-jY_IhoAjqHNG0Hyu-i1Kr881ZXgLB)
# - Video presentation
[![Watch the video](https://img.youtube.com/vi/GrlVlm8mnhs/hqdefault.jpg)](https://www.youtube.com/watch?v=GrlVlm8mnhs)
