package com.example.onlinebookstore.dto;

public record BookSearchParametersDto(String[] titles, String[] prices, String[] isbns) {
}
